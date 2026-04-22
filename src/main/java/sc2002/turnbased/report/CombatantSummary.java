package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.CombatantId;

public record CombatantSummary(
    CombatantId combatantId,
    String name,
    int currentHp,
    int maxHp,
    int currentAttack,
    int baseAttack,
    boolean alive,
    List<String> activeStatuses
) {
    public CombatantSummary {
        combatantId = Objects.requireNonNull(combatantId, "combatantId");
        name = Objects.requireNonNull(name, "name");
        activeStatuses = List.copyOf(Objects.requireNonNull(activeStatuses, "activeStatuses"));
    }

    public CombatantId getCombatantId() {
        return combatantId;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentAttack() {
        return currentAttack;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public boolean isAlive() {
        return alive;
    }

    public List<String> getActiveStatuses() {
        return activeStatuses;
    }
}
