package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class BasicAttackActionTest {
    @Test
    void execute_WhenUserSelectsTarget_DealsDamageToSelectedTargetOnly() {
        // arrange
        BasicAttackAction action = new BasicAttackAction();
        Combatant attacker = aCombatantNamed("Warrior").build();
        Combatant selectedTarget = aCombatantNamed("Goblin")
            .withCurrentHp(70)
            .withDefense(15)
            .build();
        Combatant otherEnemy = aCombatantNamed("Wolf").build();

        // act
        List<BattleEvent> events = action.execute(new TestActionExecutionContext(List.of(selectedTarget, otherEnemy)), attacker, selectedTarget);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(45, selectedTarget.getCurrentHp());
        assertEquals(100, otherEnemy.getCurrentHp());
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
        Combatant attacker = aCombatantNamed("Goblin")
            .withAttack(10)
            .build();
        Combatant target = aCombatantNamed("Warrior")
            .withDefense(25)
            .build();

        // act
        List<BattleEvent> events = action.execute(new TestActionExecutionContext(List.of(target)), attacker, target);
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
        Combatant attacker = aCombatantNamed("Wolf")
            .withAttack(90)
            .build();
        Combatant target = aCombatantNamed("Wizard")
            .withCurrentHp(20)
            .withDefense(10)
            .build();

        // act
        List<BattleEvent> events = action.execute(new TestActionExecutionContext(List.of(target)), attacker, target);
        ActionEvent actionEvent = assertInstanceOf(ActionEvent.class, events.get(0));

        // assert
        assertEquals(0, target.getCurrentHp());
        assertEquals(0, actionEvent.getHpAfter());
        assertTrue(actionEvent.isTargetEliminated());
    }

    private TestCombatantBuilder aCombatantNamed(String name) {
        return TestCombatantBuilder.aCombatant().named(name);
    }
}
