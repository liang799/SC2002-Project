package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Stat;
import sc2002.turnbased.domain.StatType;
import sc2002.turnbased.support.TestCombatantBuilder;
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

        assertEquals("ARCANE POWER +10", effect.description());
        assertEquals(new Stat(50), updatedStats.attack());
        assertEquals(new Stat(15), updatedStats.defense());
        assertEquals(new Stat(20), updatedStats.speed());
        assertFalse(effect.isExpired());
    }

    @Test
    void onRoundEnd_WhenMultipleRoundsPass_RemainsActiveAndKeepsAttackBonusUntilLevelEnds() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withAttack(40)
            .build();
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);
        Combatant owner = TestCombatantBuilder.aCombatant().build();

        effect.onRoundEnd(owner);
        CombatStats afterFirstRound = effect.modifyStats(baseStats);
        effect.onRoundEnd(owner);
        CombatStats afterSecondRound = effect.modifyStats(baseStats);

        assertAll(
            () -> assertEquals(new Stat(50), afterFirstRound.attack()),
            () -> assertEquals(new Stat(50), afterSecondRound.attack()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void mergeWith_WhenCombinedWithSameEffect_ReturnsSummedAttackBonus() {
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);
        ArcanePowerStatusEffect otherEffect = new ArcanePowerStatusEffect(20);

        Optional<StatusEffect> merged = effect.mergeWith(otherEffect);

        assertTrue(merged.isPresent());
        CombatStats updatedStats = merged.get().modifyStats(
            TestCombatStatsBuilder.combatStats()
                .withAttack(40)
                .build()
        );

        assertEquals("ARCANE POWER +30", merged.get().description());
        assertEquals(70, updatedStats.valueOf(StatType.ATTACK));
    }

    @Test
    void mergeWith_WhenOtherEffectIsNull_ThrowsNullPointerException() {
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);

        assertThrows(NullPointerException.class, () -> effect.mergeWith(null));
    }

    @Test
    void onApply_WhenApplied_ReportsReadableMessage() {
        ArcanePowerStatusEffect effect = new ArcanePowerStatusEffect(10);
        Combatant owner = TestCombatantBuilder.aCombatant().named("Wizard").build();

        assertEquals(
            List.of("Wizard gains ARCANE POWER +10"),
            effect.onApply(owner)
        );
    }
}
