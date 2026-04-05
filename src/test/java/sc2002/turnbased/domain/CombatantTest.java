package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sc2002.turnbased.support.TestCombatantBuilder.aCombatant;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatantTest {
    @Test
    void receiveDamage_whenDamageIsApplied_updatesHitPointsByDamageAmount() {
        Combatant combatant = aCombatant().build();
        HitPoints initialHitPoints = combatant.getHitPoints();

        combatant.receiveDamage(35);

        assertEquals(
            new HitPoints(initialHitPoints.current() - 35, initialHitPoints.max()),
            combatant.getHitPoints()
        );
    }

    @Test
    void heal_whenHealingWouldExceedMaxHp_capsCurrentHpAtMax() {
        Combatant combatant = aCombatant()
            .withCurrentHp(80)
            .build();

        combatant.heal(50);

        assertEquals(new HitPoints(100, 100), combatant.getHitPoints());
    }

    @Test
    void adjustStat_whenAttackBuffIsApplied_increasesResolvedAttackWithoutChangingBaseAttack() {
        Combatant combatant = aCombatant().build();
        int baseAttack = combatant.getBaseAttack();

        combatant.adjustStat(StatType.ATTACK, 10);

        assertEquals(baseAttack + 10, combatant.getAttack());
        assertEquals(baseAttack, combatant.getBaseAttack());
    }

    @Test
    void getSpeed_whenPersistentModifierIsApplied_returnsModifiedSpeed() {
        Combatant combatant = aCombatant().build();
        int baseSpeed = combatant.getSpeed();

        combatant.adjustStat(StatType.SPEED, 5);

        assertEquals(baseSpeed + 5, combatant.getSpeed());
    }

    @Test
    void getDefense_whenDefendStatusEffectIsActive_includesTemporaryDefenseBonus() {
        Combatant combatant = aCombatant().build();
        int baseDefense = combatant.getDefense();

        combatant.addStatusEffect(new DefendStatusEffect(1));

        assertEquals(baseDefense + 10, combatant.getDefense());
    }

    @Test
    void getDefense_whenDefendStatusEffectExpires_returnsToBaseDefense() {
        Combatant combatant = aCombatant().build();
        int baseDefense = combatant.getDefense();

        combatant.addStatusEffect(new DefendStatusEffect(1));
        combatant.statusEffects().onRoundCompleted();

        assertEquals(baseDefense, combatant.getDefense());
    }
}
