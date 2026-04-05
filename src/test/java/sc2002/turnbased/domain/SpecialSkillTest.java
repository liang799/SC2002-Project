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
    void specialSkillTracksCooldownOutsideCombatant() {
        SpecialSkill specialSkill = new SpecialSkill(new ShieldBashAction(), 3);

        assertTrue(specialSkill.isAvailable());

        specialSkill.triggerCooldown();

        assertFalse(specialSkill.isAvailable());
        assertEquals(3, specialSkill.cooldownRemaining());

        specialSkill.advanceCooldown();
        specialSkill.advanceCooldown();
        specialSkill.advanceCooldown();

        assertTrue(specialSkill.isAvailable());
        assertEquals(0, specialSkill.cooldownRemaining());
    }
}
