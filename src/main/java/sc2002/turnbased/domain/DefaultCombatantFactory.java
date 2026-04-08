package sc2002.turnbased.domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public class DefaultCombatantFactory implements CombatantFactory {
    private final StatusEffectRegistryFactory statusEffectRegistryFactory;
    private final EnumMap<PlayerType, PlayerCharacterCreator> playerCreators;
    private final EnumMap<EnemyType, EnemyCombatantCreator> enemyCreators;

    public DefaultCombatantFactory(
        StatusEffectRegistryFactory statusEffectRegistryFactory,
        Map<PlayerType, PlayerCharacterCreator> playerCreators,
        Map<EnemyType, EnemyCombatantCreator> enemyCreators
    ) {
        this.statusEffectRegistryFactory = Objects.requireNonNull(statusEffectRegistryFactory, "statusEffectRegistryFactory");
        this.playerCreators = copyPlayerCreators(playerCreators);
        this.enemyCreators = copyEnemyCreators(enemyCreators);
    }

    @Override
    public PlayerCharacter createPlayer(PlayerType playerType) {
        PlayerCharacterCreator creator = playerCreators.get(Objects.requireNonNull(playerType, "playerType"));
        if (creator == null) {
            throw new IllegalArgumentException("No player creator registered for " + playerType);
        }
        return creator.create(statusEffectRegistryFactory.create());
    }

    @Override
    public EnemyCombatant createEnemy(EnemyType enemyType, String name) {
        EnemyCombatantCreator creator = enemyCreators.get(Objects.requireNonNull(enemyType, "enemyType"));
        if (creator == null) {
            throw new IllegalArgumentException("No enemy creator registered for " + enemyType);
        }
        return creator.create(name, statusEffectRegistryFactory.create());
    }

    private static EnumMap<PlayerType, PlayerCharacterCreator> copyPlayerCreators(
        Map<PlayerType, PlayerCharacterCreator> playerCreators
    ) {
        EnumMap<PlayerType, PlayerCharacterCreator> copy = new EnumMap<>(PlayerType.class);
        copy.putAll(Objects.requireNonNull(playerCreators, "playerCreators"));
        return copy;
    }

    private static EnumMap<EnemyType, EnemyCombatantCreator> copyEnemyCreators(
        Map<EnemyType, EnemyCombatantCreator> enemyCreators
    ) {
        EnumMap<EnemyType, EnemyCombatantCreator> copy = new EnumMap<>(EnemyType.class);
        copy.putAll(Objects.requireNonNull(enemyCreators, "enemyCreators"));
        return copy;
    }
}
