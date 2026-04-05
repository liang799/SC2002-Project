package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CombatStatsTest {
    @Test
    void combatStatsUsesValueObjectsDirectly() {
        CombatStats combatStats = new CombatStats(
            new HitPoints(120, 120),
            new Stat(45),
            new Stat(15),
            new Stat(25)
        );

        assertEquals(new HitPoints(120, 120), combatStats.hitPoints());
        assertEquals(new Stat(45), combatStats.attack());
        assertEquals(new Stat(15), combatStats.defense());
        assertEquals(new Stat(25), combatStats.speed());
    }

    @Test
    void withersReplaceOnlyRequestedField() {
        CombatStats combatStats = new CombatStats(
            new HitPoints(120, 120),
            new Stat(45),
            new Stat(15),
            new Stat(25)
        );
        CombatStats updated = combatStats.withHitPoints(new HitPoints(70, 120))
            .withAttack(new Stat(55));

        assertEquals(new HitPoints(70, 120), updated.hitPoints());
        assertEquals(new Stat(55), updated.attack());
        assertEquals(new Stat(15), updated.defense());
        assertEquals(new Stat(25), updated.speed());
    }
}
