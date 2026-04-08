package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatStatsTest {
    @Test
    void builder_namedStatsProvided_returnsFixedValueObjects() {
        // arrange
        CombatStats combatStats = CombatStats.builder()
            .attack(45)
            .defense(15)
            .speed(25)
            .build();

        // act
        Stat attack = combatStats.attack();
        Stat defense = combatStats.defense();
        Stat speed = combatStats.speed();

        // assert
        assertEquals(new Stat(45), attack);
        assertEquals(new Stat(15), defense);
        assertEquals(new Stat(25), speed);
    }

    @Test
    void withStat_requestedUpdate_returnsUpdatedCopy() {
        // arrange
        CombatStats combatStats = CombatStats.builder()
            .attack(45)
            .defense(15)
            .speed(25)
            .build();

        // act
        CombatStats updated = combatStats.withStat(StatType.ATTACK, new Stat(55));

        // assert
        assertEquals(new Stat(55), updated.attack());
        assertEquals(new Stat(15), updated.defense());
        assertEquals(new Stat(25), updated.speed());
    }

    @Test
    void statOperations_composedUpdates_returnsExpectedResolvedStats() {
        // arrange
        CombatStats combatStats = CombatStats.builder()
            .attack(40)
            .defense(10)
            .speed(15)
            .build();

        // act
        CombatStats updated = combatStats
            .addFlat(StatType.ATTACK, 5)
            .multiplyBy(StatType.DEFENSE, 0.8)
            .multiplyBy(StatType.SPEED, 2)
            .clampMinimum(StatType.DEFENSE, 12);

        // assert
        assertEquals(new Stat(45), updated.attack());
        assertEquals(new Stat(12), updated.defense());
        assertEquals(new Stat(30), updated.speed());
    }
}
