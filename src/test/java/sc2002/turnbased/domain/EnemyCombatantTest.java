package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class EnemyCombatantTest {
    @Test
    void attackPlayer_delegatesToInjectedAttackAction() {
        RecordingBattleAction attackAction = new RecordingBattleAction();
        EnemyCombatant goblin = new EnemyCombatant(
            "Goblin",
            new HitPoints(110, 110),
            CombatStats.builder()
                .attack(35)
                .defense(10)
                .speed(15)
                .build(),
            TestDependencies.registry(),
            attackAction
        );
        PlayerCharacter warrior = TestDependencies.warrior();
        ActionExecutionContext context = new StubActionExecutionContext();

        List<BattleEvent> events = goblin.attackPlayer(context, warrior);

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

    private static final class StubActionExecutionContext implements ActionExecutionContext {
        private final Inventory inventory = new Inventory();

        @Override
        public List<Combatant> getLivingEnemies() {
            return List.of();
        }

        @Override
        public List<Combatant> getLivingEnemiesInTurnOrder() {
            return List.of();
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private static final class TestBattleEvent implements BattleEvent {
    }
}
