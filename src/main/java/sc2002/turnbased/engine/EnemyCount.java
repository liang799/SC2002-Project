package sc2002.turnbased.engine;

import java.util.Objects;

public record EnemyCount(EnemyFactory enemyFactory, int count) {
    public EnemyCount {
        Objects.requireNonNull(enemyFactory, "enemyFactory");
        if (count < 0) {
            throw new IllegalArgumentException(enemyFactory.getDisplayName() + " count cannot be negative, got: " + count);
        }
        if (count > enemyFactory.getMaxPerWave()) {
            throw new IllegalArgumentException(
                enemyFactory.getDisplayName() + " count cannot exceed " + enemyFactory.getMaxPerWave() + ", got: " + count
            );
        }
    }

    public static EnemyCount of(EnemyFactory enemyFactory, int count) {
        return new EnemyCount(enemyFactory, count);
    }

    public String describe() {
        return enemyFactory.formatCount(count);
    }
}
