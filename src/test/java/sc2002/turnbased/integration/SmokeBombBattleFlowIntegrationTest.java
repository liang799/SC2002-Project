package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.ScriptedDecisionProvider;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;
import sc2002.turnbased.support.BattleTestSupport;

@Tag("integration")
class SmokeBombBattleFlowIntegrationTest {
    @Test
    @DisplayName("Using Smoke Bomb adds protection notes and blocks the next enemy attack")
    void useSmokeBomb_WhenOneEnemyAttacks_KeepsSmokeBombActive() {
        BattleSetup battleSetup = createSetup(1);
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSmokeBombAction()));

        List<BattleEvent> events = battleEngine.runRounds(1, decisions);
        BattleTestSupport.RoundCapture round = BattleTestSupport.captureRounds(events).get(1);
        StatusEffectReportEvent statusReport = findStatusReport(round.events());
        ActionEvent goblinAttack = findAction(round.events(), "Goblin A", "Warrior");

        assertAll(
            () -> assertEquals(
                List.of("Warrior gains Smoke Bomb protection for 2 enemy attacks"),
                statusReport.statusEffectNotes()
            ),
            () -> assertEquals(List.of("Smoke Bomb blocked the attack"), goblinAttack.getStatusEffectNotes()),
            () -> assertEquals(260, battleSetup.getPlayer().getCurrentHp()),
            () -> assertEquals(List.of("SMOKE BOMB"), battleSetup.getPlayer().getActiveStatuses())
        );
    }

    @Test
    @DisplayName("Using Smoke Bomb twice in one round consumes both charges and expires the effect")
    void useSmokeBomb_WhenTwoEnemiesAttack_ExpiresAfterSecondBlock() {
        BattleSetup battleSetup = createSetup(2);
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSmokeBombAction()));

        List<BattleEvent> events = battleEngine.runRounds(1, decisions);
        BattleTestSupport.RoundCapture round = BattleTestSupport.captureRounds(events).get(1);
        ActionEvent firstGoblinAttack = findAction(round.events(), "Goblin A", "Warrior");
        ActionEvent secondGoblinAttack = findAction(round.events(), "Goblin B", "Warrior");

        assertAll(
            () -> assertEquals(List.of("Smoke Bomb blocked the attack"), firstGoblinAttack.getStatusEffectNotes()),
            () -> assertEquals(
                List.of("Smoke Bomb blocked the attack", "Smoke Bomb expired"),
                secondGoblinAttack.getStatusEffectNotes()
            ),
            () -> assertEquals(260, battleSetup.getPlayer().getCurrentHp()),
            () -> assertEquals(List.of(), battleSetup.getPlayer().getActiveStatuses())
        );
    }

    private static BattleSetup createSetup(int goblinCount) {
        return sc2002.turnbased.support.TestDependencies.battleSetupFactory().createCustom(
            new CustomGameConfiguration(
                PlayerType.WARRIOR,
                List.of(ItemType.SMOKE_BOMB, ItemType.POTION),
                List.of(WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, goblinCount),
                    EnemyCount.of(EnemyType.WOLF, 0)
                ))
            )
        );
    }

    private static StatusEffectReportEvent findStatusReport(List<BattleEvent> events) {
        return events.stream()
            .filter(StatusEffectReportEvent.class::isInstance)
            .map(StatusEffectReportEvent.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Status effect report event not found"));
    }

    private static ActionEvent findAction(List<BattleEvent> events, String actor, String target) {
        return events.stream()
            .filter(ActionEvent.class::isInstance)
            .map(ActionEvent.class::cast)
            .filter(event -> event.getActorName().equals(actor) && event.getTargetName().equals(target))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Action event not found for " + actor + " -> " + target));
    }
}
