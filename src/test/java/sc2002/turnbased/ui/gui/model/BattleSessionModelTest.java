package sc2002.turnbased.ui.gui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.support.TestDependencies;

class BattleSessionModelTest {
    @Test
    void beginBattleRejectsConcurrentBattleUntilFinished() {
        BattleSessionModel model = new BattleSessionModel();

        assertTrue(model.beginBattle());
        assertFalse(model.beginBattle());

        model.finishBattle();

        assertTrue(model.beginBattle());
    }

    @Test
    void queuedTurnBecomesActiveWhenTaken() {
        BattleSessionModel model = new BattleSessionModel();
        PlayerTurnRequest turn = turnRequest();

        model.queuePlayerTurn(turn);

        assertEquals(turn, model.takeQueuedPlayerTurn().orElseThrow());
        assertEquals(turn, model.activePlayerTurn().orElseThrow());

        model.clearActivePlayerTurn();

        assertTrue(model.activePlayerTurn().isEmpty());
    }

    @Test
    void clearQueuedPlaybackStateDropsQueuedTurnAndPostGameConfig() {
        BattleSessionModel model = new BattleSessionModel();
        model.queuePlayerTurn(turnRequest());
        model.queuePostGame("config");

        model.clearQueuedPlaybackState();

        assertTrue(model.takeQueuedPlayerTurn().isEmpty());
        assertTrue(model.takeQueuedPostGameConfig().isEmpty());
    }

    private PlayerTurnRequest turnRequest() {
        PlayerCharacter player = TestDependencies.warrior();
        List<Combatant> enemies = List.of(TestDependencies.goblin("Goblin A"));
        return new PlayerTurnRequest(
            1,
            player,
            enemies,
            new ArrayBlockingQueue<PlayerDecision>(1)
        );
    }
}
