package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;

public class StatusEffectRegistry {
    private final List<StatusEffect> effects = new ArrayList<>();

    public void add(StatusEffect statusEffect) {
        StatusEffect effectToAdd = Objects.requireNonNull(statusEffect, "statusEffect");
        pruneExpiredEffects();

        for (int index = 0; index < effects.size(); index++) {
            StatusEffect existingEffect = effects.get(index);
            if (existingEffect instanceof MergeableStatusEffect mergeableEffect
                && mergeableEffect.canMergeWith(effectToAdd)) {
                effects.set(index, mergeableEffect.merge(effectToAdd));
                return;
            }
            if (effectToAdd instanceof MergeableStatusEffect mergeableEffectToAdd
                && mergeableEffectToAdd.canMergeWith(existingEffect)) {
                effects.set(index, mergeableEffectToAdd.merge(existingEffect));
                return;
            }
        }

        effects.add(effectToAdd);
    }

    public DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        List<String> notes = new ArrayList<>();
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect instanceof IncomingDamageModifierEffect modifierEffect) {
                DamageAdjustment adjustment = modifierEffect.adjustIncomingDamage(owner, attacker, damage);
                damage = adjustment.damage();
                notes.addAll(adjustment.notes());
            }
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }
        return new DamageAdjustment(damage, notes);
    }

    public TurnWindow resolveTurnWindow() {
        boolean blocked = false;
        String blockerLabel = null;
        List<String> notes = new ArrayList<>();

        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect instanceof TurnInterferingEffect turnInterferingEffect) {
                TurnEffectResolution resolution = turnInterferingEffect.onTurnOpportunity();
                if (resolution.blocksAction() && blockerLabel == null) {
                    blocked = true;
                    blockerLabel = resolution.blockerLabel();
                }
                notes.addAll(resolution.notes());
            }
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
            names.add(statusEffect.name());
        }
        return names;
    }

    public CombatStats apply(CombatStats stats) {
        pruneExpiredEffects();

        CombatStats effectiveStats = Objects.requireNonNull(stats, "stats");
        for (StatusEffect statusEffect : effects) {
            if (statusEffect instanceof StatModifierEffect modifierEffect) {
                effectiveStats = modifierEffect.modifyStats(effectiveStats);
            }
        }
        return effectiveStats;
    }

    private void pruneExpiredEffects() {
        effects.removeIf(StatusEffect::isExpired);
    }
}
