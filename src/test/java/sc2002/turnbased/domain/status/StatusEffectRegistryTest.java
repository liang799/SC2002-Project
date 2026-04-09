package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestCombatStatsBuilder;
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

    @Test
    void adjustIncomingDamage_WhenEffectsDriveDamageBelowZero_ClampsToZeroAndPreservesNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();
        Combatant attacker = TestCombatantBuilder.aCombatant().named("Attacker").build();

        registry.add(owner, new StatusEffect() {
            private boolean expired;

            @Override
            public StatusEffectKind kind() {
                return StatusEffectKind.DEFEND;
            }

            @Override
            public String description() {
                return "NEGATIVE DAMAGE";
            }

            @Override
            public DamageAdjustment modifyIncomingDamage(Combatant effectOwner, Combatant effectAttacker, int damage) {
                expired = true;
                return new DamageAdjustment(-7, List.of("Damage reduced below zero"));
            }

            @Override
            public List<String> onExpire(Combatant effectOwner) {
                return List.of("Negative damage effect expired");
            }

            @Override
            public boolean isExpired() {
                return expired;
            }
        });
        registry.consumeNotes();

        DamageAdjustment adjustment = registry.adjustIncomingDamage(owner, attacker, 3);

        assertEquals(0, adjustment.damage());
        assertEquals(
            List.of(
                "Damage reduced below zero",
                "Negative damage effect expired"
            ),
            adjustment.notes()
        );
    }

    @Test
    void activeStatuses_WhenExpiredEffectIsPruned_DoesNotPublishExpiryNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();

        registry.add(owner, expiredEffect("Expired status", "Expired status removed"));

        assertEquals(List.of(), registry.activeStatuses(owner));
        assertEquals(List.of(), registry.consumeNotes());
    }

    @Test
    void apply_WhenExpiredEffectIsPruned_DoesNotPublishExpiryNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();
        CombatStats baseStats = TestCombatStatsBuilder.combatStats().build();

        registry.add(owner, expiredEffect("Expired status", "Expired status removed"));

        CombatStats effectiveStats = registry.apply(owner, baseStats);

        assertEquals(baseStats, effectiveStats);
        assertEquals(List.of(), registry.consumeNotes());
        assertEquals(List.of(), registry.activeStatuses(owner));
    }

    @Test
    void add_WhenExpiredEffectsArePruned_PublishesExpiryNotesBeforeNewApplyNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();

        registry.add(owner, expiredEffect("Expired status", "Expired status removed"));

        List<String> notes = registry.add(owner, activeEffect("Fresh status", "Fresh status applied"));

        assertEquals(
            List.of(
                "Expired status removed",
                "Fresh status applied"
            ),
            notes
        );
        assertEquals(List.of("Fresh status"), registry.activeStatuses(owner));
        assertEquals(List.of(), registry.consumeNotes());
    }

    private static StatusEffect expiredEffect(String description, String expiryNote) {
        return new StatusEffect() {
            @Override
            public StatusEffectKind kind() {
                return StatusEffectKind.DEFEND;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> onExpire(Combatant owner) {
                return List.of(expiryNote);
            }

            @Override
            public boolean isExpired() {
                return true;
            }
        };
    }

    private static StatusEffect activeEffect(String description, String applyNote) {
        return new StatusEffect() {
            @Override
            public StatusEffectKind kind() {
                return StatusEffectKind.ARCANE_POWER;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> onApply(Combatant owner) {
                return List.of(applyNote);
            }

            @Override
            public boolean isExpired() {
                return false;
            }
        };
    }
}
