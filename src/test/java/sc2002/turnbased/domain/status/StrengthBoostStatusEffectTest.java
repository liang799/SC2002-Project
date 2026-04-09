package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Stat;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestCombatStatsBuilder;

@Tag("unit")
class StrengthBoostStatusEffectTest {
    @Test
    void modifyStats_WhenRoundsRemain_AddsAttackForConfiguredDuration() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withAttack(40)
            .build();
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        StrengthBoostStatusEffect effect = new StrengthBoostStatusEffect(15, 3);

        CombatStats initialStats = effect.modifyStats(baseStats);
        String initialDescription = effect.description();
        effect.onRoundEnd(owner);
        CombatStats afterOneRound = effect.modifyStats(baseStats);

        assertAll(
            () -> assertEquals(new Stat(55), initialStats.attack()),
            () -> assertEquals(new Stat(55), afterOneRound.attack()),
            () -> assertEquals("STRENGTH BOOST +15", initialDescription),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void onRoundCompleted_WhenDurationElapses_ExpiresAndStopsBuffingAttack() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withAttack(40)
            .build();
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        StrengthBoostStatusEffect effect = new StrengthBoostStatusEffect(15, 1);

        effect.onRoundEnd(owner);
        CombatStats expiredStats = effect.modifyStats(baseStats);

        assertTrue(effect.isExpired());
        assertEquals(40, expiredStats.attack().value());
    }
}
