package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class SmokeBombStatusEffectTest {
    @Test
    void adjustIncomingDamage_whenOwnerTargetsSelf_leavesDamageUnchanged() {
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);

        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, owner, 15);

        assertEquals("SMOKE BOMB", effect.name());
        assertEquals(15, adjustment.damage());
        assertEquals(List.of(), adjustment.notes());
        assertFalse(effect.isExpired());
    }

    @Test
    void adjustIncomingDamage_whenEnemyAttackConsumesLastCharge_blocksDamageAndExpires() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Attacker").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(1);

        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15);

        assertEquals(0, adjustment.damage());
        assertEquals(List.of("Smoke Bomb active", "Smoke Bomb effect expires"), adjustment.notes());
        assertTrue(effect.isExpired());
    }

    @Test
    void adjustIncomingDamage_whenChargesRemain_afterFirstBlockEffectStaysActive() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Attacker").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);

        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15);

        assertEquals(0, adjustment.damage());
        assertEquals(List.of("Smoke Bomb active"), adjustment.notes());
        assertFalse(effect.isExpired());
    }
}
