package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@Tag("unit")
class StatTest {
    @Test
    void addFlatAndMultiplyBy_WhenValuesAreValid_ReturnsUpdatedStat() {
        // arrange
        Stat stat = new Stat(25);

        // act
        Stat updated = stat
            .addFlat(5)
            .multiplyBy(2);

        // assert
        assertEquals(new Stat(60), updated);
    }

    @Test
    void clampMinimum_WhenValueIsBelowMinimum_ReturnsMinimumValue() {
        // arrange
        Stat stat = new Stat(5);

        // act
        Stat updated = stat.clampMinimum(12);

        // assert
        assertEquals(new Stat(12), updated);
    }

    @Test
    void stat_WhenNegativeChangesAreApplied_ThrowsIllegalArgumentException() {
        // arrange
        Executable createNegativeStat = () -> new Stat(-1);
        Stat stat = new Stat(5);
        Executable subtractTooMuch = () -> stat.addFlat(-6);
        Executable multiplyByNegative = () -> stat.multiplyBy(-1);
        Executable clampToNegative = () -> stat.clampMinimum(-1);

        // act + assert
        assertThrows(IllegalArgumentException.class, createNegativeStat);
        assertThrows(IllegalArgumentException.class, subtractTooMuch);
        assertThrows(IllegalArgumentException.class, multiplyByNegative);
        assertThrows(IllegalArgumentException.class, clampToNegative);
    }
}
