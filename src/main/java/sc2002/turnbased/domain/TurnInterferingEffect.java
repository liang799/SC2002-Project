package sc2002.turnbased.domain;

public interface TurnInterferingEffect {
    default TurnEffectResolution onTurnOpportunity() {
        return TurnEffectResolution.allow();
    }
}
