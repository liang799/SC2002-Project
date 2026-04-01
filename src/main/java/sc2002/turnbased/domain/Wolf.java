package sc2002.turnbased.domain;

public class Wolf extends EnemyCombatant {
    private static final CombatStats WOLF_STATS = new CombatStats(40, 45, 5, 35);

    public Wolf(String name) {
        super(name, WOLF_STATS);
    }
}
