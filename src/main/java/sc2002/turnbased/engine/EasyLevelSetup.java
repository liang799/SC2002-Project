package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.ItemType;

public final class EasyLevelSetup {
    private EasyLevelSetup() {
    }

    public static BattleSetup createWarriorPotionSmokeBombSetup() {
        return new BattleSetupFactory().create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.EASY,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB)
            )
        );
    }
}
