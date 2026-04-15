package sc2002.turnbased.ui.gui.model;

import java.util.Optional;

public final class BattleSessionModel {
    private boolean battleRunning;
    private PlayerTurnRequest activePlayerTurn;
    private PlayerTurnRequest queuedPlayerTurn;
    private Object queuedPostGameConfig;

    public boolean beginBattle() {
        if (battleRunning) {
            return false;
        }
        battleRunning = true;
        activePlayerTurn = null;
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
        return true;
    }

    public void finishBattle() {
        battleRunning = false;
        activePlayerTurn = null;
    }

    public void stopBattle() {
        battleRunning = false;
        activePlayerTurn = null;
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
    }

    public void clearQueuedPlaybackState() {
        queuedPlayerTurn = null;
        queuedPostGameConfig = null;
    }

    public Optional<PlayerTurnRequest> activePlayerTurn() {
        return Optional.ofNullable(activePlayerTurn);
    }

    public void clearActivePlayerTurn() {
        activePlayerTurn = null;
    }

    public void queuePlayerTurn(PlayerTurnRequest turn) {
        queuedPlayerTurn = turn;
    }

    public Optional<PlayerTurnRequest> takeQueuedPlayerTurn() {
        PlayerTurnRequest turn = queuedPlayerTurn;
        queuedPlayerTurn = null;
        activePlayerTurn = turn;
        return Optional.ofNullable(turn);
    }

    public void queuePostGame(Object configuration) {
        queuedPostGameConfig = configuration;
    }

    public Optional<Object> takeQueuedPostGameConfig() {
        Object configuration = queuedPostGameConfig;
        queuedPostGameConfig = null;
        return Optional.ofNullable(configuration);
    }
}
