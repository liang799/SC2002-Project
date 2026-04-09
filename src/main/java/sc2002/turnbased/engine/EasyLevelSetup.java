package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.bootstrap.CombatantFactories;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.status.DefaultStatusEffectRegistryFactory;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public final class EasyLevelSetup {
    private EasyLevelSetup() {
    }

    public static BattleSetup createWarriorPotionSmokeBombSetup() {
        return new BattleSetupFactory(CombatantFactories.createDefault(
            new DefaultStatusEffectRegistryFactory(StatusEffectRegistry::new),
            new BasicAttackAction(),
            new ShieldBashAction(),
            new ArcaneBlastAction()
        )).create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.EASY,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB)
            )
        );
    }
}
