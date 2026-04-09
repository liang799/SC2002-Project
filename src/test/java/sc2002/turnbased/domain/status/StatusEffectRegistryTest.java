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
        registry.consumeOutcomes();

        java.util.Optional<String> turnBlockReason = registry.getTurnBlockReason(owner);

        assertTrue(turnBlockReason.isPresent());
        assertEquals("STUNNED", turnBlockReason.get());
        assertEquals(
            List.of(StatusEffectChange.expired(StatusEffectKind.STUN)),
            registry.consumeOutcomes()
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
        registry.consumeOutcomes();

        DamageAdjustment adjustment = registry.adjustIncomingDamage(warrior, goblin, 15);

        assertEquals(0, adjustment.damage());
        assertEquals(
            List.of(new DamageModifier(StatusEffectKind.SMOKE_BOMB, DamageModifierType.BLOCKED)),
            adjustment.modifiers()
        );
        assertEquals(
            List.of(StatusEffectChange.expired(StatusEffectKind.SMOKE_BOMB)),
            registry.consumeOutcomes()
        );
        assertEquals(List.of(), warrior.getActiveStatuses());
    }

    @Test
    void adjustIncomingDamage_WhenEffectsDriveDamageBelowZero_ClampsToZeroAndPreservesOutcomes() {
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
                return new DamageAdjustment(
                    -7,
                    List.of(new DamageModifier(StatusEffectKind.DEFEND, DamageModifierType.REDUCED))
                );
            }

            @Override
            public List<StatusEffectOutcome> onExpire(Combatant effectOwner) {
                return List.of(StatusEffectChange.expired(StatusEffectKind.DEFEND));
            }

            @Override
            public boolean isExpired() {
                return expired;
            }
        });
        registry.consumeOutcomes();

        DamageAdjustment adjustment = registry.adjustIncomingDamage(owner, attacker, 3);

        assertEquals(0, adjustment.damage());
        assertEquals(
            List.of(new DamageModifier(StatusEffectKind.DEFEND, DamageModifierType.REDUCED)),
            adjustment.modifiers()
        );
        assertEquals(
            List.of(StatusEffectChange.expired(StatusEffectKind.DEFEND)),
            registry.consumeOutcomes()
        );
    }

    @Test
    void activeStatuses_WhenExpiredEffectIsPruned_DoesNotPublishExpiryNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();

        registry.add(owner, expiredEffect("Expired status"));

        assertEquals(List.of(), registry.activeStatuses(owner));
        assertEquals(List.of(), registry.consumeOutcomes());
    }

    @Test
    void apply_WhenExpiredEffectIsPruned_DoesNotPublishExpiryNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();
        CombatStats baseStats = TestCombatStatsBuilder.combatStats().build();

        registry.add(owner, expiredEffect("Expired status"));

        CombatStats effectiveStats = registry.apply(owner, baseStats);

        assertEquals(baseStats, effectiveStats);
        assertEquals(List.of(), registry.consumeOutcomes());
        assertEquals(List.of(), registry.activeStatuses(owner));
    }

    @Test
    void add_WhenExpiredEffectsArePruned_PublishesExpiryNotesBeforeNewApplyNotes() {
        StatusEffectRegistry registry = TestDependencies.registry();
        Combatant owner = TestCombatantBuilder.aCombatant(() -> registry).named("Owner").build();

        registry.add(owner, expiredEffect("Expired status"));

        List<StatusEffectOutcome> outcomes = registry.add(owner, activeEffect("Fresh status"));

        assertEquals(
            List.of(
                StatusEffectChange.expired(StatusEffectKind.DEFEND),
                StatusEffectChange.applied(StatusEffectKind.ARCANE_POWER)
            ),
            outcomes
        );
        assertEquals(List.of("Fresh status"), registry.activeStatuses(owner));
        assertEquals(List.of(), registry.consumeOutcomes());
    }

    private static StatusEffect expiredEffect(String description) {
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
            public List<StatusEffectOutcome> onExpire(Combatant owner) {
                return List.of(StatusEffectChange.expired(kind()));
            }

            @Override
            public boolean isExpired() {
                return true;
            }
        };
    }

    private static StatusEffect activeEffect(String description) {
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
            public List<StatusEffectOutcome> onApply(Combatant owner) {
                return List.of(StatusEffectChange.applied(kind()));
            }

            @Override
            public boolean isExpired() {
                return false;
            }
        };
    }
}
