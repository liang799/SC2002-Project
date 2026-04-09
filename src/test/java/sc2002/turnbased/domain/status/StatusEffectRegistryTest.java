package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class StatusEffectRegistryTest {
    @Test
    void getTurnBlockReason_WhenStunBlocksNextTurn_MarksTurnBlockedAndExpiresEffect() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant().named("Owner").build();

        registry.add(owner, new StunStatusEffect(1));
        registry.consumeNotes();

        java.util.Optional<String> turnBlockReason = registry.getTurnBlockReason(owner);

        assertTrue(turnBlockReason.isPresent());
        assertEquals("STUNNED", turnBlockReason.get());
        assertEquals(
            List.of("Stun expired"),
            registry.consumeNotes()
        );
        assertEquals(List.of(), registry.activeStatuses(owner));
    }

    @Test
    void adjustIncomingDamage_WhenSmokeBombBlocksEnemyAttack_ReturnsZeroAndExpiresEffect() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant warrior = TestCombatantBuilder.aCombatant(() -> registry)
            .named("Warrior")
            .build();
        EnemyCombatant goblin = TestDependencies.goblin("Goblin");

        registry.add(warrior, new SmokeBombStatusEffect(1));
        registry.consumeNotes();

        DamageAdjustment adjustment = registry.adjustIncomingDamage(warrior, goblin, 15);

        assertEquals(0, adjustment.damage());
        assertEquals(
            List.of(
                "Smoke Bomb blocked the attack",
                "Smoke Bomb expired"
            ),
            adjustment.notes()
        );
        assertEquals(List.of(), warrior.getActiveStatuses());
    }
}
