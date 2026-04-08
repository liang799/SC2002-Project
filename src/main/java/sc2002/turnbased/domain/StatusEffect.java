package sc2002.turnbased.domain;

public interface StatusEffect {
    String getName();

    TurnEffectResolution onTurnOpportunity();

    default CombatStats modifyStats(CombatStats stats) {
        return stats;
    }

    default int adjustIncomingDamage(Combatant owner, Combatant attacker, int damage, java.util.List<String> notes) {
        return damage;
    }

    default void onRoundCompleted() {
    }

    boolean isExpired();
}
