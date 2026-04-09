package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class PlayerCharacterTest {
    @Test
    void useSpecialSkill_WhenTriggered_DelegatesThroughPlayerAndStartsCooldown() {
        PlayerCharacter warrior = TestDependencies.warrior();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Wolf")
            .withCurrentHp(80)
            .build();

        List<BattleEvent> events = warrior.useSpecialSkill(new TestActionExecutionContext(List.of(target)), target);

        assertEquals(1, events.size());
        assertFalse(warrior.canUseSpecialSkill());
        assertEquals(3, warrior.getSpecialSkillCooldown());
        assertEquals(60, target.getCurrentHp());
    }

    @Test
    void advanceRoundState_WhenCooldownIsActive_DecrementsCooldownThroughPlayer() {
        PlayerCharacter wizard = TestDependencies.wizard();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .build();
        wizard.useSpecialSkill(new TestActionExecutionContext(List.of(target)), null);

        wizard.advanceRoundState();

        assertEquals(2, wizard.getSpecialSkillCooldown());
    }

    @Test
    void useSpecialSkillWithoutCooldown_WhenTriggered_DoesNotConsumeSkillAvailability() {
        PlayerCharacter wizard = TestDependencies.wizard();
        Combatant goblin = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withHp(30)
            .withDefense(0)
            .build();

        List<BattleEvent> events = wizard.useSpecialSkillWithoutCooldown(new TestActionExecutionContext(List.of(goblin)), null);

        assertEquals(2, events.size());
        assertTrue(wizard.canUseSpecialSkill());
        assertEquals(0, wizard.getSpecialSkillCooldown());
        assertEquals(0, goblin.getCurrentHp());
    }
}
