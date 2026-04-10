package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class UsePowerStoneSkillActionTest {
    @Test
    void execute_WhenActorIsNotAPlayerCharacter_DoesNotConsumePowerStoneBeforeThrowing() {
        UsePowerStoneSkillAction action = new UsePowerStoneSkillAction();
        Combatant actor = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .build();
        actor.getInventory().add(ItemType.POWER_STONE, 1);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> action.execute(new TestActionExecutionContext(List.of()), actor, null)
        );

        assertEquals("Power Stone is only supported for player characters", exception.getMessage());
        assertEquals(1, actor.getInventory().countOf(ItemType.POWER_STONE));
    }
}
