package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;

public class BattleSetup {
    private final PlayerCharacter player;
    private final List<Combatant> initialEnemies;
    private final List<Combatant> backupEnemies;

    public BattleSetup(PlayerCharacter player, List<Combatant> initialEnemies, List<Combatant> backupEnemies) {
        this.player = Objects.requireNonNull(player, "player");
        this.initialEnemies = new ArrayList<>(Objects.requireNonNull(initialEnemies, "initialEnemies"));
        this.backupEnemies = new ArrayList<>(Objects.requireNonNull(backupEnemies, "backupEnemies"));
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
}
