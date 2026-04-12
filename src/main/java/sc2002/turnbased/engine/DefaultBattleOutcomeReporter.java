package sc2002.turnbased.engine;

import java.util.List;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

class DefaultBattleOutcomeReporter implements BattleOutcomeReporter {
    private final PlayerCharacter player;

    DefaultBattleOutcomeReporter(PlayerCharacter player) {
        this.player = player;
    }

    @Override
    public void reportOutcome(
        int roundsPlayed,
        List<Combatant> livingEnemies,
        List<Combatant> reserveEnemies,
        Consumer<BattleEvent> emit
    ) {
        if (player.isAlive() && livingEnemies.isEmpty() && reserveEnemies.isEmpty()) {
            addVictoryNarration(roundsPlayed, emit);
            return;
        }
        if (!player.isAlive()) {
            int totalRemainingEnemies = livingEnemies.size() + reserveEnemies.size();
            addDefeatNarration(roundsPlayed, totalRemainingEnemies, emit);
        }
    }

    private void addVictoryNarration(int roundsPlayed, Consumer<BattleEvent> emit) {
        emit.accept(new NarrationEvent("Victory:"));
        emit.accept(new NarrationEvent("Remaining HP: " + player.getCurrentHp() + " / " + player.getMaxHp()));
        emit.accept(new NarrationEvent("Total Rounds: " + roundsPlayed));
        var inventorySnapshot = player.getInventory().snapshot();
        for (ItemType itemType : inventorySnapshot.keySet()) {
            emit.accept(
                new NarrationEvent(
                    "Remaining " + itemType.getDisplayName() + ": " + inventorySnapshot.get(itemType)
                )
            );
        }
        if (player.getAttack() != player.getBaseAttack()) {
            emit.accept(new NarrationEvent("Final " + player.getName() + " ATK: " + player.getAttack()));
        }
    }

    private void addDefeatNarration(int roundsPlayed, int enemiesRemaining, Consumer<BattleEvent> emit) {
        emit.accept(new NarrationEvent("Defeat:"));
        emit.accept(new NarrationEvent("Enemies remaining: " + enemiesRemaining));
        emit.accept(new NarrationEvent("Total Rounds Survived: " + roundsPlayed));
    }
}