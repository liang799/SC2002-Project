package sc2002.turnbased.ui.gui.model;

import java.util.List;
import java.util.Objects;
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
    public PlayerTurnRequest {
        player = Objects.requireNonNull(player, "player");
        livingEnemies = List.copyOf(Objects.requireNonNull(livingEnemies, "livingEnemies"));
        responseQueue = Objects.requireNonNull(responseQueue, "responseQueue");
    }
}
