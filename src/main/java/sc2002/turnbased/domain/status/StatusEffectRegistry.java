package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;

public class StatusEffectRegistry {
    private final List<StatusEffect> effects = new ArrayList<>();
    private final List<StatusEffectOutcome> pendingOutcomes = new ArrayList<>();

    public List<StatusEffectOutcome> add(Combatant owner, StatusEffect statusEffect) {
        Combatant effectOwner = Objects.requireNonNull(owner, "owner");
        StatusEffect effectToAdd = Objects.requireNonNull(statusEffect, "statusEffect");
        recordOutcomes(pruneExpiredEffects(effectOwner));

        for (int index = 0; index < effects.size(); index++) {
            StatusEffect existingEffect = effects.get(index);
            Optional<StatusEffect> mergedEffect = merge(existingEffect, effectToAdd);
            if (mergedEffect.isPresent()) {
                effects.set(index, mergedEffect.get());
                recordOutcomes(mergedEffect.get().onApply(effectOwner));
                return consumeOutcomes();
            }
        }

        effects.add(effectToAdd);
        recordOutcomes(effectToAdd.onApply(effectOwner));
        return consumeOutcomes();
    }

    public DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        List<DamageModifier> modifiers = new ArrayList<>();
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            DamageAdjustment adjustment = statusEffect.modifyIncomingDamage(owner, attacker, damage);
            modifiers.addAll(adjustment.modifiers());
            damage = adjustment.damage();
            if (statusEffect.isExpired()) {
                recordOutcomes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        damage = Math.max(0, damage);
        return new DamageAdjustment(damage, modifiers);
    }

    public Optional<String> getTurnBlockReason(Combatant owner) {
        Optional<String> blockReason = Optional.empty();
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (blockReason.isEmpty()) {
                blockReason = statusEffect.getTurnBlockReason(owner);
            }
            if (statusEffect.isExpired()) {
                recordOutcomes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        return blockReason;
    }

    public List<StatusEffectOutcome> completeRound(Combatant owner) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            recordOutcomes(statusEffect.onRoundEnd(owner));
            if (statusEffect.isExpired()) {
                recordOutcomes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        return consumeOutcomes();
    }

    public List<String> activeStatuses(Combatant owner) {
        Combatant effectOwner = Objects.requireNonNull(owner, "owner");
        pruneExpiredEffects(effectOwner);
        if (!effectOwner.isAlive()) {
            return List.of();
        }

        return effects.stream()
            .filter(statusEffect -> !statusEffect.isExpired())
            .map(StatusEffect::description)
            .toList();
    }

    public CombatStats apply(Combatant owner, CombatStats stats) {
        Combatant effectOwner = Objects.requireNonNull(owner, "owner");
        pruneExpiredEffects(effectOwner);

        CombatStats effectiveStats = Objects.requireNonNull(stats, "stats");
        for (StatusEffect statusEffect : effects) {
            effectiveStats = statusEffect.modifyStats(effectiveStats);
        }
        return effectiveStats;
    }

    public List<StatusEffectOutcome> consumeOutcomes() {
        List<StatusEffectOutcome> outcomes = List.copyOf(pendingOutcomes);
        pendingOutcomes.clear();
        return outcomes;
    }

    private List<StatusEffectOutcome> pruneExpiredEffects(Combatant owner) {
        List<StatusEffectOutcome> expiryOutcomes = new ArrayList<>();
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect.isExpired()) {
                expiryOutcomes.addAll(List.copyOf(Objects.requireNonNull(statusEffect.onExpire(owner), "outcomes")));
                iterator.remove();
            }
        }
        return List.copyOf(expiryOutcomes);
    }

    private void recordOutcomes(List<? extends StatusEffectOutcome> outcomes) {
        pendingOutcomes.addAll(List.copyOf(Objects.requireNonNull(outcomes, "outcomes")));
    }

    private Optional<StatusEffect> merge(StatusEffect existingEffect, StatusEffect incomingEffect) {
        Optional<StatusEffect> mergedEffect = existingEffect.mergeWith(incomingEffect);
        if (mergedEffect.isPresent()) {
            return mergedEffect;
        }
        return incomingEffect.mergeWith(existingEffect);
    }
}
