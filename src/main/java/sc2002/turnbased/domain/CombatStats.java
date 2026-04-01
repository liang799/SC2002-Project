package sc2002.turnbased.domain;

public record CombatStats(int maxHp, int attack, int defense, int speed) {
    public CombatStats {
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be positive");
        }
        if (attack < 0) {
            throw new IllegalArgumentException("attack cannot be negative");
        }
        if (defense < 0) {
            throw new IllegalArgumentException("defense cannot be negative");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("speed cannot be negative");
        }
    }
}
