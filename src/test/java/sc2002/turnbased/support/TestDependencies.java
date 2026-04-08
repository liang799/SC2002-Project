package sc2002.turnbased.support;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.DefaultCombatantFactory;
import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Warrior;
import sc2002.turnbased.domain.Wizard;
import sc2002.turnbased.domain.status.StatusEffectEventPublisher;
import sc2002.turnbased.domain.status.StatusEffectRegistry;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerType;

public final class TestDependencies {
    private static final StatusEffectRegistryFactory STATUS_EFFECT_REGISTRY_FACTORY = () ->
        new StatusEffectRegistry(new StatusEffectEventPublisher());

    private static final CombatantFactory COMBATANT_FACTORY = new DefaultCombatantFactory(
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

    public static Warrior warrior() {
        return (Warrior) COMBATANT_FACTORY.createPlayer(PlayerType.WARRIOR);
    }

    public static Wizard wizard() {
        return (Wizard) COMBATANT_FACTORY.createPlayer(PlayerType.WIZARD);
    }

    public static Goblin goblin(String name) {
        return (Goblin) COMBATANT_FACTORY.createEnemy(EnemyType.GOBLIN, name);
    }

    public static StatusEffectRegistry registry() {
        return STATUS_EFFECT_REGISTRY_FACTORY.create();
    }
}
