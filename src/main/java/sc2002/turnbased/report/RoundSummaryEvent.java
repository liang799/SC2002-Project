package sc2002.turnbased.report;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sc2002.turnbased.domain.ItemType;

public record RoundSummaryEvent(
    int roundNumber,
    CombatantSummary playerSummary,
    List<CombatantSummary> enemySummaries,
    Map<ItemType, Integer> inventorySnapshot,
    int specialSkillCooldown
) implements BattleEvent {
    public RoundSummaryEvent {
        playerSummary = Objects.requireNonNull(playerSummary, "playerSummary");
        enemySummaries = List.copyOf(Objects.requireNonNull(enemySummaries, "enemySummaries"));
        inventorySnapshot = Collections.unmodifiableMap(new LinkedHashMap<>(
            Objects.requireNonNull(inventorySnapshot, "inventorySnapshot")
        ));
    }

    @Override
    public <T> T visit(Visitor<T> visitor) {
        return visitor.onRoundSummary(this);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public CombatantSummary getPlayerSummary() {
        return playerSummary;
    }

    public List<CombatantSummary> getEnemySummaries() {
        return enemySummaries;
    }

    public Map<ItemType, Integer> getInventorySnapshot() {
        return inventorySnapshot;
    }

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }
}
