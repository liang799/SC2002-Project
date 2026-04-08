package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.domain.status.event.StatusEffectExpiredEvent;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class StatusEffectRegistryTest {
    @Test
    void resolveTurnWindow_whenStunBlocksNextTurn_marksTurnBlockedAndExpiresEffect() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();

        registry.add(owner, new StunStatusEffect(1));

        try (StatusEffectObservationScope observation = registry.openObservation()) {
            TurnWindow turnWindow = registry.resolveTurnWindow(owner);

            assertTrue(turnWindow.isBlocked());
            assertEquals("STUNNED", turnWindow.getBlockerLabel());
            assertEquals(List.of(new StatusEffectExpiredEvent("Owner", StatusEffectKind.STUN)), observation.observedEvents());
            assertEquals(List.of(), registry.activeStatusNames("Owner", true));
        }
    }

    @Test
    void adjustIncomingDamage_whenSmokeBombBlocksEnemyAttack_returnsZeroAndExpiresEffect() {
        PlayerCharacter warrior = TestDependencies.warrior();
        EnemyCombatant goblin = TestDependencies.goblin("Goblin");

        warrior.addStatusEffect(new SmokeBombStatusEffect(1));

        try (StatusEffectObservationScope observation = warrior.statusEffects().openObservation()) {
            DamageAdjustment adjustment = warrior.statusEffects().adjustIncomingDamage(warrior, goblin, 15);

            assertEquals(0, adjustment.damage());
            assertEquals(
                List.of(
                    new SmokeBombActivatedEvent("Warrior", "Goblin", 0),
                    new StatusEffectExpiredEvent("Warrior", StatusEffectKind.SMOKE_BOMB)
                ),
                observation.observedEvents()
            );
            assertEquals(List.of(), warrior.getActiveStatusNames());
        }
    }
}
