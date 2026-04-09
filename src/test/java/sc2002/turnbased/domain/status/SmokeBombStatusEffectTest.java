package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
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
    void adjustIncomingDamage_WhenOwnerTargetsSelf_LeavesDamageUnchanged() {
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);

        DamageAdjustment adjustment = effect.modifyIncomingDamage(owner, owner, 15);

        assertAll(
            () -> assertEquals(15, adjustment.damage()),
            () -> assertEquals(List.of(), adjustment.notes()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenEffectIsAlreadyExpired_LeavesDamageUnchangedAndPublishesNoEvent() {
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        Combatant attacker = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(0);

        DamageAdjustment adjustment = effect.modifyIncomingDamage(owner, attacker, 15);

        assertAll(
            () -> assertEquals(15, adjustment.damage()),
            () -> assertEquals(List.of(), adjustment.notes()),
            () -> assertTrue(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenEnemyAttackConsumesLastCharge_BlocksDamageAndExpires() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Warrior").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Goblin").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(1);

        DamageAdjustment adjustment = effect.modifyIncomingDamage(owner, attacker, 15);

        assertAll(
            () -> assertEquals(0, adjustment.damage()),
            () -> assertEquals(List.of("Smoke Bomb blocked the attack"), adjustment.notes()),
            () -> assertTrue(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenChargesRemainAfterFirstBlock_StaysActive() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Warrior").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Goblin").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);

        DamageAdjustment adjustment = effect.modifyIncomingDamage(owner, attacker, 15);

        assertAll(
            () -> assertEquals(0, adjustment.damage()),
            () -> assertEquals(List.of("Smoke Bomb blocked the attack"), adjustment.notes()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenCurrentAndNextEnemyAttackAreProtected_BlocksTwoHitsThenExpires() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Warrior").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Goblin").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);

        DamageAdjustment firstAdjustment = effect.modifyIncomingDamage(owner, attacker, 15);
        DamageAdjustment secondAdjustment = effect.modifyIncomingDamage(owner, attacker, 20);
        DamageAdjustment thirdAdjustment = effect.modifyIncomingDamage(owner, attacker, 25);

        assertAll(
            () -> assertEquals(0, firstAdjustment.damage()),
            () -> assertEquals(0, secondAdjustment.damage()),
            () -> assertEquals(25, thirdAdjustment.damage()),
            () -> assertEquals(
                List.of("Smoke Bomb blocked the attack", "Smoke Bomb blocked the attack"),
                List.of(firstAdjustment.notes().get(0), secondAdjustment.notes().get(0))
            ),
            () -> assertEquals(List.of(), thirdAdjustment.notes()),
            () -> assertTrue(effect.isExpired())
        );
    }
}
