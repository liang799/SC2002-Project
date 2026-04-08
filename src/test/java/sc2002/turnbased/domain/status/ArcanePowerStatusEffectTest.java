package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Stat;
import sc2002.turnbased.domain.StatType;
import sc2002.turnbased.support.TestCombatStatsBuilder;

@Tag("unit")
class ArcanePowerStatusEffectTest {
    @Test
    void constructor_WhenAttackBonusIsNotPositive_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ArcanePowerStatusEffect(0));
    }

    @Test
    void modifyStats_WhenApplied_AddsAttackWithoutChangingOtherStats() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withAttack(40)
            .build();
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);

        CombatStats updatedStats = effect.modifyStats(baseStats);

        assertEquals("ARCANE POWER +10", effect.name());
        assertEquals(new Stat(50), updatedStats.attack());
        assertEquals(new Stat(15), updatedStats.defense());
        assertEquals(new Stat(20), updatedStats.speed());
        assertFalse(effect.isExpired());
    }

    @Test
    void onRoundCompleted_WhenMultipleRoundsPass_RemainsActiveAndKeepsAttackBonusUntilLevelEnds() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withAttack(40)
            .build();
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);

        effect.onRoundCompleted();
        CombatStats afterFirstRound = effect.modifyStats(baseStats);
        effect.onRoundCompleted();
        CombatStats afterSecondRound = effect.modifyStats(baseStats);

        assertAll(
            () -> assertEquals(new Stat(50), afterFirstRound.attack()),
            () -> assertEquals(new Stat(50), afterSecondRound.attack()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void merge_WhenCombinedWithSameEffect_ReturnsSummedAttackBonus() {
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);
        ArcanePowerStatusEffect otherEffect = new ArcanePowerStatusEffect(20);

        StatusEffect merged = effect.merge(otherEffect);
        CombatStats updatedStats = ((StatModifierEffect) merged).modifyStats(
            TestCombatStatsBuilder.combatStats()
                .withAttack(40)
                .build()
        );

        assertTrue(effect.canMergeWith(otherEffect));
        assertEquals("ARCANE POWER +30", merged.name());
        assertEquals(70, updatedStats.valueOf(StatType.ATTACK));
    }
}
