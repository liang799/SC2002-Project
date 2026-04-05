package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatStatsTest {
    @Test
    void valueOf_whenStatExists_returnsConfiguredValue() {
        CombatStats combatStats = createCombatStats();

        int attack = combatStats.valueOf(StatType.ATTACK);

        assertEquals(45, attack);
    }

    @Test
    void valueOf_whenStatIsMissing_returnsZero() {
        CombatStats combatStats = new CombatStats(
            new HitPoints(120, 120),
            Map.of(
                StatType.ATTACK, new Stat(45)
            )
        );

        int speed = combatStats.valueOf(StatType.SPEED);

        assertEquals(0, speed);
    }

    @Test
    void withHitPoints_whenReplacingHitPoints_keepsExistingStats() {
        CombatStats combatStats = createCombatStats();
        HitPoints updatedHitPoints = new HitPoints(70, 120);

        CombatStats updated = combatStats.withHitPoints(updatedHitPoints);

        assertEquals(updatedHitPoints, updated.hitPoints());
        assertEquals(combatStats.stats(), updated.stats());
    }

    @Test
    void withStat_whenReplacingAttack_keepsOtherFieldsUnchanged() {
        CombatStats combatStats = createCombatStats();

        CombatStats updated = combatStats.withStat(StatType.ATTACK, new Stat(55));

        assertEquals(combatStats.hitPoints(), updated.hitPoints());
        assertEquals(new Stat(55), updated.stats().get(StatType.ATTACK));
        assertEquals(combatStats.stats().get(StatType.DEFENSE), updated.stats().get(StatType.DEFENSE));
        assertEquals(combatStats.stats().get(StatType.SPEED), updated.stats().get(StatType.SPEED));
    }

    private static CombatStats createCombatStats() {
        return new CombatStats(
            new HitPoints(120, 120),
            Map.of(
                StatType.ATTACK, new Stat(45),
                StatType.DEFENSE, new Stat(15),
                StatType.SPEED, new Stat(25)
            )
        );
    }
}
