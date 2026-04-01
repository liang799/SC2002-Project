package sc2002.turnbased.domain;

public class Goblin extends EnemyCombatant {
    private static final CombatStats GOBLIN_STATS = new CombatStats(55, 35, 15, 25);

    public Goblin(String name) {
        super(name, GOBLIN_STATS);
    }
}
