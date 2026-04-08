package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void modifyStats_attackBuffApplied_returnsBuffedAttackWithoutChangingBaseAttack() {
        // arrange
        Wizard wizard = new Wizard();

        // act
        wizard.modifyStats(stats -> stats.addFlat(StatType.ATTACK, 10));
        int currentAttack = wizard.getAttack();
        int baseAttack = wizard.getBaseAttack();

        // assert
        assertEquals(60, currentAttack);
        assertEquals(50, baseAttack);
    }

    @Test
    void modifyStats_speedMultiplierApplied_returnsScaledSpeed() {
        // arrange
        Warrior warrior = new Warrior();

        // act
        warrior.modifyStats(stats -> stats.multiplyBy(StatType.SPEED, 2));
        int speed = warrior.getSpeed();

        // assert
        assertEquals(60, speed);
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
