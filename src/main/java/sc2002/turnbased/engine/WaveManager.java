package sc2002.turnbased.engine;

import java.util.function.Consumer;

import sc2002.turnbased.report.BattleEvent;

interface WaveManager {
    void spawnBackupIfNeeded(Consumer<BattleEvent> emit);
}