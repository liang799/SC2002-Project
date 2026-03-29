package sc2002.turnbased.domain;

public interface StatusEffect {
    String getName();

    TurnEffectResolution onTurnOpportunity();

    boolean isExpired();
}
