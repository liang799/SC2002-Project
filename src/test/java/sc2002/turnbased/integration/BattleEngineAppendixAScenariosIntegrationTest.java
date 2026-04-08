package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sc2002.turnbased.support.BattleRoundAssertions.assertRoundSummary;
import static sc2002.turnbased.support.ExpectedCombatantState.enemy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.AppendixAScenarios;
import sc2002.turnbased.support.BattleTestSupport;
import sc2002.turnbased.support.BattleTestSupport.ScenarioRun;

@Tag("integration")
class BattleEngineAppendixAScenariosIntegrationTest {
    @Test
    @DisplayName("Given the Appendix A easy warrior scenario, when it is run, then the transcript and round state match expectations")
    void givenAppendixAEasyWarriorScenario_WhenRun_ThenTranscriptAndRoundStateMatchExpectations() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.easyWarrior());

        assertEquals(11, run.summaries().size());
        assertRoundSummary(run.summaries().get(0), 215, 0, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 30),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(run.summaries().get(1), 185, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 5).stunned(),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(run.summaries().get(2), 155, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 0),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(run.summaries().get(3), 155, 1, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(run.summaries().get(4), 140, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 30).stunned(),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(run.summaries().get(5), 125, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 30),
            enemy("Goblin C", 30)
        );
        assertRoundSummary(run.summaries().get(6), 195, 1, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 30),
            enemy("Goblin C", 30)
        );
        assertRoundSummary(run.summaries().get(7), 180, 3, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 5).stunned(),
            enemy("Goblin C", 30)
        );
        assertRoundSummary(run.summaries().get(8), 165, 2, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 0),
            enemy("Goblin C", 30)
        );
        assertRoundSummary(run.summaries().get(9), 150, 1, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 0),
            enemy("Goblin C", 5)
        );
        assertRoundSummary(run.summaries().get(10), 150, 0, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0),
            enemy("Goblin A", 0),
            enemy("Goblin B", 0),
            enemy("Goblin C", 0)
        );

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Goblin A -> ELIMINATED: Skipped | Stun expires")),
            () -> assertTrue(run.formattedOutput().contains("Warrior -> Item -> Smoke Bomb used")),
            () -> assertTrue(run.formattedOutput().contains("Victory:"))
        );
    }

    @Test
    @DisplayName("Given the Appendix A medium warrior scenario, when it is run, then the transcript and round state match expectations")
    void givenAppendixAMediumWarriorScenario_WhenRun_ThenTranscriptAndRoundStateMatchExpectations() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.mediumWarrior());

        assertEquals(9, run.summaries().size());
        assertRoundSummary(run.summaries().get(0), 220, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 55),
            enemy("Wolf", 5).stunned()
        );
        assertRoundSummary(run.summaries().get(1), 205, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 55),
            enemy("Wolf", 0)
        );
        assertRoundSummary(run.summaries().get(2), 190, 1, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 30),
            enemy("Wolf", 0)
        );
        assertRoundSummary(run.summaries().get(3), 175, 0, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 5),
            enemy("Wolf", 0)
        );
        assertRoundSummary(run.summaries().get(4), 175, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 40),
            enemy("Wolf B", 40)
        );
        assertRoundSummary(run.summaries().get(5), 125, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 5).stunned(),
            enemy("Wolf B", 40)
        );
        assertRoundSummary(run.summaries().get(6), 100, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 0),
            enemy("Wolf B", 40)
        );
        assertRoundSummary(run.summaries().get(7), 75, 1, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 0),
            enemy("Wolf B", 5)
        );
        assertRoundSummary(run.summaries().get(8), 50, 0, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 0),
            enemy("Wolf B", 0)
        );

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Backup Spawn triggered: Wolf A, Wolf B")),
            () -> assertTrue(run.formattedOutput().contains("Power Stone used -> Shield Bash triggered on Wolf A")),
            () -> assertTrue(run.formattedOutput().contains("Victory:"))
        );
    }

    @Test
    @DisplayName("Given the Appendix A medium wizard scenario, when it is run, then the transcript and round state match expectations")
    void givenAppendixAMediumWizardScenario_WhenRun_ThenTranscriptAndRoundStateMatchExpectations() {
        ScenarioRun run = BattleTestSupport.runScenario(AppendixAScenarios.mediumWizard());

        assertEquals(3, run.summaries().size());
        assertRoundSummary(run.summaries().get(0), 140, 3, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 20),
            enemy("Wolf", 0)
        );
        assertEquals(60, run.summaries().get(0).getPlayerSummary().getCurrentAttack());

        assertRoundSummary(run.summaries().get(1), 115, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 1),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 40),
            enemy("Wolf B", 40)
        );
        assertEquals(60, run.summaries().get(1).getPlayerSummary().getCurrentAttack());

        assertRoundSummary(run.summaries().get(2), 45, 2, Map.of(ItemType.POTION, 1, ItemType.POWER_STONE, 0),
            enemy("Goblin", 0),
            enemy("Wolf", 0),
            enemy("Wolf A", 0),
            enemy("Wolf B", 0)
        );
        assertEquals(80, run.summaries().get(2).getPlayerSummary().getCurrentAttack());

        assertAll(
            () -> assertTrue(run.formattedOutput().contains("Wizard -> Arcane Blast -> all enemies")),
            () -> assertTrue(run.formattedOutput().contains("Power Stone used -> Arcane Blast triggered")),
            () -> assertTrue(run.formattedOutput().contains("Final Wizard ATK: 80"))
        );
    }

}
