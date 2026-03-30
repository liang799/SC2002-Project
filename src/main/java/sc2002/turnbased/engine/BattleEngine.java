package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.TurnWindow;
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
    private final BattleAction enemyBasicAttack;
    private final List<BattleEvent> events = new ArrayList<>();
    private int smokeBombEnemyAttackCharges;

    public BattleEngine(BattleSetup battleSetup, TurnOrderStrategy turnOrderStrategy) {
        this.player = battleSetup.getPlayer();
        this.initialEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
        this.reserveEnemies = new ArrayList<>(battleSetup.getBackupEnemies());
        this.spawnedEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
        this.inventory = battleSetup.getInventory();
        this.turnOrderStrategy = turnOrderStrategy;
        this.enemyBasicAttack = new BasicAttackAction();
        this.smokeBombEnemyAttackCharges = 0;
    }

    public List<BattleEvent> runRounds(int roundCount, PlayerDecisionProvider playerDecisionProvider) {
        int roundsPlayed = 0;
        for (int roundNumber = 1; roundNumber <= roundCount; roundNumber++) {
            events.add(new RoundStartEvent(roundNumber));
            roundsPlayed = roundNumber;

            List<Combatant> turnOrder = turnOrderStrategy.determineOrder(combatantsAliveAtRoundStart());
            for (Combatant actor : turnOrder) {
                if (isBattleOver()) {
                    break;
                }
                processTurn(roundNumber, actor, playerDecisionProvider);
            }

            spawnBackupIfNeeded();
            events.add(createRoundSummary(roundNumber));
            if (isBattleOver()) {
                break;
            }
        }

        if (player.isAlive() && livingEnemies().isEmpty() && reserveEnemies.isEmpty()) {
            addVictoryNarration(roundsPlayed);
        }
        return List.copyOf(events);
    }

    private void processTurn(int roundNumber, Combatant actor, PlayerDecisionProvider playerDecisionProvider) {
        PlayerDecision decision = null;
        if (actor == player && actor.isAlive()) {
            decision = playerDecisionProvider.decide(roundNumber, player, livingEnemies());
        }
        if (actor.isAlive() && shouldAdvanceCooldown(actor, decision)) {
            actor.beginTurn();
        }

        if (!actor.isAlive()) {
            TurnWindow turnWindow = actor.openTurnWindow();
            events.add(new SkippedTurnEvent(actor.getName(), "ELIMINATED", turnWindow.getNotes()));
            return;
        }

        TurnWindow turnWindow = actor.openTurnWindow();
        if (turnWindow.isBlocked()) {
            events.add(new SkippedTurnEvent(actor.getName(), turnWindow.getBlockerLabel(), turnWindow.getNotes()));
            return;
        }

        if (actor == player) {
            Combatant target = findEnemy(decision.getTargetName());
            events.addAll(decision.getAction().execute(this, player, target));
            return;
        }

        if (player.isAlive()) {
            events.addAll(enemyBasicAttack.execute(this, actor, player));
        }
    }

    private boolean shouldAdvanceCooldown(Combatant actor, PlayerDecision decision) {
        if (actor != player || decision == null) {
            return true;
        }
        return decision.getAction().advancesCooldown();
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

    @Override
    public void activateSmokeBomb() {
        smokeBombEnemyAttackCharges = 2;
    }

    @Override
    public int adjustDamage(Combatant actor, Combatant target, int baseDamage, List<String> notes) {
        if (actor != player && target == player && smokeBombEnemyAttackCharges > 0) {
            smokeBombEnemyAttackCharges--;
            notes.add("Smoke Bomb active");
            if (smokeBombEnemyAttackCharges == 0) {
                notes.add("Smoke Bomb effect expires");
            }
            return 0;
        }
        return baseDamage;
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

    private Combatant findEnemy(String targetName) {
        if (targetName == null) {
            return null;
        }
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.getName().equals(targetName)) {
                return enemy;
            }
        }
        throw new IllegalArgumentException("Unknown enemy target: " + targetName);
    }

    private boolean isBattleOver() {
        return !player.isAlive() || (livingEnemies().isEmpty() && reserveEnemies.isEmpty());
    }

    private void spawnBackupIfNeeded() {
        if (!reserveEnemies.isEmpty() && initialWaveDefeated()) {
            spawnedEnemies.addAll(reserveEnemies);
            reserveEnemies.clear();
            events.add(new NarrationEvent("Backup Spawn triggered: " + spawnedEnemiesAfterInitialWave()));
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

    private CombatantSummary toSummary(Combatant combatant) {
        return new CombatantSummary(
            combatant.getName(),
            combatant.getCurrentHp(),
            combatant.getMaxHp(),
            combatant.getAttack(),
            combatant.getBaseAttack(),
            combatant.isAlive(),
            combatant.getActiveStatusNames()
        );
    }

    private void addVictoryNarration(int roundsPlayed) {
        events.add(new NarrationEvent("Victory:"));
        events.add(new NarrationEvent("Remaining HP: " + player.getCurrentHp() + " / " + player.getMaxHp()));
        events.add(new NarrationEvent("Total Rounds: " + roundsPlayed));
        for (ItemType itemType : inventory.snapshot().keySet()) {
            events.add(new NarrationEvent("Remaining " + itemType.getDisplayName() + ": " + inventory.countOf(itemType)));
        }
        if (player.getAttack() != player.getBaseAttack()) {
            events.add(new NarrationEvent("Final " + player.getName() + " ATK: " + player.getAttack()));
        }
    }
}
