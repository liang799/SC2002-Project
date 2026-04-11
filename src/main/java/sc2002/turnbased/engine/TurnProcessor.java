package sc2002.turnbased.engine;

import java.util.function.Consumer;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;

interface TurnProcessor {
    void processTurn(
        int roundNumber,
        Combatant actor,
        PlayerDecisionProvider playerDecisionProvider,
        ActionExecutionContext actionExecutionContext,
        Consumer<BattleEvent> emit
    );
}