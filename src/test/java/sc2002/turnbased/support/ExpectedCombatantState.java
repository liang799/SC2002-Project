package sc2002.turnbased.support;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record ExpectedCombatantState(String name, int hp, boolean alive, Set<String> statuses) {
    public ExpectedCombatantState {
        Objects.requireNonNull(name, "name");
        statuses = Set.copyOf(Objects.requireNonNull(statuses, "statuses"));
    }

    public static ExpectedCombatantState enemy(String name, int hp) {
        return new ExpectedCombatantState(name, hp, hp > 0, Set.of());
    }

    public ExpectedCombatantState stunned() {
        return withStatus("STUNNED");
    }

    public ExpectedCombatantState withStatus(String status) {
        LinkedHashSet<String> updatedStatuses = new LinkedHashSet<>(statuses);
        updatedStatuses.add(Objects.requireNonNull(status, "status"));
        return new ExpectedCombatantState(name, hp, alive, updatedStatuses);
    }

    public ExpectedCombatantState withStatuses(String... updatedStatuses) {
        return new ExpectedCombatantState(name, hp, alive, Set.of(updatedStatuses));
    }
}
