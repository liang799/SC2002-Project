package sc2002.turnbased.engine;

import java.util.List;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;

interface BattleOutcomeReporter {
    void reportOutcome(
        int roundsPlayed,
        List<Combatant> livingEnemies,
        List<Combatant> reserveEnemies,
        Consumer<BattleEvent> emit
    );
}