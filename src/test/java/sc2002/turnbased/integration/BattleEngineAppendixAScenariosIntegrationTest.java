package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.AppendixAScenarios;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.support.BattleTestSupport;
import sc2002.turnbased.support.BattleTestSupport.ScenarioRun;

@Tag("integration")
class BattleEngineAppendixAScenariosIntegrationTest {
    @Test
    @DisplayName("BattleEngine and Appendix A easy warrior scenario produce expected transcript and round state")
    void battleEngine_andAppendixAEasyWarriorScenario_produceExpectedTranscriptAndRoundState() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.easyWarrior());

        assertEquals(11, run.summaries().size());
        assertRound(run.summaries().get(0), 215, 0, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 30, true, false),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRound(run.summaries().get(1), 185, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 5, true, true),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRound(run.summaries().get(2), 155, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRound(run.summaries().get(3), 155, 1, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 55, true, false),
            enemy("Goblin C", 55, true, false)
        );
        assertRound(run.summaries().get(4), 140, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 30, true, true),
            enemy("Goblin C", 55, true, false)
        );
        assertRound(run.summaries().get(5), 125, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 30, true, false),
            enemy("Goblin C", 30, true, false)
        );
        assertRound(run.summaries().get(6), 195, 1, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 30, true, false),
            enemy("Goblin C", 30, true, false)
        );
        assertRound(run.summaries().get(7), 180, 3, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 5, true, true),
            enemy("Goblin C", 30, true, false)
        );
        assertRound(run.summaries().get(8), 165, 2, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 0, false, false),
            enemy("Goblin C", 30, true, false)
        );
        assertRound(run.summaries().get(9), 150, 1, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 0, false, false),
            enemy("Goblin C", 5, true, false)
        );
        assertRound(run.summaries().get(10), 150, 0, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0, false, false),
            enemy("Goblin B", 0, false, false),
            enemy("Goblin C", 0, false, false)
        );

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Goblin A -> ELIMINATED: Skipped | Stun expires")),
            () -> assertTrue(run.formattedOutput().contains("Warrior -> Item -> Smoke Bomb used")),
            () -> assertTrue(run.formattedOutput().contains("Victory:"))
        );
    }

    @Test
    @DisplayName("BattleEngine and Appendix A medium warrior scenario produce expected transcript and round state")
    void battleEngine_andAppendixAMediumWarriorScenario_produceExpectedTranscriptAndRoundState() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.mediumWarrior());

        assertEquals(9, run.summaries().size());
        assertRound(run.summaries().get(0), 220, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 55, true, false),
            enemy("Wolf", 5, true, true)
        );
        assertRound(run.summaries().get(1), 205, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 55, true, false),
            enemy("Wolf", 0, false, false)
        );
        assertRound(run.summaries().get(2), 190, 1, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 30, true, false),
            enemy("Wolf", 0, false, false)
        );
        assertRound(run.summaries().get(3), 175, 0, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 5, true, false),
            enemy("Wolf", 0, false, false)
        );
        assertRound(run.summaries().get(4), 175, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 40, true, false),
            enemy("Wolf B", 40, true, false)
        );
        assertRound(run.summaries().get(5), 125, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 5, true, true),
            enemy("Wolf B", 40, true, false)
        );
        assertRound(run.summaries().get(6), 100, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 0, false, false),
            enemy("Wolf B", 40, true, false)
        );
        assertRound(run.summaries().get(7), 75, 1, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 0, false, false),
            enemy("Wolf B", 5, true, false)
        );
        assertRound(run.summaries().get(8), 50, 0, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 0, false, false),
            enemy("Wolf B", 0, false, false)
        );

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Backup Spawn triggered: Wolf A, Wolf B")),
            () -> assertTrue(run.formattedOutput().contains("Power Stone used -> Shield Bash triggered on Wolf A")),
            () -> assertTrue(run.formattedOutput().contains("Victory:"))
        );
    }

    @Test
    @DisplayName("BattleEngine and Appendix A medium wizard scenario produce expected transcript and round state")
    void battleEngine_andAppendixAMediumWizardScenario_produceExpectedTranscriptAndRoundState() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.mediumWizard());

        assertEquals(3, run.summaries().size());
        assertRound(run.summaries().get(0), 140, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 20, true, false),
            enemy("Wolf", 0, false, false)
        );
        assertEquals(60, run.summaries().get(0).getPlayerSummary().getCurrentAttack());

        assertRound(run.summaries().get(1), 115, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 40, true, false),
            enemy("Wolf B", 40, true, false)
        );
        assertEquals(60, run.summaries().get(1).getPlayerSummary().getCurrentAttack());

        assertRound(run.summaries().get(2), 45, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0, false, false),
            enemy("Wolf", 0, false, false),
            enemy("Wolf A", 0, false, false),
            enemy("Wolf B", 0, false, false)
        );
        assertEquals(80, run.summaries().get(2).getPlayerSummary().getCurrentAttack());

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Wizard -> Arcane Blast -> all enemies")),
            () -> assertTrue(run.formattedOutput().contains("Power Stone used -> Arcane Blast triggered")),
            () -> assertTrue(run.formattedOutput().contains("Final Wizard ATK: 80"))
        );
    }

    private static void assertRound(
        RoundSummaryEvent summary,
        int expectedPlayerHp,
        int expectedCooldown,
        Map<ItemType, Integer> expectedInventory,
        EnemyExpectation... expectedEnemies
    ) {
        assertAll(
            () -> assertEquals(expectedPlayerHp, summary.getPlayerSummary().getCurrentHp(), "Unexpected player HP"),
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
