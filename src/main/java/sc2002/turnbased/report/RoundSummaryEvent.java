package sc2002.turnbased.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sc2002.turnbased.domain.ItemType;

public class RoundSummaryEvent implements BattleEvent {
    private final int roundNumber;
    private final CombatantSummary playerSummary;
    private final List<CombatantSummary> enemySummaries;
    private final Map<ItemType, Integer> inventorySnapshot;
    private final int specialSkillCooldown;

    public RoundSummaryEvent(
        int roundNumber,
        CombatantSummary playerSummary,
        List<CombatantSummary> enemySummaries,
        Map<ItemType, Integer> inventorySnapshot,
        int specialSkillCooldown
    ) {
        this.roundNumber = roundNumber;
        this.playerSummary = playerSummary;
        this.enemySummaries = new ArrayList<>(enemySummaries);
        this.inventorySnapshot = inventorySnapshot;
        this.specialSkillCooldown = specialSkillCooldown;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public CombatantSummary getPlayerSummary() {
        return playerSummary;
    }

    public List<CombatantSummary> getEnemySummaries() {
        return Collections.unmodifiableList(enemySummaries);
    }

    public Map<ItemType, Integer> getInventorySnapshot() {
        return Collections.unmodifiableMap(inventorySnapshot);
    }

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }
}
