package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HitPointsTest {
    @Test
    void takeDamageClampsAtZero() {
        HitPoints hitPoints = new HitPoints(70, 100);

        assertEquals(new HitPoints(0, 100), hitPoints.takeDamage(90));
    }

    @Test
    void healCapsAtMaximum() {
        HitPoints hitPoints = new HitPoints(40, 100);

        assertEquals(new HitPoints(100, 100), hitPoints.heal(80));
    }

    @Test
    void constructorRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(-1, 100));
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(101, 100));
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(0, 0));
    }

    @Test
    void isDeadReturnsTrueAtZeroHp() {
        assertTrue(new HitPoints(0, 100).isDead());
    }

    @Test
    void healthPercentageReflectsCurrentHealth() {
        assertEquals(25.0, new HitPoints(25, 100).getHealthPercentage());
    }
}
