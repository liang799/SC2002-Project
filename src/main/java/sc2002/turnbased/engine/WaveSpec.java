package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record WaveSpec(List<EnemyCount> enemyCounts) {
    private static final int MAX_TOTAL_ENEMIES = 4;

    public WaveSpec {
        Objects.requireNonNull(enemyCounts, "enemyCounts");
        enemyCounts = List.copyOf(new ArrayList<>(enemyCounts));

        Set<EnemyFactory> seenFactories = new HashSet<>();
        int total = 0;
        for (EnemyCount enemyCount : enemyCounts) {
            Objects.requireNonNull(enemyCount, "enemyCounts entry cannot be null");
            if (!seenFactories.add(enemyCount.enemyFactory())) {
                throw new IllegalArgumentException(
                    "Duplicate enemy type in wave: " + enemyCount.enemyFactory().getDisplayName()
                );
            }
            total += enemyCount.count();
        }

        if (total == 0) {
            throw new IllegalArgumentException("A wave must have at least 1 enemy");
        }
        if (total > MAX_TOTAL_ENEMIES) {
            throw new IllegalArgumentException(
                "A wave cannot have more than 4 enemies total, got: " + total
                    + " (" + describeCounts(enemyCounts) + ")"
            );
        }
    }

    public static WaveSpec of(EnemyCount... enemyCounts) {
        return new WaveSpec(List.of(enemyCounts));
    }

    public int totalEnemies() {
        return enemyCounts.stream().mapToInt(EnemyCount::count).sum();
    }

    public int countOf(EnemyFactory enemyFactory) {
        Objects.requireNonNull(enemyFactory, "enemyFactory");
        return enemyCounts.stream()
            .filter(enemyCount -> enemyCount.enemyFactory().equals(enemyFactory))
            .mapToInt(EnemyCount::count)
            .sum();
    }

    public String describe() {
        return describeCounts(enemyCounts);
    }

    private static String describeCounts(List<EnemyCount> enemyCounts) {
        return enemyCounts.stream()
            .filter(enemyCount -> enemyCount.count() > 0)
            .map(EnemyCount::describe)
            .collect(Collectors.joining(", "));
    }
}
