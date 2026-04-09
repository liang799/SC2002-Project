package sc2002.turnbased.domain.status;

import java.util.Objects;

import sc2002.turnbased.domain.CombatantId;

public record CombatantStatusOutcome(CombatantId combatantId, StatusEffectOutcome outcome) {
    public CombatantStatusOutcome {
        Objects.requireNonNull(combatantId, "combatantId");
        Objects.requireNonNull(outcome, "outcome");
    }
}
