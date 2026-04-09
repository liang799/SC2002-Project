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
    private final List<String> pendingNotes = new ArrayList<>();

    public List<String> add(Combatant owner, StatusEffect statusEffect) {
        Combatant effectOwner = Objects.requireNonNull(owner, "owner");
        StatusEffect effectToAdd = Objects.requireNonNull(statusEffect, "statusEffect");
        pruneExpiredEffects(effectOwner);

        for (int index = 0; index < effects.size(); index++) {
            StatusEffect existingEffect = effects.get(index);
            Optional<StatusEffect> mergedEffect = merge(existingEffect, effectToAdd);
            if (mergedEffect.isPresent()) {
                effects.set(index, mergedEffect.get());
                recordNotes(mergedEffect.get().onApply(effectOwner));
                return consumeNotes();
            }
        }

        effects.add(effectToAdd);
        recordNotes(effectToAdd.onApply(effectOwner));
        return consumeNotes();
    }

    public DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            DamageAdjustment adjustment = statusEffect.modifyIncomingDamage(owner, attacker, damage);
            recordNotes(adjustment.notes());
            damage = adjustment.damage();
            if (statusEffect.isExpired()) {
                recordNotes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        return new DamageAdjustment(damage, consumeNotes());
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
                recordNotes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        return blockReason;
    }

    public List<String> completeRound(Combatant owner) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            recordNotes(statusEffect.onRoundEnd(owner));
            if (statusEffect.isExpired()) {
                recordNotes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
        return consumeNotes();
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

    public List<String> consumeNotes() {
        List<String> notes = List.copyOf(pendingNotes);
        pendingNotes.clear();
        return notes;
    }

    private void pruneExpiredEffects(Combatant owner) {
        Iterator<StatusEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            if (statusEffect.isExpired()) {
                recordNotes(statusEffect.onExpire(owner));
                iterator.remove();
            }
        }
    }

    private void recordNotes(List<String> notes) {
        pendingNotes.addAll(List.copyOf(Objects.requireNonNull(notes, "notes")));
    }

    private Optional<StatusEffect> merge(StatusEffect existingEffect, StatusEffect incomingEffect) {
        Optional<StatusEffect> mergedEffect = existingEffect.mergeWith(incomingEffect);
        if (mergedEffect.isPresent()) {
            return mergedEffect;
        }
        return incomingEffect.mergeWith(existingEffect);
    }
}
