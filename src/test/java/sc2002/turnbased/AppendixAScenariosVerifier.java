package sc2002.turnbased;

import java.util.List;

import sc2002.turnbased.engine.AppendixAScenarios;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.ScenarioScript;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.ui.BattleConsoleFormatter;

public class AppendixAScenariosVerifier {
    public static void main(String[] args) {
        verifyEasyWarrior();
        verifyMediumWarrior();
        verifyMediumWizard();
    }

    private static void verifyEasyWarrior() {
        ScenarioScript scenario = AppendixAScenarios.easyWarrior();
        ScenarioRun run = runScenario(scenario);

        require(run.summaries().size() == 11, "Easy scenario should have 11 summaries");
        assertRound(run.summaries().get(0), 215, 0, 1, "Goblin A", 30, false, "Goblin B", 55, false, "Goblin C", 55, false);
        assertRound(run.summaries().get(1), 185, 3, 1, "Goblin A", 5, true, "Goblin B", 55, false, "Goblin C", 55, false);
        assertRound(run.summaries().get(2), 155, 2, 1, "Goblin A", 0, false, "Goblin B", 55, false, "Goblin C", 55, false);
        assertRound(run.summaries().get(3), 155, 1, 0, "Goblin A", 0, false, "Goblin B", 55, false, "Goblin C", 55, false);
        assertRound(run.summaries().get(4), 140, 3, 0, "Goblin A", 0, false, "Goblin B", 30, true, "Goblin C", 55, false);
        assertRound(run.summaries().get(5), 125, 2, 0, "Goblin A", 0, false, "Goblin B", 30, false, "Goblin C", 30, false);
        assertRound(run.summaries().get(6), 195, 1, 0, "Goblin A", 0, false, "Goblin B", 30, false, "Goblin C", 30, false);
        assertRound(run.summaries().get(7), 180, 3, 0, "Goblin A", 0, false, "Goblin B", 5, true, "Goblin C", 30, false);
        assertRound(run.summaries().get(8), 165, 2, 0, "Goblin A", 0, false, "Goblin B", 0, false, "Goblin C", 30, false);
        assertRound(run.summaries().get(9), 150, 1, 0, "Goblin A", 0, false, "Goblin B", 0, false, "Goblin C", 5, false);
        assertRound(run.summaries().get(10), 150, 0, 0, "Goblin A", 0, false, "Goblin B", 0, false, "Goblin C", 0, false);

        require(run.formattedOutput().contains("Goblin A -> ELIMINATED: Skipped | Stun expires"), "Easy output missing stun-expiry skip");
        require(run.formattedOutput().contains("Warrior -> Item -> Smoke Bomb used"), "Easy output missing smoke bomb usage");
        require(run.formattedOutput().contains("Victory:"), "Easy output missing victory section");
    }

    private static void verifyMediumWarrior() {
        ScenarioScript scenario = AppendixAScenarios.mediumWarrior();
        ScenarioRun run = runScenario(scenario);

        require(run.summaries().size() == 9, "Medium Warrior scenario should have 9 summaries");
        assertRound(run.summaries().get(0), 220, 3, 1, "Goblin", 55, false, "Wolf", 5, true);
        assertRound(run.summaries().get(1), 205, 2, 1, "Goblin", 55, false, "Wolf", 0, false);
        assertRound(run.summaries().get(2), 190, 1, 1, "Goblin", 30, false, "Wolf", 0, false);
        assertRound(run.summaries().get(3), 175, 0, 1, "Goblin", 5, false, "Wolf", 0, false);
        assertRound(run.summaries().get(4), 175, 3, 1, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 40, false, "Wolf B", 40, false);
        assertRound(run.summaries().get(5), 125, 3, 0, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 5, true, "Wolf B", 40, false);
        assertRound(run.summaries().get(6), 100, 2, 0, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 0, false, "Wolf B", 40, false);
        assertRound(run.summaries().get(7), 75, 1, 0, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 0, false, "Wolf B", 5, false);
        assertRound(run.summaries().get(8), 50, 0, 0, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 0, false, "Wolf B", 0, false);

        require(run.formattedOutput().contains("Backup Spawn triggered: Wolf A, Wolf B"), "Medium Warrior output missing backup spawn");
        require(run.formattedOutput().contains("Power Stone used -> Shield Bash triggered on Wolf A"), "Medium Warrior output missing Power Stone turn");
        require(run.formattedOutput().contains("Victory:"), "Medium Warrior output missing victory section");
    }

    private static void verifyMediumWizard() {
        ScenarioScript scenario = AppendixAScenarios.mediumWizard();
        ScenarioRun run = runScenario(scenario);

        require(run.summaries().size() == 3, "Medium Wizard scenario should have 3 summaries");
        assertRound(run.summaries().get(0), 140, 3, 1, "Goblin", 20, false, "Wolf", 0, false);
        require(run.summaries().get(0).getPlayerSummary().getCurrentAttack() == 60, "Wizard ATK should be 60 after round 1");

        assertRound(run.summaries().get(1), 115, 2, 1, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 40, false, "Wolf B", 40, false);
        require(run.summaries().get(1).getPlayerSummary().getCurrentAttack() == 60, "Wizard ATK should stay 60 after round 2");

        assertRound(run.summaries().get(2), 45, 2, 0, "Goblin", 0, false, "Wolf", 0, false, "Wolf A", 0, false, "Wolf B", 0, false);
        require(run.summaries().get(2).getPlayerSummary().getCurrentAttack() == 80, "Wizard ATK should be 80 after round 3");

        require(run.formattedOutput().contains("Wizard -> Arcane Blast -> all enemies"), "Medium Wizard output missing Arcane Blast");
        require(run.formattedOutput().contains("Power Stone used -> Arcane Blast triggered"), "Medium Wizard output missing Power Stone Arcane Blast");
        require(run.formattedOutput().contains("Final Wizard ATK: 80"), "Medium Wizard output missing final ATK");
    }

    private static ScenarioRun runScenario(ScenarioScript scenario) {
        BattleEngine battleEngine = new BattleEngine(scenario.getBattleSetup(), new SpeedTurnOrderStrategy());
        List<BattleEvent> events = battleEngine.runRounds(scenario.getRoundCount(), scenario.getDecisionProvider());
        List<RoundSummaryEvent> summaries = events.stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .toList();

        BattleConsoleFormatter formatter = new BattleConsoleFormatter();
        String output = String.join("\n", formatter.format(events));
        return new ScenarioRun(summaries, output);
    }

    private static void assertRound(RoundSummaryEvent summary, int expectedPlayerHp, int expectedCooldown, int expectedPrimaryItemCount, Object... enemyTriples) {
        require(summary.getPlayerSummary().getCurrentHp() == expectedPlayerHp, "Unexpected player HP");
        require(summary.getSpecialSkillCooldown() == expectedCooldown, "Unexpected cooldown");
        require(summary.getInventorySnapshot().values().stream().findFirst().orElse(-1) >= 0, "Inventory snapshot missing");

        for (int index = 0; index < enemyTriples.length; index += 3) {
            String enemyName = (String) enemyTriples[index];
            int expectedHp = (Integer) enemyTriples[index + 1];
            boolean expectedStunned = (Boolean) enemyTriples[index + 2];

            CombatantSummary enemySummary = findEnemy(summary, enemyName);
            require(enemySummary.getCurrentHp() == expectedHp, "Unexpected HP for " + enemyName);
            boolean isStunned = enemySummary.getActiveStatuses().contains("STUNNED");
            require(isStunned == expectedStunned, "Unexpected stun state for " + enemyName);
        }

        require(summary.getInventorySnapshot().containsValue(expectedPrimaryItemCount), "Expected item count not found in summary inventory");
    }

    private static CombatantSummary findEnemy(RoundSummaryEvent summary, String enemyName) {
        return summary.getEnemySummaries().stream()
            .filter(enemy -> enemy.getName().equals(enemyName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Enemy not found in summary: " + enemyName));
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private record ScenarioRun(List<RoundSummaryEvent> summaries, String formattedOutput) {
    }
}
