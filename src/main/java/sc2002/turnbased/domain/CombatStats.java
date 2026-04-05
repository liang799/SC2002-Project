package sc2002.turnbased.domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public record CombatStats(HitPoints hitPoints, Map<StatType, Stat> stats) {
    public CombatStats {
        Objects.requireNonNull(hitPoints, "hitPoints");
        Objects.requireNonNull(stats, "stats");

        EnumMap<StatType, Stat> copiedStats = new EnumMap<>(StatType.class);
        for (Map.Entry<StatType, Stat> entry : stats.entrySet()) {
            copiedStats.put(
                Objects.requireNonNull(entry.getKey(), "statType"),
                Objects.requireNonNull(entry.getValue(), "stat")
            );
        }
        stats = Map.copyOf(copiedStats);
    }

    public static CombatStats of(HitPoints hitPoints, Stat attack, Stat defense, Stat speed) {
        EnumMap<StatType, Stat> stats = new EnumMap<>(StatType.class);
        stats.put(StatType.ATTACK, attack);
        stats.put(StatType.DEFENSE, defense);
        stats.put(StatType.SPEED, speed);
        return new CombatStats(hitPoints, stats);
    }

    public int valueOf(StatType statType) {
        return stats.getOrDefault(statType, new Stat(0)).value();
    }

    public CombatStats withHitPoints(HitPoints updatedHitPoints) {
        return new CombatStats(updatedHitPoints, stats);
    }

    public CombatStats withStat(StatType type, Stat updatedStat) {
        EnumMap<StatType, Stat> newStats = new EnumMap<>(StatType.class);
        newStats.putAll(stats);
        newStats.put(type, updatedStat);
        return new CombatStats(hitPoints, Map.copyOf(newStats));
    }
}
