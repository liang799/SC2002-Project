package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class DefaultWaveManagerTest {
    @Test
    void spawnBackupIfNeeded_WhenInitialWaveDefeated_SpawnsReserveAndEmitsNarration() {
        EnemyCombatant deadInitialEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withCurrentHp(0)
            .withMaxHp(30)
            .build();
        EnemyCombatant reserveA = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Wolf A")
            .withHp(40)
            .build();
        EnemyCombatant reserveB = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Wolf B")
            .withHp(40)
            .build();

        List<Combatant> initialEnemies = List.of(deadInitialEnemy);
        List<Combatant> reserveEnemies = new ArrayList<>(List.of(reserveA, reserveB));
        List<Combatant> spawnedEnemies = new ArrayList<>(List.of(deadInitialEnemy));
        List<BattleEvent> emittedEvents = new ArrayList<>();

        WaveManager waveManager = new DefaultWaveManager(initialEnemies, reserveEnemies, spawnedEnemies);
        waveManager.spawnBackupIfNeeded(emittedEvents::add);

        assertEquals(0, reserveEnemies.size());
        assertEquals(3, spawnedEnemies.size());
        assertEquals(1, emittedEvents.size());
        NarrationEvent narrationEvent = (NarrationEvent) emittedEvents.get(0);
        assertEquals("Backup Spawn triggered: Wolf A, Wolf B", narrationEvent.getText());
    }

    @Test
    void spawnBackupIfNeeded_WhenInitialWaveStillAlive_DoesNothing() {
        EnemyCombatant aliveInitialEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withHp(30)
            .build();
        EnemyCombatant reserveEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Wolf")
            .withHp(40)
            .build();

        List<Combatant> initialEnemies = List.of(aliveInitialEnemy);
        List<Combatant> reserveEnemies = new ArrayList<>(List.of(reserveEnemy));
        List<Combatant> spawnedEnemies = new ArrayList<>(List.of(aliveInitialEnemy));
        List<BattleEvent> emittedEvents = new ArrayList<>();

        WaveManager waveManager = new DefaultWaveManager(initialEnemies, reserveEnemies, spawnedEnemies);
        waveManager.spawnBackupIfNeeded(emittedEvents::add);

        assertEquals(1, reserveEnemies.size());
        assertEquals(1, spawnedEnemies.size());
        assertTrue(emittedEvents.isEmpty());
    }
}