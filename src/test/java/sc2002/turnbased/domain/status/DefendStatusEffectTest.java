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
class DefendStatusEffectTest {
    @Test
    void modifyStats_WhenDefendCoversCurrentAndNextTurn_AddsDefenseBonusAcrossBothRounds() {
        CombatStats baseStats = TestCombatStatsBuilder.combatStats()
            .withDefense(15)
            .build();
        DefendStatusEffect effect = new DefendStatusEffect(2);
        Combatant owner = TestCombatantBuilder.aCombatant().build();

        CombatStats currentTurnStats = effect.modifyStats(baseStats);
        String initialDescription = effect.description();
        effect.onRoundEnd(owner);
        CombatStats nextTurnStats = effect.modifyStats(baseStats);

        assertAll(
            () -> assertEquals("DEFENDING", initialDescription),
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
        Combatant owner = TestCombatantBuilder.aCombatant().build();

        effect.onRoundEnd(owner);
        effect.onRoundEnd(owner);
        CombatStats updatedStats = effect.modifyStats(baseStats);

        assertTrue(effect.isExpired());
        assertEquals(baseStats, updatedStats);
    }
}
