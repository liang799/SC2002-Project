package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StatTest {
    @Test
    void adjustByReturnsUpdatedStat() {
        assertEquals(new Stat(50), new Stat(40).adjustBy(10));
    }

    @Test
    void statCannotBecomeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Stat(-1));
        assertThrows(IllegalArgumentException.class, () -> new Stat(5).adjustBy(-6));
    }
}
