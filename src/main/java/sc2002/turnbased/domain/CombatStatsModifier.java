package sc2002.turnbased.domain;

import java.util.Objects;

@FunctionalInterface
public interface CombatStatsModifier {
    CombatStats applyTo(CombatStats stats);

    default CombatStatsModifier andThen(CombatStatsModifier next) {
        Objects.requireNonNull(next, "next");
        return stats -> next.applyTo(applyTo(stats));
    }

    static CombatStatsModifier identity() {
        return stats -> stats;
    }
}
