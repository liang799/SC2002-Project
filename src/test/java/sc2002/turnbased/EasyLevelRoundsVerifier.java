package sc2002.turnbased;

import java.util.List;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.EasyLevelSetup;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.ScriptedDecisionProvider;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.RoundSummaryEvent;

public class EasyLevelRoundsVerifier {
    public static void main(String[] args) {
        BattleSetup battleSetup = EasyLevelSetup.createWarriorPotionSmokeBombSetup();
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());

        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, new PlayerDecision(new BasicAttackAction(), "Goblin A"))
            .addDecision(2, new PlayerDecision(new ShieldBashAction(), "Goblin A"))
            .addDecision(3, new PlayerDecision(new BasicAttackAction(), "Goblin A"));

        List<BattleEvent> events = battleEngine.runRounds(3, decisions);
        List<RoundSummaryEvent> summaries = events.stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .toList();

        assertSummary(summaries.get(0), 215, 30, 55, 55, 0, false);
        assertSummary(summaries.get(1), 185, 5, 55, 55, 3, true);
        assertSummary(summaries.get(2), 155, 0, 55, 55, 2, false);
    }

    private static void assertSummary(
        RoundSummaryEvent summary,
        int expectedWarriorHp,
        int expectedGoblinAHp,
        int expectedGoblinBHp,
        int expectedGoblinCHp,
        int expectedCooldown,
        boolean goblinAStunned
    ) {
        require(summary.getPlayerSummary().getCurrentHp() == expectedWarriorHp, "Unexpected Warrior HP");
        require(summary.getEnemySummaries().get(0).getCurrentHp() == expectedGoblinAHp, "Unexpected Goblin A HP");
        require(summary.getEnemySummaries().get(1).getCurrentHp() == expectedGoblinBHp, "Unexpected Goblin B HP");
        require(summary.getEnemySummaries().get(2).getCurrentHp() == expectedGoblinCHp, "Unexpected Goblin C HP");
        require(summary.getSpecialSkillCooldown() == expectedCooldown, "Unexpected cooldown");
        require(summary.getInventorySnapshot().getOrDefault(ItemType.POTION, 0) == 1, "Potion count changed");
        require(summary.getInventorySnapshot().getOrDefault(ItemType.SMOKE_BOMB, 0) == 1, "Smoke Bomb count changed");

        boolean hasStun = summary.getEnemySummaries().get(0).getActiveStatuses().contains("STUNNED");
        require(hasStun == goblinAStunned, "Unexpected Goblin A stun state");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
