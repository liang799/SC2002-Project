package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.domain.DefaultCombatantFactory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.status.DefaultStatusEffectRegistryFactory;

public final class MediumLevelSetup {
    private MediumLevelSetup() {
    }

    public static BattleSetup createWarriorPowerStonePotionSetup() {
        return new BattleSetupFactory(new DefaultCombatantFactory(
            new DefaultStatusEffectRegistryFactory(),
            new BasicAttackAction(),
            new ShieldBashAction(),
            new ArcaneBlastAction()
        )).create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POWER_STONE, ItemType.POTION)
            )
        );
    }

    public static BattleSetup createWizardPowerStonePotionSetup() {
        return new BattleSetupFactory(new DefaultCombatantFactory(
            new DefaultStatusEffectRegistryFactory(),
            new BasicAttackAction(),
            new ShieldBashAction(),
            new ArcaneBlastAction()
        )).create(
            new GameConfiguration(
                PlayerType.WIZARD,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POWER_STONE, ItemType.POTION)
            )
        );
    }
}
