package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatStatsTest {
    @Test
    void of_validStats_returnsFixedValueObjects() {
        // arrange
        CombatStats combatStats = CombatStats.of(
            new HitPoints(120, 120),
            new Stat(45),
            new Stat(15),
            new Stat(25)
        );

        // act
        HitPoints hitPoints = combatStats.hitPoints();
        Stat attack = combatStats.attack();
        Stat defense = combatStats.defense();
        Stat speed = combatStats.speed();

        // assert
        assertEquals(new HitPoints(120, 120), hitPoints);
        assertEquals(new Stat(45), attack);
        assertEquals(new Stat(15), defense);
        assertEquals(new Stat(25), speed);
    }

    @Test
    void withHitPointsAndWithStat_requestedUpdates_returnsUpdatedCopy() {
        // arrange
        CombatStats combatStats = CombatStats.of(
            new HitPoints(120, 120),
            new Stat(45),
            new Stat(15),
            new Stat(25)
        );

        // act
        CombatStats updated = combatStats.withHitPoints(new HitPoints(70, 120))
            .withStat(StatType.ATTACK, new Stat(55));

        // assert
        assertEquals(new HitPoints(70, 120), updated.hitPoints());
        assertEquals(new Stat(55), updated.attack());
        assertEquals(new Stat(15), updated.defense());
        assertEquals(new Stat(25), updated.speed());
    }

    @Test
    void apply_composedModifiers_returnsExpectedResolvedStats() {
        // arrange
        CombatStats combatStats = CombatStats.of(
            new HitPoints(120, 120),
            new Stat(40),
            new Stat(10),
            new Stat(15)
        );

        // act
        CombatStats updated = combatStats
            .addFlat(StatType.ATTACK, 5)
            .multiplyBy(StatType.DEFENSE, 0.8)
            .apply(stats -> stats.multiplyBy(StatType.SPEED, 2))
            .clampMinimum(StatType.DEFENSE, 12);

        // assert
        assertEquals(new Stat(45), updated.attack());
        assertEquals(new Stat(12), updated.defense());
        assertEquals(new Stat(30), updated.speed());
    }
}
