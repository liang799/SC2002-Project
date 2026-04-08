package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sc2002.turnbased.support.BattleRoundAssertions.assertRoundSummary;
import static sc2002.turnbased.support.ExpectedCombatantState.enemy;

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
import sc2002.turnbased.report.RoundSummaryEvent;

@Tag("integration")
class BattleEngineEasyLevelRoundsIntegrationTest {
    @Test
    @DisplayName("Given the easy warrior opening script, when rounds are run, then the round summaries are deterministic")
    void givenEasyWarriorOpeningScript_WhenRoundsAreRun_ThenRoundSummariesAreDeterministic() {
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
            enemy("Goblin A", 30),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(summaries.get(1), 185, 3, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 5).stunned(),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertRoundSummary(summaries.get(2), 155, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1),
            enemy("Goblin A", 0),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
    }
}
