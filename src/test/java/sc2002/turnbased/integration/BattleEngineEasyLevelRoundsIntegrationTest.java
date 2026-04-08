package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.EasyLevelSetup;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.ScriptedDecisionProvider;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.support.BattleTestSupport;

@Tag("integration")
class BattleEngineEasyLevelRoundsIntegrationTest {
    @Test
    @DisplayName("BattleEngine and easy warrior opening script produce deterministic round summaries")
    void battleEngine_andEasyWarriorOpeningScript_produceDeterministicRoundSummaries() {
        BattleSetup battleSetup = EasyLevelSetup.createWarriorPotionSmokeBombSetup();
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());

        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), "Goblin A"))
            .addDecision(2, PlayerDecision.targeted(new UseSpecialSkillAction(), "Goblin A"))
            .addDecision(3, PlayerDecision.targeted(new BasicAttackAction(), "Goblin A"));

        List<RoundSummaryEvent> summaries = battleEngine.runRounds(3, decisions).stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .toList();

        assertEquals(3, summaries.size());
        assertRoundSummary(summaries.get(0), 215, 0, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 30, true, false),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRoundSummary(summaries.get(1), 185, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 5, true, true),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRoundSummary(summaries.get(2), 155, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
    }

    private static void assertRoundSummary(
        RoundSummaryEvent summary,
        int expectedPlayerHp,
        int expectedCooldown,
        Map<ItemType, Integer> expectedInventory,
        EnemyExpectation... expectedEnemies
    ) {
        assertAll(
            () -> assertEquals(expectedPlayerHp, summary.getPlayerSummary().getCurrentHp(), "Unexpected Warrior HP"),
            () -> assertEquals(expectedCooldown, summary.getSpecialSkillCooldown(), "Unexpected cooldown"),
            () -> expectedInventory.forEach((itemType, expectedCount) ->
                assertEquals(expectedCount, summary.getInventorySnapshot().getOrDefault(itemType, 0), "Unexpected count for " + itemType))
        );

        for (EnemyExpectation expectedEnemy : expectedEnemies) {
            CombatantSummary enemySummary = BattleTestSupport.findEnemy(summary, expectedEnemy.name());
            assertAll(
                () -> assertEquals(expectedEnemy.hp(), enemySummary.getCurrentHp(), "Unexpected HP for " + expectedEnemy.name()),
                () -> assertEquals(expectedEnemy.alive(), enemySummary.isAlive(), "Unexpected alive state for " + expectedEnemy.name()),
                () -> assertEquals(
                    expectedEnemy.stunned(),
                    enemySummary.getActiveStatuses().contains("STUNNED"),
                    "Unexpected stun state for " + expectedEnemy.name()
                )
            );
        }
    }

    private static EnemyExpectation enemy(String name, int hp, boolean alive, boolean stunned) {
        return new EnemyExpectation(name, hp, alive, stunned);
    }

    private record EnemyExpectation(String name, int hp, boolean alive, boolean stunned) {
    }
}
