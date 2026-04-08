package sc2002.turnbased.bootstrap;

import java.util.EnumMap;
import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.DefaultCombatantFactory;
import sc2002.turnbased.domain.DefaultSpecialSkillFactory;
import sc2002.turnbased.domain.EnemyCombatantCreator;
import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.PlayerCharacterCreator;
import sc2002.turnbased.domain.SpecialSkillFactory;
import sc2002.turnbased.domain.Warrior;
import sc2002.turnbased.domain.Wizard;
import sc2002.turnbased.domain.Wolf;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public final class CombatantFactories {
    private CombatantFactories() {
    }

    public static CombatantFactory createDefault(
        StatusEffectRegistryFactory statusEffectRegistryFactory,
        BattleAction basicAttackAction,
        BattleAction shieldBashAction,
        BattleAction arcaneBlastAction
    ) {
        Objects.requireNonNull(statusEffectRegistryFactory, "statusEffectRegistryFactory");
        Objects.requireNonNull(basicAttackAction, "basicAttackAction");
        Objects.requireNonNull(shieldBashAction, "shieldBashAction");
        Objects.requireNonNull(arcaneBlastAction, "arcaneBlastAction");

        SpecialSkillFactory warriorSpecialSkillFactory = new DefaultSpecialSkillFactory(shieldBashAction, 3);
        SpecialSkillFactory wizardSpecialSkillFactory = new DefaultSpecialSkillFactory(arcaneBlastAction, 3);

        EnumMap<PlayerType, PlayerCharacterCreator> playerCreators = new EnumMap<>(PlayerType.class);
        playerCreators.put(PlayerType.WARRIOR, statusEffectRegistry ->
            new Warrior(statusEffectRegistry, warriorSpecialSkillFactory.create()));
        playerCreators.put(PlayerType.WIZARD, statusEffectRegistry ->
            new Wizard(statusEffectRegistry, wizardSpecialSkillFactory.create()));

        EnumMap<EnemyType, EnemyCombatantCreator> enemyCreators = new EnumMap<>(EnemyType.class);
        enemyCreators.put(EnemyType.GOBLIN, (name, statusEffectRegistry) ->
            new Goblin(name, statusEffectRegistry, basicAttackAction));
        enemyCreators.put(EnemyType.WOLF, (name, statusEffectRegistry) ->
            new Wolf(name, statusEffectRegistry, basicAttackAction));

        return new DefaultCombatantFactory(statusEffectRegistryFactory, playerCreators, enemyCreators);
    }
}
