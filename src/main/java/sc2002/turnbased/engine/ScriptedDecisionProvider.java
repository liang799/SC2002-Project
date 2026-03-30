package sc2002.turnbased.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;

public class ScriptedDecisionProvider implements PlayerDecisionProvider {
    private final Map<Integer, PlayerDecision> decisionsByRound = new HashMap<>();

    public ScriptedDecisionProvider addDecision(int roundNumber, PlayerDecision playerDecision) {
        decisionsByRound.put(roundNumber, playerDecision);
        return this;
    }

    @Override
    public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        PlayerDecision decision = decisionsByRound.get(roundNumber);
        if (decision == null) {
            throw new IllegalStateException("No scripted decision configured for round " + roundNumber);
        }
        return decision;
    }
}
