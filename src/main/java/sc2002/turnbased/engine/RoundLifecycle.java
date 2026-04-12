package sc2002.turnbased.engine;

import java.util.function.Consumer;

import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.RoundSummaryEvent;

interface RoundLifecycle {
    RoundSummaryEvent createRoundSummary(int roundNumber);

    void completeRound(Consumer<BattleEvent> emit);
}