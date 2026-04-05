package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StatusEffectRegistry {
    private final List<StatusEffect> effects = new ArrayList<>();

    public void add(StatusEffect statusEffect) {
        effects.add(Objects.requireNonNull(statusEffect, "statusEffect"));
    }

    public int adjustIncomingDamage(Combatant owner, Combatant attacker, int damage, List<String> notes) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            damage = statusEffect.adjustIncomingDamage(owner, attacker, damage, notes);
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }
        return damage;
    }

    public TurnWindow resolveTurnWindow() {
        boolean blocked = false;
        String blockerLabel = null;
        List<String> notes = new ArrayList<>();

        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            TurnEffectResolution resolution = statusEffect.onTurnOpportunity();
            if (resolution.blocksAction() && blockerLabel == null) {
                blocked = true;
                blockerLabel = resolution.blockerLabel();
            }
            notes.addAll(resolution.notes());
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }

        return new TurnWindow(blocked, blockerLabel, notes);
    }

    public void onRoundCompleted() {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            statusEffect.onRoundCompleted();
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }
    }

    public List<String> activeStatusNames(boolean ownerAlive) {
        pruneExpiredEffects();
        if (!ownerAlive) {
            return List.of();
        }

        List<String> names = new ArrayList<>();
        for (StatusEffect statusEffect : effects) {
            names.add(statusEffect.getName());
        }
        return names;
    }

    public int modifierFor(StatType statType) {
        pruneExpiredEffects();

        int modifier = 0;
        for (StatusEffect statusEffect : effects) {
            modifier += statusEffect.statModifier(statType);
        }
        return modifier;
    }

    private void pruneExpiredEffects() {
        effects.removeIf(StatusEffect::isExpired);
    }
}
