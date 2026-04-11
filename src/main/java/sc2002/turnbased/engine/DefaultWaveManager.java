package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

class DefaultWaveManager implements WaveManager {
    private final List<Combatant> initialEnemies;
    private final List<Combatant> reserveEnemies;
    private final List<Combatant> spawnedEnemies;

    DefaultWaveManager(List<Combatant> initialEnemies, List<Combatant> reserveEnemies, List<Combatant> spawnedEnemies) {
        this.initialEnemies = initialEnemies;
        this.reserveEnemies = reserveEnemies;
        this.spawnedEnemies = spawnedEnemies;
    }

    @Override
    public void spawnBackupIfNeeded(Consumer<BattleEvent> emit) {
        if (!reserveEnemies.isEmpty() && initialWaveDefeated()) {
            spawnedEnemies.addAll(reserveEnemies);
            reserveEnemies.clear();
            emit.accept(new NarrationEvent("Backup Spawn triggered: " + spawnedEnemiesAfterInitialWave()));
        }
    }

    private boolean initialWaveDefeated() {
        for (Combatant enemy : initialEnemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private String spawnedEnemiesAfterInitialWave() {
        List<String> names = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.isAlive() && !initialEnemies.contains(enemy)) {
                names.add(enemy.getName());
            }
        }
        return String.join(", ", names);
    }
}