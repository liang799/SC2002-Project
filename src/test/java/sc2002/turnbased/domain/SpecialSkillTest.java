package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.ShieldBashAction;

@Tag("unit")
class SpecialSkillTest {
    @Test
    void isAvailable_WhenCooldownHasNotBeenTriggered_ReturnsTrue() {
        SpecialSkill specialSkill = specialSkillWithCooldown(3);

        boolean available = specialSkill.isAvailable();

        assertTrue(available);
    }

    @Test
    void triggerCooldown_WhenCalled_SetsCooldownRemainingToConfiguredTurns() {
        SpecialSkill specialSkill = specialSkillWithCooldown(3);

        specialSkill.triggerCooldown();

        assertFalse(specialSkill.isAvailable());
        assertEquals(3, specialSkill.cooldownRemaining());
    }

    @Test
    void advanceCooldown_WhenCooldownIsActive_DecrementsRemainingTurns() {
        SpecialSkill specialSkill = specialSkillWithCooldown(3);
        specialSkill.triggerCooldown();

        specialSkill.advanceCooldown();

        assertFalse(specialSkill.isAvailable());
        assertEquals(2, specialSkill.cooldownRemaining());
    }

    @Test
    void advanceCooldown_WhenFinalTurnElapses_MakesSkillAvailableAgain() {
        SpecialSkill specialSkill = specialSkillWithCooldown(1);
        specialSkill.triggerCooldown();

        specialSkill.advanceCooldown();

        assertTrue(specialSkill.isAvailable());
        assertEquals(0, specialSkill.cooldownRemaining());
    }

    private static SpecialSkill specialSkillWithCooldown(int cooldownTurns) {
        return new SpecialSkill(new ShieldBashAction(), cooldownTurns);
    }
}
