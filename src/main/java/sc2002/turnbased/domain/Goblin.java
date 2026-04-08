package sc2002.turnbased.domain;

public class Goblin extends EnemyCombatant {
    private static final HitPoints GOBLIN_HIT_POINTS = HitPoints.full(55);
    private static final CombatStats GOBLIN_STATS = CombatStats.of(
        new Stat(35),
        new Stat(15),
        new Stat(25)
    );

    public Goblin(String name) {
        super(name, GOBLIN_HIT_POINTS, GOBLIN_STATS);
    }
}
