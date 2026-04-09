package sc2002.turnbased.support;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.bootstrap.CombatantFactories;
import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.DefaultStatusEffectRegistryFactory;
import sc2002.turnbased.domain.status.StatusEffectRegistry;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public final class TestDependencies {
    private static final StatusEffectRegistryFactory STATUS_EFFECT_REGISTRY_FACTORY =
        new DefaultStatusEffectRegistryFactory(StatusEffectRegistry::new);

    private static final CombatantFactory COMBATANT_FACTORY = CombatantFactories.createDefault(
        STATUS_EFFECT_REGISTRY_FACTORY,
        new BasicAttackAction(),
        new ShieldBashAction(),
        new ArcaneBlastAction()
    );

    private TestDependencies() {
    }

    public static CombatantFactory combatantFactory() {
        return COMBATANT_FACTORY;
    }

    public static BattleSetupFactory battleSetupFactory() {
        return new BattleSetupFactory(COMBATANT_FACTORY);
    }

    public static PlayerCharacter warrior() {
        return COMBATANT_FACTORY.createPlayer(PlayerType.WARRIOR);
    }

    public static PlayerCharacter wizard() {
        return COMBATANT_FACTORY.createPlayer(PlayerType.WIZARD);
    }

    public static EnemyCombatant goblin(String name) {
        return COMBATANT_FACTORY.createEnemy(EnemyType.GOBLIN, name);
    }

    public static StatusEffectRegistry registry() {
        return STATUS_EFFECT_REGISTRY_FACTORY.create();
    }
}
