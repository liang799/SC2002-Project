package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CombatStatsTest {
    @Test
    void combatStatsUsesValueObjectsDirectly() {
        CombatStats combatStats = new CombatStats(
            new HitPoints(120, 120),
            Map.of(
                StatType.ATTACK, new Stat(45),
                StatType.DEFENSE, new Stat(15),
                StatType.SPEED, new Stat(25)
            )
        );

        assertEquals(new HitPoints(120, 120), combatStats.hitPoints());
        assertEquals(new Stat(45), combatStats.stats().get(StatType.ATTACK));
        assertEquals(new Stat(15), combatStats.stats().get(StatType.DEFENSE));
        assertEquals(new Stat(25), combatStats.stats().get(StatType.SPEED));
    }

    @Test
    void withersReplaceOnlyRequestedField() {
        CombatStats combatStats = new CombatStats(
            new HitPoints(120, 120),
            Map.of(
                StatType.ATTACK, new Stat(45),
                StatType.DEFENSE, new Stat(15),
                StatType.SPEED, new Stat(25)
            )
        );
        CombatStats updated = combatStats.withHitPoints(new HitPoints(70, 120))
            .withStat(StatType.ATTACK, new Stat(55));

        assertEquals(new HitPoints(70, 120), updated.hitPoints());
        assertEquals(new Stat(55), updated.stats().get(StatType.ATTACK));
        assertEquals(new Stat(15), updated.stats().get(StatType.DEFENSE));
        assertEquals(new Stat(25), updated.stats().get(StatType.SPEED));
    }
}
