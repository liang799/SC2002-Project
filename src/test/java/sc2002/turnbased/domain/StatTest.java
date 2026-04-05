package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StatTest {
    @Test
    void adjustBy_whenAmountIsPositive_returnsUpdatedStat() {
        Stat stat = new Stat(40);

        Stat updatedStat = stat.adjustBy(10);

        assertEquals(new Stat(50), updatedStat);
    }

    @Test
    void constructor_whenValueIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Stat(-1));
    }

    @Test
    void adjustBy_whenResultWouldBeNegative_throwsIllegalArgumentException() {
        Stat stat = new Stat(5);

        assertThrows(IllegalArgumentException.class, () -> stat.adjustBy(-6));
    }
}
