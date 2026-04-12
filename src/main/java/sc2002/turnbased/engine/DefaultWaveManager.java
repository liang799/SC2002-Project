package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

class DefaultWaveManager implements WaveManager {
    private final List<Combatant> initialEnemies;
    private final List<Combatant> reserveEnemies;
    private final Runnable moveReserveToSpawned;

    DefaultWaveManager(BattleState battleState) {
        this(
            battleState.initialEnemies(),
            battleState.reserveEnemies(),
            battleState::moveAllReserveToSpawned
        );
    }

    DefaultWaveManager(List<Combatant> initialEnemies, List<Combatant> reserveEnemies, List<Combatant> spawnedEnemies) {
        this(
            initialEnemies,
            reserveEnemies,
            () -> {
                spawnedEnemies.addAll(reserveEnemies);
                reserveEnemies.clear();
            }
        );
    }

    private DefaultWaveManager(
        List<Combatant> initialEnemies,
        List<Combatant> reserveEnemies,
        Runnable moveReserveToSpawned
    ) {
        this.initialEnemies = Objects.requireNonNull(initialEnemies, "initialEnemies");
        this.reserveEnemies = Objects.requireNonNull(reserveEnemies, "reserveEnemies");
        this.moveReserveToSpawned = Objects.requireNonNull(moveReserveToSpawned, "moveReserveToSpawned");
    }

    @Override
    public void spawnBackupIfNeeded(Consumer<BattleEvent> emit) {
        if (!reserveEnemies.isEmpty() && initialWaveDefeated()) {
            String backupNames = reserveEnemyNamesToSpawn();
            moveReserveToSpawned.run();
            emit.accept(new NarrationEvent("Backup Spawn triggered: " + backupNames));
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

    private String reserveEnemyNamesToSpawn() {
        List<String> names = new ArrayList<>();
        for (Combatant enemy : reserveEnemies) {
            if (enemy.isAlive() && !initialEnemies.contains(enemy)) {
                names.add(enemy.getName());
            }
        }
        return String.join(", ", names);
    }
}