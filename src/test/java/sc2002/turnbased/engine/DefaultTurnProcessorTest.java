package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.StunStatusEffect;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class DefaultTurnProcessorTest {
    @Test
    void processTurn_WhenPlayerActs_EmitsActionEvent() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withHp(60)
            .build();

        TurnProcessor turnProcessor = new DefaultTurnProcessor(player);
        PlayerDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy));
        ActionExecutionContext context = new TestActionExecutionContext(List.of(enemy));
        List<BattleEvent> emittedEvents = new ArrayList<>();

        turnProcessor.processTurn(1, player, decisions, context, emittedEvents::add);

        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, emittedEvents.get(0));
        assertEquals(player.getName(), actionEvent.getActorName());
        assertEquals(enemy.getName(), actionEvent.getTargetName());
    }

    @Test
    void processTurn_WhenEnemyIsStunned_EmitsSkippedTurnEvent() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant stunnedEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Stunned Goblin")
            .withHp(50)
            .build();
        stunnedEnemy.addStatusEffect(new StunStatusEffect(1));

        TurnProcessor turnProcessor = new DefaultTurnProcessor(player);
        ActionExecutionContext context = new TestActionExecutionContext(List.of(stunnedEnemy));
        List<BattleEvent> emittedEvents = new ArrayList<>();

        turnProcessor.processTurn(1, stunnedEnemy, new ScriptedDecisionProvider(), context, emittedEvents::add);

        SkippedTurnEvent skippedTurnEvent = assertInstanceOf(SkippedTurnEvent.class, emittedEvents.get(0));
        assertEquals("STUNNED", skippedTurnEvent.getReason());
        assertEquals(stunnedEnemy.getName(), skippedTurnEvent.getCombatantName());
    }

    @Test
    void processTurn_WhenPlayerIsTurnBlocked_DoesNotConsumeDecisionOrAdvanceCooldown() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withHp(80)
            .build();

        ActionExecutionContext context = new TestActionExecutionContext(List.of(enemy));
        player.useSpecialSkill(context, enemy);
        int cooldownBefore = player.getSpecialSkillCooldown();
        assertTrue(cooldownBefore > 0);

        player.addStatusEffect(new StunStatusEffect(2));
        List<String> activeStatusesBefore = player.getActiveStatuses();

        int[] decisionCalls = new int[] {0};
        PlayerDecisionProvider decisions = (roundNumber, actingPlayer, livingEnemies) -> {
            decisionCalls[0]++;
            return PlayerDecision.targeted(new BasicAttackAction(), enemy);
        };

        TurnProcessor turnProcessor = new DefaultTurnProcessor(player);
        List<BattleEvent> emittedEvents = new ArrayList<>();

        turnProcessor.processTurn(1, player, decisions, context, emittedEvents::add);

        SkippedTurnEvent skippedTurnEvent = assertInstanceOf(SkippedTurnEvent.class, emittedEvents.get(0));
        assertEquals("STUNNED", skippedTurnEvent.getReason());
        assertEquals(player.getName(), skippedTurnEvent.getCombatantName());
        assertEquals(0, decisionCalls[0]);
        assertEquals(cooldownBefore, player.getSpecialSkillCooldown());
        assertEquals(activeStatusesBefore, player.getActiveStatuses());
    }
}