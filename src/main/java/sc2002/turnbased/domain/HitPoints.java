package sc2002.turnbased.domain;

public record HitPoints(int current, int max) {
    public HitPoints {
        if (max <= 0) {
            throw new IllegalArgumentException("Maximum HP must be greater than 0");
        }
        if (current < 0) {
            throw new IllegalArgumentException("Current HP cannot be negative");
        }
        if (current > max) {
            throw new IllegalArgumentException("Current HP cannot exceed maximum HP");
        }
    }

    public static HitPoints full(int maxHp) {
        return new HitPoints(maxHp, maxHp);
    }

    public HitPoints takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        int newCurrent = Math.max(0, this.current - damage);
        return new HitPoints(newCurrent, this.max);
    }

    public HitPoints heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Healing amount cannot be negative");
        }
        int newCurrent = Math.min(this.max, this.current + amount);
        return new HitPoints(newCurrent, this.max);
    }

    public boolean isDead() {
        return this.current == 0;
    }

    public double getHealthPercentage() {
        return ((double) this.current / this.max) * 100.0;
    }
}
