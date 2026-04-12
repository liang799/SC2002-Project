package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("integration")
class BattleEngineEventStreamConsistencyTest {
    @Test
    void runRounds_WhenListenerIsProvided_ListenerStreamMatchesReturnedEvents() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withHp(60)
            .build();

        BattleEngine battleEngine = new BattleEngine(
            new BattleSetup(player, List.of(enemy), List.of()),
            new SpeedTurnOrderStrategy()
        );

        List<BattleEvent> observedByListener = new ArrayList<>();
        List<BattleEvent> returnedEvents = battleEngine.runRounds(
            1,
            new ScriptedDecisionProvider().addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy)),
            observedByListener::add
        );

        assertFalse(returnedEvents.isEmpty());
        assertEquals(returnedEvents, observedByListener);
    }

    @Test
    void runRounds_WhenInvokedTwiceOnSameEngine_EventStreamsDoNotCarryOverBetweenRuns() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Training Goblin")
            .withHp(300)
            .withAttack(0)
            .build();

        BattleEngine battleEngine = new BattleEngine(
            new BattleSetup(player, List.of(enemy), List.of()),
            new SpeedTurnOrderStrategy()
        );

        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy));
        List<BattleEvent> observedByListener = new ArrayList<>();

        List<BattleEvent> firstReturnedEvents = battleEngine.runRounds(1, decisions, observedByListener::add);
        List<BattleEvent> firstObservedByListener = List.copyOf(observedByListener);

        List<BattleEvent> secondReturnedEvents = battleEngine.runRounds(1, decisions, observedByListener::add);
        List<BattleEvent> secondObservedByListener = List.copyOf(
            observedByListener.subList(firstObservedByListener.size(), observedByListener.size())
        );

        assertFalse(firstReturnedEvents.isEmpty());
        assertFalse(secondReturnedEvents.isEmpty());
        assertEquals(firstReturnedEvents, firstObservedByListener);
        assertEquals(secondReturnedEvents, secondObservedByListener);
    }

    @Test
    void runUntilBattleEnds_WhenListenerIsProvided_ListenerStreamMatchesReturnedEvents() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Weak Goblin")
            .withHp(1)
            .withDefense(0)
            .build();

        BattleEngine battleEngine = new BattleEngine(
            new BattleSetup(player, List.of(enemy), List.of()),
            new SpeedTurnOrderStrategy()
        );

        List<BattleEvent> observedByListener = new ArrayList<>();
        PlayerDecisionProvider decisions = (roundNumber, actor, livingEnemies) ->
            PlayerDecision.targeted(new BasicAttackAction(), firstEnemy(livingEnemies));

        List<BattleEvent> returnedEvents = battleEngine.runUntilBattleEnds(decisions, observedByListener::add);

        assertFalse(returnedEvents.isEmpty());
        assertEquals(returnedEvents, observedByListener);
    }

    private static Combatant firstEnemy(List<Combatant> livingEnemies) {
        return livingEnemies.stream()
            .findFirst()
            .orElseThrow(() -> new AssertionError("Expected at least one living enemy"));
    }
}