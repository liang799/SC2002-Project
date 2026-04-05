package sc2002.turnbased.domain;

public record Stat(int value) {
    public Stat {
        if (value < 0) {
            throw new IllegalArgumentException("value cannot be negative");
        }
    }

    public Stat adjustBy(int amount) {
        return new Stat(Math.addExact(value, amount));
    }
}
