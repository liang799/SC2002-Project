package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.CombatantStatusOutcome;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;

public class BattleEngine implements ActionExecutionContext {
    private final PlayerCharacter player;
    private final List<Combatant> initialEnemies;
    private final List<Combatant> reserveEnemies;
    private final List<Combatant> spawnedEnemies;
    private final Inventory inventory;
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<BattleEvent> events = new ArrayList<>();

    public BattleEngine(BattleSetup battleSetup, TurnOrderStrategy turnOrderStrategy) {
        this.player = battleSetup.getPlayer();
        this.initialEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
        this.reserveEnemies = new ArrayList<>(battleSetup.getBackupEnemies());
        this.spawnedEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
        this.inventory = battleSetup.getInventory();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    public List<BattleEvent> runRounds(int roundCount, PlayerDecisionProvider playerDecisionProvider) {
        return runRounds(roundCount, playerDecisionProvider, BattleEventListener.NO_OP);
    }

    public List<BattleEvent> runRounds(
        int roundCount,
        PlayerDecisionProvider playerDecisionProvider,
        BattleEventListener battleEventListener
    ) {
        int roundsPlayed = 0;
        for (int roundNumber = 1; roundNumber <= roundCount; roundNumber++) {
            emit(new RoundStartEvent(roundNumber), battleEventListener);
            roundsPlayed = roundNumber;

            List<Combatant> turnOrder = turnOrderStrategy.determineOrder(combatantsAliveAtRoundStart());
            for (Combatant actor : turnOrder) {
                if (isBattleOver()) {
                    break;
                }
                processTurn(roundNumber, actor, playerDecisionProvider, battleEventListener);
            }

            spawnBackupIfNeeded(battleEventListener);
            emit(createRoundSummary(roundNumber), battleEventListener);
            completeRound();
            if (isBattleOver()) {
                break;
            }
        }

        if (player.isAlive() && livingEnemies().isEmpty() && reserveEnemies.isEmpty()) {
            addVictoryNarration(roundsPlayed, battleEventListener);
        } else if (!player.isAlive()) {
            addDefeatNarration(roundsPlayed, battleEventListener);
        }
        return List.copyOf(events);
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

    private void processTurn(
        int roundNumber,
        Combatant actor,
        PlayerDecisionProvider playerDecisionProvider,
        BattleEventListener battleEventListener
    ) {
        PlayerDecision decision = null;
        if (actor == player && actor.isAlive()) {
            decision = playerDecisionProvider.decide(roundNumber, player, livingEnemies());
        }
        if (shouldAdvanceCooldown(actor, decision)) {
            player.advanceRoundState();
        }

        java.util.Optional<String> turnBlockReason = actor.getTurnBlockReason();
        List<CombatantStatusOutcome> statusEffectOutcomes = actor.consumeStatusEffectOutcomes();
        if (!actor.isAlive()) {
            emit(
                SkippedTurnEvent.fromStatusEffectOutcomes(actor.getName(), "ELIMINATED", statusEffectOutcomes),
                battleEventListener
            );
            return;
        }

        if (turnBlockReason.isPresent()) {
            emit(
                SkippedTurnEvent.fromStatusEffectOutcomes(
                    actor.getName(),
                    turnBlockReason.get(),
                    statusEffectOutcomes
                ),
                battleEventListener
            );
            return;
        }

        if (actor == player) {
            processPlayerTurn(decision, battleEventListener);
            return;
        }

        if (actor instanceof EnemyCombatant enemy) {
            processEnemyTurn(enemy, battleEventListener);
        }
    }

    private boolean shouldAdvanceCooldown(Combatant actor, PlayerDecision decision) {
        return actor == player && decision != null && decision.action().advancesCooldown();
    }

    private void processPlayerTurn(PlayerDecision decision, BattleEventListener battleEventListener) {
        Combatant target = decision.targetReference().resolveFrom(livingEnemies());
        emitAll(decision.action().execute(this, player, target), battleEventListener);
    }

    private void processEnemyTurn(EnemyCombatant enemy, BattleEventListener battleEventListener) {
        if (!player.isAlive()) {
            return;
        }
        emitAll(enemy.takeTurn(this, player), battleEventListener);
    }

    private List<Combatant> combatantsAliveAtRoundStart() {
        List<Combatant> combatants = new ArrayList<>();
        if (player.isAlive()) {
            combatants.add(player);
        }
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.isAlive()) {
                combatants.add(enemy);
            }
        }
        return combatants;
    }

    @Override
    public List<Combatant> getLivingEnemies() {
        return livingEnemies();
    }

    @Override
    public List<Combatant> getLivingEnemiesInTurnOrder() {
        return turnOrderStrategy.determineOrder(livingEnemies());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private List<Combatant> livingEnemies() {
        List<Combatant> livingEnemies = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.isAlive()) {
                livingEnemies.add(enemy);
            }
        }
        return livingEnemies;
    }

    private boolean isBattleOver() {
        return !player.isAlive() || (livingEnemies().isEmpty() && reserveEnemies.isEmpty());
    }

    private void spawnBackupIfNeeded(BattleEventListener battleEventListener) {
        if (!reserveEnemies.isEmpty() && initialWaveDefeated()) {
            spawnedEnemies.addAll(reserveEnemies);
            reserveEnemies.clear();
            emit(new NarrationEvent("Backup Spawn triggered: " + spawnedEnemiesAfterInitialWave()), battleEventListener);
        }
    }

    private boolean initialWaveDefeated() {
        for (Combatant enemy : initialEnemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private String spawnedEnemiesAfterInitialWave() {
        List<String> names = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.isAlive() && !initialEnemies.contains(enemy)) {
                names.add(enemy.getName());
            }
        }
        return String.join(", ", names);
    }

    private RoundSummaryEvent createRoundSummary(int roundNumber) {
        List<CombatantSummary> enemySummaries = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            enemySummaries.add(toSummary(enemy));
        }
        return new RoundSummaryEvent(
            roundNumber,
            toSummary(player),
            enemySummaries,
            inventory.snapshot(),
            player.getSpecialSkillCooldown()
        );
    }

    private void completeRound() {
        player.completeRound();
        for (Combatant enemy : spawnedEnemies) {
            enemy.completeRound();
        }
    }

    private CombatantSummary toSummary(Combatant combatant) {
        return new CombatantSummary(
            combatant.getName(),
            combatant.getCurrentHp(),
            combatant.getMaxHp(),
            combatant.getAttack(),
            combatant.getBaseAttack(),
            combatant.isAlive(),
            combatant.getActiveStatuses()
        );
    }

    private void addVictoryNarration(int roundsPlayed, BattleEventListener battleEventListener) {
        emit(new NarrationEvent("Victory:"), battleEventListener);
        emit(new NarrationEvent("Remaining HP: " + player.getCurrentHp() + " / " + player.getMaxHp()), battleEventListener);
        emit(new NarrationEvent("Total Rounds: " + roundsPlayed), battleEventListener);
        for (ItemType itemType : inventory.snapshot().keySet()) {
            emit(new NarrationEvent("Remaining " + itemType.getDisplayName() + ": " + inventory.countOf(itemType)), battleEventListener);
        }
        if (player.getAttack() != player.getBaseAttack()) {
            emit(new NarrationEvent("Final " + player.getName() + " ATK: " + player.getAttack()), battleEventListener);
        }
    }

    private void addDefeatNarration(int roundsPlayed, BattleEventListener battleEventListener) {
        emit(new NarrationEvent("Defeat:"), battleEventListener);
        emit(new NarrationEvent("Enemies remaining: " + livingEnemies().size()), battleEventListener);
        emit(new NarrationEvent("Total Rounds Survived: " + roundsPlayed), battleEventListener);
    }

    private void emit(BattleEvent event, BattleEventListener battleEventListener) {
        events.add(event);
        battleEventListener.onEvent(event);
    }

    private void emitAll(List<BattleEvent> emittedEvents, BattleEventListener battleEventListener) {
        for (BattleEvent emittedEvent : emittedEvents) {
            emit(emittedEvent, battleEventListener);
        }
    }
}
