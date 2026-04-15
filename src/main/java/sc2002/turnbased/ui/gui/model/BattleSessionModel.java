package sc2002.turnbased.ui.gui.model;

import java.util.Optional;

public final class BattleSessionModel {
    private boolean battleRunning;
    private PlayerTurnRequest activePlayerTurn;
    private PlayerTurnRequest queuedPlayerTurn;
    private Object queuedPostGameConfig;

    public synchronized boolean beginBattle() {
        if (battleRunning) {
            return false;
        }
        battleRunning = true;
        activePlayerTurn = null;
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
        return true;
    }

    public synchronized void finishBattle() {
        battleRunning = false;
        activePlayerTurn = null;
    }

    public synchronized void stopBattle() {
        battleRunning = false;
        activePlayerTurn = null;
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
    }

    public synchronized void clearQueuedPlaybackState() {
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
    }

    public synchronized Optional<PlayerTurnRequest> activePlayerTurn() {
        return Optional.ofNullable(activePlayerTurn);
    }

    public synchronized void clearActivePlayerTurn() {
        activePlayerTurn = null;
    }

    public synchronized void queuePlayerTurn(PlayerTurnRequest turn) {
        queuedPlayerTurn = turn;
    }

    public synchronized Optional<PlayerTurnRequest> takeQueuedPlayerTurn() {
        PlayerTurnRequest turn = queuedPlayerTurn;
        queuedPlayerTurn = null;
        activePlayerTurn = turn;
        return Optional.ofNullable(turn);
    }

    public synchronized void queuePostGame(Object configuration) {
        queuedPostGameConfig = configuration;
    }

    public synchronized Optional<Object> takeQueuedPostGameConfig() {
        Object configuration = queuedPostGameConfig;
        queuedPostGameConfig = null;
        return Optional.ofNullable(configuration);
    }
}
