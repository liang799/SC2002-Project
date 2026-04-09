package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatStats;

public interface StatusEffect {
    StatusEffectKind kind();

    String description();

    default Optional<StatusEffect> mergeWith(StatusEffect other) {
        Objects.requireNonNull(other, "other");
        return Optional.empty();
    }

    default List<StatusEffectOutcome> onApply(Combatant owner) {
        Objects.requireNonNull(owner, "owner");
        return List.of();
    }

    default List<StatusEffectOutcome> onExpire(Combatant owner) {
        Objects.requireNonNull(owner, "owner");
        return List.of();
    }

    default List<StatusEffectOutcome> onRoundEnd(Combatant owner) {
        Objects.requireNonNull(owner, "owner");
        return List.of();
    }

    default CombatStats modifyStats(CombatStats stats) {
        return Objects.requireNonNull(stats, "stats");
    }

    default DamageAdjustment modifyIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(attacker, "attacker");
        return DamageAdjustment.unchanged(damage);
    }

    default Optional<String> getTurnBlockReason(Combatant owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.empty();
    }

    boolean isExpired();
}
