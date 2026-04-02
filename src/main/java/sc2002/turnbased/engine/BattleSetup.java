package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.PlayerCharacter;

public class BattleSetup {
    private final PlayerCharacter player;
    private final List<Combatant> initialEnemies;
    private final List<Combatant> backupEnemies;
    private final Inventory inventory;

    public BattleSetup(PlayerCharacter player, List<Combatant> initialEnemies, List<Combatant> backupEnemies, Inventory inventory) {
        this.player = Objects.requireNonNull(player, "player");
        this.initialEnemies = new ArrayList<>(Objects.requireNonNull(initialEnemies, "initialEnemies"));
        this.backupEnemies = new ArrayList<>(Objects.requireNonNull(backupEnemies, "backupEnemies"));
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public List<Combatant> getInitialEnemies() {
        return Collections.unmodifiableList(initialEnemies);
    }

    public List<Combatant> getBackupEnemies() {
        return Collections.unmodifiableList(backupEnemies);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
