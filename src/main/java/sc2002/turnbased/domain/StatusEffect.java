package sc2002.turnbased.domain;

public interface StatusEffect {
    String getName();

    TurnEffectResolution onTurnOpportunity();

    default int getDefenseModifier() {
        return 0;
    }

    default int adjustIncomingDamage(Combatant owner, Combatant attacker, int damage, java.util.List<String> notes) {
        return damage;
    }

    default void onRoundCompleted() {
    }

    boolean isExpired();
}
