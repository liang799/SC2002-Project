package sc2002.turnbased.domain;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public class DefaultCombatantFactory implements CombatantFactory {
    private final StatusEffectRegistryFactory statusEffectRegistryFactory;
    private final BattleAction basicAttackAction;
    private final BattleAction shieldBashAction;
    private final BattleAction arcaneBlastAction;

    public DefaultCombatantFactory(
        StatusEffectRegistryFactory statusEffectRegistryFactory,
        BattleAction basicAttackAction,
        BattleAction shieldBashAction,
        BattleAction arcaneBlastAction
    ) {
        this.statusEffectRegistryFactory = Objects.requireNonNull(statusEffectRegistryFactory, "statusEffectRegistryFactory");
        this.basicAttackAction = Objects.requireNonNull(basicAttackAction, "basicAttackAction");
        this.shieldBashAction = Objects.requireNonNull(shieldBashAction, "shieldBashAction");
        this.arcaneBlastAction = Objects.requireNonNull(arcaneBlastAction, "arcaneBlastAction");
    }

    @Override
    public PlayerCharacter createPlayer(PlayerType playerType) {
        return switch (Objects.requireNonNull(playerType, "playerType")) {
            case WARRIOR -> new Warrior(
                statusEffectRegistryFactory.create(),
                new SpecialSkill(shieldBashAction, 3)
            );
            case WIZARD -> new Wizard(
                statusEffectRegistryFactory.create(),
                new SpecialSkill(arcaneBlastAction, 3)
            );
        };
    }

    @Override
    public EnemyCombatant createEnemy(EnemyType enemyType, String name) {
        return switch (Objects.requireNonNull(enemyType, "enemyType")) {
            case GOBLIN -> new Goblin(name, statusEffectRegistryFactory.create(), basicAttackAction);
            case WOLF -> new Wolf(name, statusEffectRegistryFactory.create(), basicAttackAction);
        };
    }
}
