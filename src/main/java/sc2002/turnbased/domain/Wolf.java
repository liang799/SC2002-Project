package sc2002.turnbased.domain;

public class Wolf extends EnemyCombatant {
    private static final HitPoints WOLF_HIT_POINTS = HitPoints.full(40);
    private static final CombatStats WOLF_STATS = CombatStats.builder()
        .attack(45)
        .defense(5)
        .speed(35)
        .build();

    public Wolf(String name) {
        super(name, WOLF_HIT_POINTS, WOLF_STATS);
    }
}
