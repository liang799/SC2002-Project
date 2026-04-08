package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class BasicAttackActionTest {
    @Test
    void execute_WhenUserSelectedTarget_DealsDamageToSelectedTargetOnly() {
        // arrange
        BasicAttackAction action = new BasicAttackAction();
        Combatant attacker = TestCombatantBuilder.aCombatant()
            .named("Warrior")
            .withAttack(40)
            .build();
        Combatant selectedTarget = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withCurrentHp(70)
            .withMaxHp(70)
            .withDefense(15)
            .build();
        Combatant otherEnemy = TestCombatantBuilder.aCombatant()
            .named("Wolf")
            .withCurrentHp(80)
            .withMaxHp(80)
            .withDefense(5)
            .build();

        // act
        List<BattleEvent> events = action.execute(new StubActionExecutionContext(List.of(selectedTarget, otherEnemy)), attacker, selectedTarget);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(45, selectedTarget.getCurrentHp());
        assertEquals(80, otherEnemy.getCurrentHp());
        assertEquals("Warrior", actionEvent.getActorName());
        assertEquals("BasicAttack", actionEvent.getActionName());
        assertEquals("Goblin", actionEvent.getTargetName());
        assertEquals(25, actionEvent.getDamage());
        assertFalse(actionEvent.isTargetEliminated());
    }

    @Test
    void execute_WhenTargetDefenseExceedsAttack_DealsZeroDamage() {
        // arrange
        BasicAttackAction action = new BasicAttackAction();
        Combatant attacker = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withAttack(10)
            .build();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Warrior")
            .withCurrentHp(100)
            .withMaxHp(100)
            .withDefense(25)
            .build();

        // act
        List<BattleEvent> events = action.execute(new StubActionExecutionContext(List.of(target)), attacker, target);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(100, target.getCurrentHp());
        assertEquals(0, actionEvent.getDamage());
        assertEquals(100, actionEvent.getHpAfter());
        assertFalse(actionEvent.isTargetEliminated());
    }

    @Test
    void execute_WhenDamageExceedsCurrentHp_ClampsTargetHpAtZero() {
        // arrange
        BasicAttackAction action = new BasicAttackAction();
        Combatant attacker = TestCombatantBuilder.aCombatant()
            .named("Wolf")
            .withAttack(90)
            .build();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Wizard")
            .withCurrentHp(20)
            .withMaxHp(60)
            .withDefense(10)
            .build();

        // act
        List<BattleEvent> events = action.execute(new StubActionExecutionContext(List.of(target)), attacker, target);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(0, target.getCurrentHp());
        assertEquals(0, actionEvent.getHpAfter());
        assertTrue(actionEvent.isTargetEliminated());
    }

    private record StubActionExecutionContext(List<Combatant> livingEnemies) implements ActionExecutionContext {
        @Override
        public List<Combatant> getLivingEnemies() {
            return livingEnemies();
        }

        @Override
        public List<Combatant> getLivingEnemiesInTurnOrder() {
            return livingEnemies();
        }

        @Override
        public Inventory getInventory() {
            return new Inventory();
        }
    }
}
