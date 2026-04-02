package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.ItemType;

public final class MediumLevelSetup {
    private MediumLevelSetup() {
    }

    public static BattleSetup createWarriorPowerStonePotionSetup() {
        return new BattleSetupFactory().create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POWER_STONE, ItemType.POTION)
            )
        );
    }

    public static BattleSetup createWizardPowerStonePotionSetup() {
        return new BattleSetupFactory().create(
            new GameConfiguration(
                PlayerType.WIZARD,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POWER_STONE, ItemType.POTION)
            )
        );
    }
}
