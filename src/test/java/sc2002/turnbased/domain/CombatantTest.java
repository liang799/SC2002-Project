package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatantTest {
    @Test
    void receiveDamage_whenDamageIsApplied_updatesHitPointsByDamageAmount() {
        Combatant combatant = testCombatant(100, 100, 40, 20, 30);
        HitPoints initialHitPoints = combatant.getHitPoints();

        combatant.receiveDamage(35);

        assertEquals(
            new HitPoints(initialHitPoints.current() - 35, initialHitPoints.max()),
            combatant.getHitPoints()
        );
    }

    @Test
    void heal_whenHealingWouldExceedMaxHp_capsCurrentHpAtMax() {
        Combatant combatant = testCombatant(80, 100, 40, 20, 30);

        combatant.heal(50);

        assertEquals(new HitPoints(100, 100), combatant.getHitPoints());
    }

    @Test
    void adjustStat_whenAttackBuffIsApplied_increasesResolvedAttackWithoutChangingBaseAttack() {
        Combatant combatant = testCombatant(100, 100, 40, 20, 30);
        int baseAttack = combatant.getBaseAttack();

        combatant.adjustStat(StatType.ATTACK, 10);

        assertEquals(baseAttack + 10, combatant.getAttack());
        assertEquals(baseAttack, combatant.getBaseAttack());
    }

    @Test
    void getSpeed_whenPersistentModifierIsApplied_returnsModifiedSpeed() {
        Combatant combatant = testCombatant(100, 100, 40, 20, 30);
        int baseSpeed = combatant.getSpeed();

        combatant.adjustStat(StatType.SPEED, 5);

        assertEquals(baseSpeed + 5, combatant.getSpeed());
    }

    @Test
    void getDefense_whenDefendStatusEffectIsActive_includesTemporaryDefenseBonus() {
        Combatant combatant = testCombatant(100, 100, 40, 20, 30);
        int baseDefense = combatant.getDefense();

        combatant.addStatusEffect(new DefendStatusEffect(1));

        assertEquals(baseDefense + 10, combatant.getDefense());
    }

    @Test
    void getDefense_whenDefendStatusEffectExpires_returnsToBaseDefense() {
        Combatant combatant = testCombatant(100, 100, 40, 20, 30);
        int baseDefense = combatant.getDefense();

        combatant.addStatusEffect(new DefendStatusEffect(1));
        combatant.statusEffects().onRoundCompleted();

        assertEquals(baseDefense, combatant.getDefense());
    }

    private static Combatant testCombatant(int currentHp, int maxHp, int attack, int defense, int speed) {
        return new TestCombatant(
            "Test Combatant",
            CombatStats.of(
                new HitPoints(currentHp, maxHp),
                new Stat(attack),
                new Stat(defense),
                new Stat(speed)
            )
        );
    }

    private static final class TestCombatant extends Combatant {
        private TestCombatant(String name, CombatStats baseStats) {
            super(name, baseStats);
        }
    }
}
