package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatantTest {
    @Test
    void receiveDamageAndHeal_existingHitPoints_updatesHitPointsValueObject() {
        // arrange
        Warrior warrior = new Warrior();

        // act
        warrior.receiveDamage(90);
        HitPoints damagedHitPoints = warrior.getHitPoints();
        warrior.heal(500);
        HitPoints healedHitPoints = warrior.getHitPoints();

        // assert
        assertEquals(new HitPoints(170, 260), damagedHitPoints);
        assertEquals(new HitPoints(260, 260), healedHitPoints);
    }

    @Test
    void addStatusEffect_arcanePowerBuffApplied_returnsBuffedAttackWithoutChangingBaseAttack() {
        // arrange
        Wizard wizard = new Wizard();

        // act
        wizard.addStatusEffect(new ArcanePowerStatusEffect(10));
        int currentAttack = wizard.getAttack();
        int baseAttack = wizard.getBaseAttack();

        // assert
        assertEquals(60, currentAttack);
        assertEquals(50, baseAttack);
    }

    @Test
    void addStatusEffect_arcanePowerEffectsStack_returnsMergedAttackBonus() {
        // arrange
        Warrior warrior = new Warrior();

        // act
        warrior.addStatusEffect(new ArcanePowerStatusEffect(10));
        warrior.addStatusEffect(new ArcanePowerStatusEffect(10));
        int attack = warrior.getAttack();

        // assert
        assertEquals(60, attack);
        assertEquals(List.of("ARCANE POWER +20"), warrior.getActiveStatusNames());
    }

    @Test
    void addStatusEffect_defendEffectActive_returnsTemporarilyIncreasedDefense() {
        // arrange
        Warrior warrior = new Warrior();
        int defenseBeforeDefend = warrior.getDefense();

        // act
        warrior.addStatusEffect(new DefendStatusEffect(1));
        int defenseDuringDefend = warrior.getDefense();
        warrior.statusEffects().onRoundCompleted();
        int defenseAfterDefend = warrior.getDefense();

        // assert
        assertEquals(20, defenseBeforeDefend);
        assertEquals(30, defenseDuringDefend);
        assertEquals(20, defenseAfterDefend);
    }
}
