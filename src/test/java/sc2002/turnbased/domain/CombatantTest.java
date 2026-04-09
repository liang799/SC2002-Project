package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.status.ArcanePowerStatusEffect;
import sc2002.turnbased.domain.status.CombatantStatusOutcome;
import sc2002.turnbased.domain.status.DamageModifier;
import sc2002.turnbased.domain.status.DamageModifierType;
import sc2002.turnbased.domain.status.DefendStatusEffect;
import sc2002.turnbased.domain.status.SmokeBombStatusEffect;
import sc2002.turnbased.domain.status.StatusEffectChange;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class CombatantTest {
    @Test
    void receiveDamageAndHeal_WhenHitPointsChange_UpdatesHitPointsValueObject() {
        PlayerCharacter warrior = TestDependencies.warrior();

        warrior.receiveDamage(90);
        HitPoints damagedHitPoints = warrior.getHitPoints();
        warrior.heal(500);
        HitPoints healedHitPoints = warrior.getHitPoints();

        assertEquals(new HitPoints(170, 260), damagedHitPoints);
        assertEquals(new HitPoints(260, 260), healedHitPoints);
    }

    @Test
    void addStatusEffect_WhenArcanePowerIsApplied_ReturnsBuffedAttackWithoutChangingBaseAttack() {
        PlayerCharacter wizard = TestDependencies.wizard();

        wizard.addStatusEffect(new ArcanePowerStatusEffect(10));
        int currentAttack = wizard.getAttack();
        int baseAttack = wizard.getBaseAttack();

        assertEquals(60, currentAttack);
        assertEquals(50, baseAttack);
    }

    @Test
    void addStatusEffect_WhenArcanePowerEffectsStack_ReturnsMergedAttackBonus() {
        PlayerCharacter warrior = TestDependencies.warrior();

        warrior.addStatusEffect(new ArcanePowerStatusEffect(10));
        warrior.addStatusEffect(new ArcanePowerStatusEffect(10));
        int attack = warrior.getAttack();

        assertEquals(60, attack);
        assertEquals(List.of("ARCANE POWER +20"), warrior.getActiveStatuses());
    }

    @Test
    void addStatusEffect_WhenDefendEffectIsActive_ReturnsTemporarilyIncreasedDefense() {
        PlayerCharacter warrior = TestDependencies.warrior();
        int defenseBeforeDefend = warrior.getDefense();

        warrior.addStatusEffect(new DefendStatusEffect(1));
        int defenseDuringDefend = warrior.getDefense();
        warrior.completeRound();
        int defenseAfterDefend = warrior.getDefense();

        assertEquals(20, defenseBeforeDefend);
        assertEquals(30, defenseDuringDefend);
        assertEquals(20, defenseAfterDefend);
    }

    @Test
    void attack_WhenCombatantAttacksTarget_ResolvesCombatInsideCombatant() {
        Combatant attacker = TestCombatantBuilder.aCombatant()
            .named("Warrior")
            .withAttack(40)
            .build();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withCurrentHp(70)
            .withDefense(15)
            .build();

        AttackResolution attackResolution = attacker.attack(target);

        assertEquals(25, attackResolution.damage());
        assertEquals(40, attackResolution.attackUsed());
        assertEquals(15, attackResolution.targetDefense());
        assertEquals(70, attackResolution.hpBefore());
        assertEquals(45, attackResolution.hpAfter());
        assertEquals(45, target.getCurrentHp());
    }

    @Test
    void attack_WhenTargetHasSmokeBomb_AppliesDefensiveStatusEffectsInsideCombatant() {
        Combatant attacker = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withAttack(35)
            .build();
        Combatant target = TestCombatantBuilder.aCombatant()
            .named("Warrior")
            .build();
        target.addStatusEffect(new SmokeBombStatusEffect(1));

        AttackResolution attackResolution = attacker.attack(target);

        assertEquals(0, attackResolution.damage());
        assertEquals(100, target.getCurrentHp());
        assertEquals(
            List.of(
                new CombatantStatusOutcome(
                    CombatantId.of("Warrior"),
                    new DamageModifier(StatusEffectKind.SMOKE_BOMB, DamageModifierType.BLOCKED)
                ),
                new CombatantStatusOutcome(
                    CombatantId.of("Warrior"),
                    StatusEffectChange.expired(StatusEffectKind.SMOKE_BOMB)
                )
            ),
            attackResolution.statusEffectOutcomes()
        );
    }
}
