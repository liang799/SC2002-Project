package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class HitPointsTest {
    @Test
    void takeDamage_WhenDamageExceedsCurrentHp_CapsCurrentHpAtZero() {
        HitPoints hitPoints = new HitPoints(70, 100);

        HitPoints updatedHitPoints = hitPoints.takeDamage(90);

        assertEquals(new HitPoints(0, 100), updatedHitPoints);
    }

    @Test
    void heal_WhenHealingExceedsMaxHp_CapsCurrentHpAtMax() {
        HitPoints hitPoints = new HitPoints(40, 100);

        HitPoints updatedHitPoints = hitPoints.heal(80);

        assertEquals(new HitPoints(100, 100), updatedHitPoints);
    }

    @Test
    void constructor_WhenCurrentHpIsNegative_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(-1, 100));
    }

    @Test
    void constructor_WhenCurrentHpExceedsMaxHp_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(101, 100));
    }

    @Test
    void constructor_WhenMaxHpIsNotPositive_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new HitPoints(0, 0));
    }

    @Test
    void isDead_WhenCurrentHpIsZero_ReturnsTrue() {
        HitPoints hitPoints = new HitPoints(0, 100);

        boolean isDead = hitPoints.isDead();

        assertTrue(isDead);
    }

    @Test
    void getHealthPercentage_WhenCurrentHpIsQuarterOfMax_ReturnsTwentyFivePercent() {
        HitPoints hitPoints = new HitPoints(25, 100);

        double healthPercentage = hitPoints.getHealthPercentage();

        assertEquals(25.0, healthPercentage);
    }
}
