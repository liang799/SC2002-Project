package sc2002.turnbased.domain.status;

public record DamageAdjustment(int damage) {
    public static DamageAdjustment unchanged(int damage) {
        return new DamageAdjustment(damage);
    }
}
