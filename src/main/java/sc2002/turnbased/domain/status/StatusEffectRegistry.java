package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;

public class StatusEffectRegistry {
    private final List<StatusEffect> effects = new ArrayList<>();
    private final StatusEffectEventPublisher eventPublisher;

    public StatusEffectRegistry(StatusEffectEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
    }

    public void add(Combatant owner, StatusEffect statusEffect) {
        Combatant effectOwner = Objects.requireNonNull(owner, "owner");
        StatusEffect effectToAdd = Objects.requireNonNull(statusEffect, "statusEffect");
        pruneExpiredEffects(effectOwner);

        for (int index = 0; index < effects.size(); index++) {
            StatusEffect existingEffect = effects.get(index);
            if (existingEffect instanceof MergeableStatusEffect mergeableEffect
                && mergeableEffect.canMergeWith(effectToAdd)) {
                StatusEffect mergedEffect = mergeableEffect.merge(effectToAdd);
                effects.set(index, mergedEffect);
                mergedEffect.onRegistered(effectOwner.getName(), eventPublisher);
                return;
            }
            if (effectToAdd instanceof MergeableStatusEffect mergeableEffectToAdd
                && mergeableEffectToAdd.canMergeWith(existingEffect)) {
                StatusEffect mergedEffect = mergeableEffectToAdd.merge(existingEffect);
                effects.set(index, mergedEffect);
                mergedEffect.onRegistered(effectOwner.getName(), eventPublisher);
                return;
            }
        }

        effects.add(effectToAdd);
        effectToAdd.onRegistered(effectOwner.getName(), eventPublisher);
    }

    public DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect instanceof IncomingDamageModifierEffect modifierEffect) {
                DamageAdjustment adjustment = modifierEffect.adjustIncomingDamage(owner, attacker, damage, eventPublisher);
                damage = adjustment.damage();
            }
            if (statusEffect.isExpired()) {
                statusEffect.onExpired(owner.getName(), eventPublisher);
                iterator.remove();
            }
        }
        return new DamageAdjustment(damage);
    }

    public TurnWindow resolveTurnWindow(Combatant owner) {
        boolean blocked = false;
        String blockerLabel = null;

        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect instanceof TurnInterferingEffect turnInterferingEffect) {
                TurnEffectResolution resolution = turnInterferingEffect.onTurnOpportunity(owner, eventPublisher);
                if (resolution.blocksAction() && blockerLabel == null) {
                    blocked = true;
                    blockerLabel = resolution.blockerLabel();
                }
            }
            if (statusEffect.isExpired()) {
                statusEffect.onExpired(owner.getName(), eventPublisher);
                iterator.remove();
            }
        }

        return new TurnWindow(blocked, blockerLabel);
    }

    public void onRoundCompleted(Combatant owner) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            statusEffect.onRoundCompleted();
            if (statusEffect.isExpired()) {
                statusEffect.onExpired(owner.getName(), eventPublisher);
                iterator.remove();
            }
        }
    }

    public List<String> activeStatusNames(String ownerName, boolean ownerAlive) {
        pruneExpiredEffects(ownerName);
        if (!ownerAlive) {
            return List.of();
        }

        List<String> names = new ArrayList<>();
        for (StatusEffect statusEffect : effects) {
            names.add(statusEffect.name());
        }
        return names;
    }

    public CombatStats apply(String ownerName, CombatStats stats) {
        pruneExpiredEffects(ownerName);

        CombatStats effectiveStats = Objects.requireNonNull(stats, "stats");
        for (StatusEffect statusEffect : effects) {
            if (statusEffect instanceof StatModifierEffect modifierEffect) {
                effectiveStats = modifierEffect.modifyStats(effectiveStats);
            }
        }
        return effectiveStats;
    }

    public StatusEffectObservationScope openObservation() {
        return new StatusEffectObservationScope(eventPublisher);
    }

    private void pruneExpiredEffects(Combatant owner) {
        pruneExpiredEffects(owner.getName());
    }

    private void pruneExpiredEffects(String ownerName) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect.isExpired()) {
                statusEffect.onExpired(ownerName, eventPublisher);
                iterator.remove();
            }
        }
    }
}
