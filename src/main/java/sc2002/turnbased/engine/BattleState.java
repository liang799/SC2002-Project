package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;

class BattleState {
    private final PlayerCharacter player;
    private final List<Combatant> initialEnemies;
    private final List<Combatant> reserveEnemies;
    private final List<Combatant> spawnedEnemies;

    BattleState(BattleSetup battleSetup) {
        this.player = battleSetup.getPlayer();
        this.initialEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
        this.reserveEnemies = new ArrayList<>(battleSetup.getBackupEnemies());
        this.spawnedEnemies = new ArrayList<>(battleSetup.getInitialEnemies());
    }

    PlayerCharacter player() {
        return player;
    }

    List<Combatant> initialEnemies() {
        return initialEnemies;
    }

    List<Combatant> reserveEnemies() {
        return reserveEnemies;
    }

    List<Combatant> spawnedEnemies() {
        return spawnedEnemies;
    }

    List<Combatant> combatantsAliveAtRoundStart() {
        List<Combatant> combatants = new ArrayList<>();
        if (player.isAlive()) {
            combatants.add(player);
        }
        combatants.addAll(livingEnemies());
        return combatants;
    }

    List<Combatant> livingEnemies() {
        List<Combatant> livingEnemies = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            if (enemy.isAlive()) {
                livingEnemies.add(enemy);
            }
        }
        return livingEnemies;
    }

    boolean isBattleOver() {
        return !player.isAlive() || (livingEnemies().isEmpty() && reserveEnemies.isEmpty());
    }
}