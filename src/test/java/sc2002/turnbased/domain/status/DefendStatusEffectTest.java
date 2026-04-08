package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Stat;
import sc2002.turnbased.support.TestCombatStatsBuilder;

@Tag("unit")
class DefendStatusEffectTest {
    @Test
    void modifyStats_WhenDefendCoversCurrentAndNextTurn_AddsDefenseBonusAcrossBothRounds() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withDefense(15)
            .build();
        DefendStatusEffect effect = new DefendStatusEffect(2);

        CombatStats currentTurnStats = effect.modifyStats(baseStats);
        effect.onRoundCompleted();
        CombatStats nextTurnStats = effect.modifyStats(baseStats);

        assertAll(
            () -> assertEquals("DEFENDING", effect.name()),
            () -> assertEquals(new Stat(40), currentTurnStats.attack()),
            () -> assertEquals(new Stat(25), currentTurnStats.defense()),
            () -> assertEquals(new Stat(20), currentTurnStats.speed()),
            () -> assertEquals(new Stat(25), nextTurnStats.defense()),
            () -> assertFalse(effect.isExpired())
        );
    }

    @Test
    void onRoundCompleted_WhenCurrentAndNextTurnHavePassed_ExpiresAndStopsModifyingStats() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withDefense(15)
            .build();
        DefendStatusEffect effect = new DefendStatusEffect(2);

        effect.onRoundCompleted();
        effect.onRoundCompleted();
        CombatStats updatedStats = effect.modifyStats(baseStats);

        assertTrue(effect.isExpired());
        assertEquals(baseStats, updatedStats);
    }
}
