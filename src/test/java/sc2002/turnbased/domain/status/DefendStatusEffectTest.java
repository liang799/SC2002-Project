package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Stat;

@Tag("unit")
class DefendStatusEffectTest {
    @Test
    void modifyStats_whenEffectIsActive_addsDefenseBonus() {
        CombatStats baseStats = CombatStats.builder()
            .attack(40)
            .defense(15)
            .speed(20)
            .build();
        DefendStatusEffect effect = new DefendStatusEffect(2);

        CombatStats updatedStats = effect.modifyStats(baseStats);

        assertEquals("DEFENDING", effect.name());
        assertEquals(new Stat(40), updatedStats.attack());
        assertEquals(new Stat(25), updatedStats.defense());
        assertEquals(new Stat(20), updatedStats.speed());
        assertFalse(effect.isExpired());
    }

    @Test
    void onRoundCompleted_whenDurationEnds_effectExpiresAndStopsModifyingStats() {
        CombatStats baseStats = CombatStats.builder()
            .attack(40)
            .defense(15)
            .speed(20)
            .build();
        DefendStatusEffect effect = new DefendStatusEffect(1);

        effect.onRoundCompleted();
        CombatStats updatedStats = effect.modifyStats(baseStats);

        assertTrue(effect.isExpired());
        assertEquals(baseStats, updatedStats);
    }
}
