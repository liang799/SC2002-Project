package sc2002.turnbased.domain;

public class Goblin extends EnemyCombatant {
    private static final CombatStats GOBLIN_STATS = new CombatStats(
        HitPoints.full(55),
        new Stat(35),
        new Stat(15),
        new Stat(25)
    );

    public Goblin(String name) {
        super(name, GOBLIN_STATS);
    }
}
