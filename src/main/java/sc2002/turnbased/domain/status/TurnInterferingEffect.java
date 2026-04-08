package sc2002.turnbased.domain.status;

public interface TurnInterferingEffect {
    default TurnEffectResolution onTurnOpportunity() {
        return TurnEffectResolution.allow();
    }
}
