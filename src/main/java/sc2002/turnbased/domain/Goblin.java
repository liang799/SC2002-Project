package sc2002.turnbased.domain;

public class Goblin extends EnemyCombatant {
    private static final HitPoints GOBLIN_HIT_POINTS = HitPoints.full(55);
    private static final CombatStats GOBLIN_STATS = CombatStats.builder()
        .attack(35)
        .defense(15)
        .speed(25)
        .build();

    public Goblin(String name) {
        super(name, GOBLIN_HIT_POINTS, GOBLIN_STATS);
    }
}
