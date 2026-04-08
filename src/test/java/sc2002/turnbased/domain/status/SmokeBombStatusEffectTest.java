package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class SmokeBombStatusEffectTest {
    @Test
    void adjustIncomingDamage_whenOwnerTargetsSelf_leavesDamageUnchanged() {
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);
        StatusEffectEventPublisher eventPublisher = TestDependencies.statusEffectEventPublisher();

        try (StatusEffectObservationScope observation = new StatusEffectObservationScope(eventPublisher)) {
            DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, owner, 15, eventPublisher);

            assertEquals("SMOKE BOMB", effect.name());
            assertEquals(15, adjustment.damage());
            assertEquals(List.of(), observation.observedEvents());
            assertFalse(effect.isExpired());
        }
    }

    @Test
    void adjustIncomingDamage_whenEnemyAttackConsumesLastCharge_blocksDamageAndExpires() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Attacker").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(1);
        StatusEffectEventPublisher eventPublisher = TestDependencies.statusEffectEventPublisher();

        try (StatusEffectObservationScope observation = new StatusEffectObservationScope(eventPublisher)) {
            DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);

            assertEquals(0, adjustment.damage());
            assertEquals(List.of(new SmokeBombActivatedEvent("Owner", "Attacker", 0)), observation.observedEvents());
            assertTrue(effect.isExpired());
        }
    }

    @Test
    void adjustIncomingDamage_whenChargesRemain_afterFirstBlockEffectStaysActive() {
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Attacker").build();
        SmokeBombStatusEffect effect = new SmokeBombStatusEffect(2);
        StatusEffectEventPublisher eventPublisher = TestDependencies.statusEffectEventPublisher();

        try (StatusEffectObservationScope observation = new StatusEffectObservationScope(eventPublisher)) {
            DamageAdjustment adjustment = effect.adjustIncomingDamage(owner, attacker, 15, eventPublisher);

            assertEquals(0, adjustment.damage());
            assertEquals(List.of(new SmokeBombActivatedEvent("Owner", "Attacker", 1)), observation.observedEvents());
            assertFalse(effect.isExpired());
        }
    }
}
