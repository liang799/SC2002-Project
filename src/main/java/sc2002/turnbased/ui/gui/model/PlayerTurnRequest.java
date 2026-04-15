package sc2002.turnbased.ui.gui.model;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;

public record PlayerTurnRequest(
    int roundNumber,
    PlayerCharacter player,
    List<Combatant> livingEnemies,
    BlockingQueue<PlayerDecision> responseQueue
) {
}
