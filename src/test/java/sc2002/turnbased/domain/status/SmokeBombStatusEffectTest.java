package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.support.FakeStatusEffectEventPublisher;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class SmokeBombStatusEffectTest {
    @Test
    void adjustIncomingDamage_WhenOwnerTargetsSelf_LeavesDamageUnchanged() {
        // Arrange
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        // Act
        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, owner, 15, eventPublisher);

        // Assert
        assertAll(
            () -> assertEquals(15, adjustment.damage()),
            () -> assertEquals(List.of(), eventPublisher.publishedEvents()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenEffectIsAlreadyExpired_LeavesDamageUnchangedAndPublishesNoEvent() {
        // Arrange
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        Combatant attacker = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(0);
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        // Act
        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);

        // Assert
        assertAll(
            () -> assertEquals(15, adjustment.damage()),
            () -> assertEquals(List.of(), eventPublisher.publishedEvents()),
            () -> assertTrue(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenEnemyAttackConsumesLastCharge_BlocksDamageAndExpires() {
        // Arrange
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        Combatant attacker = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(1);
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        // Act
        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);

        // Assert
        assertAll(
            () -> assertEquals(0, adjustment.damage()),
            () -> assertTrue(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenChargesRemainAfterFirstBlock_StaysActive() {
        // Arrange
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        Combatant attacker = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        // Act
        DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);

        // Assert
        assertAll(
            () -> assertEquals(0, adjustment.damage()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void adjustIncomingDamage_WhenCurrentAndNextEnemyAttackAreProtected_BlocksTwoHitsThenExpires() {
        // Arrange
        Combatant owner = TestCombatantBuilder.aCombatant().named("Warrior").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Goblin").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        // Act
        DamageAdjustment firstAdjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);
        DamageAdjustment secondAdjustment = effect.adjustIncomingDamage(owner, attacker, 20, eventPublisher);
        DamageAdjustment thirdAdjustment = effect.adjustIncomingDamage(owner, attacker, 25, eventPublisher);

        // Assert
        assertAll(
            () -> assertEquals(0, firstAdjustment.damage()),
            () -> assertEquals(0, secondAdjustment.damage()),
            () -> assertEquals(25, thirdAdjustment.damage()),
            () -> assertEquals(
                List.of(
                    new SmokeBombActivatedEvent("Warrior", "Goblin", 1),
                    new SmokeBombActivatedEvent("Warrior", "Goblin", 0)
                ),
                eventPublisher.publishedEvents()
            ),
            () -> assertTrue(effect.isExpired())
        );
    }
}
