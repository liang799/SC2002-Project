package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class EnemyCombatantTest {
    @Test
    void takeTurn_WhenCreatedByFactory_ExecutesBasicAttackAgainstTarget() {
        // arrange
        EnemyCombatant goblin = TestDependencies.goblin("Goblin");
        PlayerCharacter warrior = TestDependencies.warrior();
        TestActionExecutionContext context = new TestActionExecutionContext(List.of());

        // act
        List<BattleEvent> events = goblin.takeTurn(context, warrior);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(1, events.size());
        assertEquals(245, warrior.getCurrentHp());
        assertEquals("Goblin", actionEvent.getActorName());
        assertEquals("BasicAttack", actionEvent.getActionName());
        assertEquals("Warrior", actionEvent.getTargetName());
        assertEquals(15, actionEvent.getDamage());
    }

    @Test
    void takeTurn_WhenCustomAttackActionIsInjected_DelegatesToInjectedAction() {
        // arrange
        RecordingBattleAction attackAction = new RecordingBattleAction();
        EnemyCombatant goblin = TestEnemyCombatantBuilder.anEnemyCombatant(attackAction)
            .named("Goblin")
            .build();
        PlayerCharacter warrior = TestDependencies.warrior();
        TestActionExecutionContext context = new TestActionExecutionContext(List.of());

        // act
        List<BattleEvent> events = goblin.takeTurn(context, warrior);

        // assert
        assertSame(context, attackAction.context);
        assertSame(goblin, attackAction.actor);
        assertSame(warrior, attackAction.target);
        assertEquals(1, events.size());
        assertSame(attackAction.event, events.get(0));
    }

    private static final class RecordingBattleAction implements BattleAction {
        private final BattleEvent event = new TestBattleEvent();
        private ActionExecutionContext context;
        private Combatant actor;
        private Combatant target;

        @Override
        public String getName() {
            return "EnemyAttack";
        }

        @Override
        public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
            this.context = context;
            this.actor = actor;
            this.target = target;
            return List.of(event);
        }
    }

    private static final class TestBattleEvent implements BattleEvent {
    }
}
