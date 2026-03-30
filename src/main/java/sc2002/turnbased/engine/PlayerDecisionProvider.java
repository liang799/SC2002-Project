package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;

public interface PlayerDecisionProvider {
    PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies);
}
