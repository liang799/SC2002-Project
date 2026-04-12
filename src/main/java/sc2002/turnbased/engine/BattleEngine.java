package sc2002.turnbased.engine;

import java.util.List;
import java.util.function.Consumer;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.RoundStartEvent;

public class BattleEngine implements ActionExecutionContext {
    private final BattleState battleState;
    private final TurnOrderStrategy turnOrderStrategy;
    private final TurnProcessor turnProcessor;
    private final WaveManager waveManager;
    private final RoundLifecycle roundLifecycle;
    private final BattleOutcomeReporter battleOutcomeReporter;

    public BattleEngine(BattleSetup battleSetup, TurnOrderStrategy turnOrderStrategy) {
        this.battleState = new BattleState(battleSetup);
        this.turnOrderStrategy = turnOrderStrategy;
        this.turnProcessor = new DefaultTurnProcessor(battleState.player());
        this.waveManager = new DefaultWaveManager(battleState);
        this.roundLifecycle = new DefaultRoundLifecycle(battleState.player(), battleState.spawnedEnemies());
        this.battleOutcomeReporter = new DefaultBattleOutcomeReporter(battleState.player());
    }

    public List<BattleEvent> runRounds(int roundCount, PlayerDecisionProvider playerDecisionProvider) {
        return runRounds(roundCount, playerDecisionProvider, BattleEventListener.NO_OP);
    }

    public List<BattleEvent> runRounds(
        int roundCount,
        PlayerDecisionProvider playerDecisionProvider,
        BattleEventListener battleEventListener
    ) {
        BattleEventPublisher battleEventPublisher = new BattleEventPublisher();
        Consumer<BattleEvent> emit = event -> battleEventPublisher.emit(event, battleEventListener);
        int roundsPlayed = 0;
        for (int roundNumber = 1; roundNumber <= roundCount; roundNumber++) {
            emit.accept(new RoundStartEvent(roundNumber));
            roundsPlayed = roundNumber;

            List<Combatant> turnOrder = turnOrderStrategy.determineOrder(battleState.combatantsAliveAtRoundStart());
            for (Combatant actor : turnOrder) {
                if (battleState.isBattleOver()) {
                    break;
                }
                turnProcessor.processTurn(
                    roundNumber,
                    actor,
                    playerDecisionProvider,
                    this,
                    emit
                );
            }

            waveManager.spawnBackupIfNeeded(emit);
            emit.accept(roundLifecycle.createRoundSummary(roundNumber));
            roundLifecycle.completeRound(emit);
            if (battleState.isBattleOver()) {
                break;
            }
        }

        battleOutcomeReporter.reportOutcome(
            roundsPlayed,
            battleState.livingEnemies(),
            battleState.reserveEnemies(),
            emit
        );
        return battleEventPublisher.snapshot();
    }

    public List<BattleEvent> runUntilBattleEnds(PlayerDecisionProvider playerDecisionProvider) {
        return runUntilBattleEnds(playerDecisionProvider, BattleEventListener.NO_OP);
    }

    public List<BattleEvent> runUntilBattleEnds(
        PlayerDecisionProvider playerDecisionProvider,
        BattleEventListener battleEventListener
    ) {
        return runRounds(1000, playerDecisionProvider, battleEventListener);
    }

    @Override
    public List<Combatant> getLivingEnemies() {
        return battleState.livingEnemies();
    }

    @Override
    public List<Combatant> getLivingEnemiesInTurnOrder() {
        return turnOrderStrategy.determineOrder(battleState.livingEnemies());
    }
}
