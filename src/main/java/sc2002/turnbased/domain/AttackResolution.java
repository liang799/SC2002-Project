package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.CombatantStatusOutcome;

public record AttackResolution(
    int attackUsed,
    int targetDefense,
    int hpBefore,
    int hpAfter,
    int damage,
    boolean targetEliminated,
    List<CombatantStatusOutcome> statusEffectOutcomes
) {
    public AttackResolution {
        statusEffectOutcomes = List.copyOf(Objects.requireNonNull(statusEffectOutcomes, "statusEffectOutcomes"));
    }

    public AttackResolution appendStatusEffectOutcomes(List<CombatantStatusOutcome> additionalOutcomes) {
        List<CombatantStatusOutcome> combinedOutcomes = new ArrayList<>(statusEffectOutcomes);
        combinedOutcomes.addAll(List.copyOf(Objects.requireNonNull(additionalOutcomes, "additionalOutcomes")));
        return new AttackResolution(attackUsed, targetDefense, hpBefore, hpAfter, damage, targetEliminated, combinedOutcomes);
    }
}
