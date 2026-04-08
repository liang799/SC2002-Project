package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.Combatant;

public interface TurnInterferingEffect {
    default TurnEffectResolution onTurnOpportunity(Combatant owner, StatusEffectEventPublisher eventPublisher) {
        return TurnEffectResolution.allow();
    }
}
