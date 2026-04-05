package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatantTest {
    @Test
    void combatantHpMutationsFlowThroughHitPointsValueObject() {
        Warrior warrior = new Warrior();

        warrior.receiveDamage(90);
        assertEquals(new HitPoints(170, 260), warrior.getHitPoints());

        warrior.heal(500);
        assertEquals(new HitPoints(260, 260), warrior.getHitPoints());
    }

    @Test
    void attackBuffChangesCurrentAttackWithoutMutatingBaseAttack() {
        Wizard wizard = new Wizard();

        wizard.adjustStat(StatType.ATTACK, 10);

        assertEquals(60, wizard.getAttack());
        assertEquals(50, wizard.getBaseAttack());
    }

    @Test
    void genericStatResolutionSupportsFutureStatModifiers() {
        Warrior warrior = new Warrior();

        warrior.adjustStat(StatType.SPEED, 5);

        assertEquals(35, warrior.getSpeed());
    }

    @Test
    void defendEffectTemporarilyRaisesDefense() {
        Warrior warrior = new Warrior();

        assertEquals(20, warrior.getDefense());
        warrior.addStatusEffect(new DefendStatusEffect(1));
        assertEquals(30, warrior.getDefense());

        warrior.statusEffects().onRoundCompleted();

        assertEquals(20, warrior.getDefense());
    }
}
