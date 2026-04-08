package sc2002.turnbased.domain;

import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public interface CombatantFactory {
    PlayerCharacter createPlayer(PlayerType playerType);

    EnemyCombatant createEnemy(EnemyType enemyType, String name);
}
