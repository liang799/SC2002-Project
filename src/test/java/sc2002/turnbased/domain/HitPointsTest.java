package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class HitPointsTest {
    @Test
    void takeDamage_whenDamageExceedsCurrentHp_capsCurrentHpAtZero() {
        HitPoints hitPoints = new HitPoints(70, 100);

        HitPoints updatedHitPoints = hitPoints.takeDamage(90);

        assertEquals(new HitPoints(0, 100), updatedHitPoints);
    }

    @Test
    void heal_whenHealingExceedsMaxHp_capsCurrentHpAtMax() {
        HitPoints hitPoints = new HitPoints(40, 100);

        HitPoints updatedHitPoints = hitPoints.heal(80);

        assertEquals(new HitPoints(100, 100), updatedHitPoints);
    }

    @Test
    void constructor_whenCurrentHpIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(-1, 100));
    }

    @Test
    void constructor_whenCurrentHpExceedsMaxHp_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(101, 100));
    }

    @Test
    void constructor_whenMaxHpIsNotPositive_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(0, 0));
    }

    @Test
    void isDead_whenCurrentHpIsZero_returnsTrue() {
        HitPoints hitPoints = new HitPoints(0, 100);

        boolean isDead = hitPoints.isDead();

        assertTrue(isDead);
    }

    @Test
    void getHealthPercentage_whenCurrentHpIsQuarterOfMax_returnsTwentyFivePercent() {
        HitPoints hitPoints = new HitPoints(25, 100);

        double healthPercentage = hitPoints.getHealthPercentage();

        assertEquals(25.0, healthPercentage);
    }
}
