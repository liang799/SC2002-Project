package sc2002.turnbased.engine;

import java.util.Optional;
import java.util.function.Consumer;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.SkippedTurnEvent;

class DefaultTurnProcessor implements TurnProcessor {
    private final PlayerCharacter player;

    DefaultTurnProcessor(PlayerCharacter player) {
        this.player = player;
    }

    @Override
    public void processTurn(
        int roundNumber,
        Combatant actor,
        PlayerDecisionProvider playerDecisionProvider,
        ActionExecutionContext actionExecutionContext,
        Consumer<BattleEvent> emit
    ) {
        PlayerDecision decision = null;
        if (actor == player && actor.isAlive()) {
            decision = playerDecisionProvider.decide(roundNumber, player, actionExecutionContext.getLivingEnemies());
        }
        if (shouldAdvanceCooldown(actor, decision)) {
            player.advanceRoundState();
        }

        Optional<String> turnBlockReason = actor.getTurnBlockReason();
        if (!actor.isAlive()) {
            emit.accept(
                SkippedTurnEvent.fromStatusEffectOutcomes(
                    actor,
                    "ELIMINATED",
                    actor.consumeStatusEffectOutcomes()
                )
            );
            return;
        }

        if (turnBlockReason.isPresent()) {
            emit.accept(
                SkippedTurnEvent.fromStatusEffectOutcomes(
                    actor,
                    turnBlockReason.get(),
                    actor.consumeStatusEffectOutcomes()
                )
            );
            return;
        }

        if (actor == player) {
            processPlayerTurn(decision, actionExecutionContext, emit);
            return;
        }

        if (actor instanceof EnemyCombatant enemy) {
            processEnemyTurn(enemy, actionExecutionContext, emit);
        }
    }

    private boolean shouldAdvanceCooldown(Combatant actor, PlayerDecision decision) {
        return actor == player && decision != null && decision.action().advancesCooldown();
    }

    private void processPlayerTurn(
        PlayerDecision decision,
        ActionExecutionContext actionExecutionContext,
        Consumer<BattleEvent> emit
    ) {
        Combatant target = decision.targetReference().resolveFrom(actionExecutionContext.getLivingEnemies());
        for (BattleEvent battleEvent : decision.action().execute(actionExecutionContext, player, target)) {
            emit.accept(battleEvent);
        }
    }

    private void processEnemyTurn(
        EnemyCombatant enemy,
        ActionExecutionContext actionExecutionContext,
        Consumer<BattleEvent> emit
    ) {
        if (!player.isAlive()) {
            return;
        }
        for (BattleEvent battleEvent : enemy.takeTurn(actionExecutionContext, player)) {
            emit.accept(battleEvent);
        }
    }
}