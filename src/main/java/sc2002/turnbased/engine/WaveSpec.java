package sc2002.turnbased.engine;
public record WaveSpec(int goblinCount, int wolfCount) {
    public WaveSpec {
        if (goblinCount < 0) {
            throw new IllegalArgumentException("goblinCount cannot be negative, got: " + goblinCount);
        }
        if (wolfCount < 0) {
            throw new IllegalArgumentException("wolfCount cannot be negative, got: " + wolfCount);
        }
        if (goblinCount > 3) {
            throw new IllegalArgumentException("goblinCount cannot exceed 3, got: " + goblinCount);
        }
        if (wolfCount > 3) {
            throw new IllegalArgumentException("wolfCount cannot exceed 3, got: " + wolfCount);
        }
        int total = goblinCount + wolfCount;
        if (total == 0) {
            throw new IllegalArgumentException("A wave must have at least 1 enemy");
        }
        if (total > 4) {
            throw new IllegalArgumentException(
                "A wave cannot have more than 4 enemies total, got: " + total
                    + " (" + goblinCount + " goblins + " + wolfCount + " wolves)"
            );
        }
    }
    public int totalEnemies() {
        return goblinCount + wolfCount;
    }
}