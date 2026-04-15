package sc2002.turnbased.ui.gui.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.support.TestDependencies;

class BattleLaunchRequestTest {
    @Test
    void presetRequestCreatesMatchingBattleSetup() {
        GameConfiguration configuration = new GameConfiguration(
            PlayerType.WIZARD,
            DifficultyLevel.MEDIUM,
            List.of(ItemType.POTION, ItemType.SMOKE_BOMB)
        );
        BattleLaunchRequest request = BattleLaunchRequest.preset(configuration);

        BattleSetup setup = request.createSetup(TestDependencies.battleSetupFactory());

        assertEquals(PostGameConfig.preset(configuration), request.replayConfiguration());
        assertTrue(request.intro().contains("Wizard"));
        assertTrue(request.intro().contains("Medium"));
        assertEquals("Wizard", setup.getPlayer().getName());
        assertEquals(1, setup.getPlayer().getInventory().countOf(ItemType.POTION));
        assertEquals(1, setup.getPlayer().getInventory().countOf(ItemType.SMOKE_BOMB));
        assertEquals(2, setup.getInitialEnemies().size());
        assertEquals(2, setup.getBackupEnemies().size());
    }

    @Test
    void customRequestCreatesConfiguredWavesAndIntro() {
        CustomGameConfiguration configuration = new CustomGameConfiguration(
            PlayerType.WARRIOR,
            List.of(ItemType.POWER_STONE, ItemType.SMOKE_BOMB),
            List.of(
                WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, 1),
                    EnemyCount.of(EnemyType.WOLF, 2)
                ),
                WaveSpec.of(EnemyCount.of(EnemyType.GOBLIN, 1))
            )
        );
        BattleLaunchRequest request = BattleLaunchRequest.custom(configuration);

        BattleSetup setup = request.createSetup(TestDependencies.battleSetupFactory());

        assertEquals(PostGameConfig.custom(configuration), request.replayConfiguration());
        assertTrue(request.intro().contains("Wave 1: 1 Goblin, 2 Wolves - 3 enemies total"));
        assertTrue(request.intro().contains("Wave 2: 1 Goblin - 1 enemy total"));
        assertEquals("Warrior", setup.getPlayer().getName());
        assertEquals(3, setup.getInitialEnemies().size());
        assertEquals(1, setup.getBackupEnemies().size());
    }

    @Test
    void replayRequestUsesExistingConfiguration() {
        GameConfiguration configuration = new GameConfiguration(
            PlayerType.WARRIOR,
            DifficultyLevel.EASY,
            List.of(ItemType.POTION, ItemType.POTION)
        );
        BattleLaunchRequest request = BattleLaunchRequest.replay(PostGameConfig.preset(configuration));

        BattleSetup setup = request.createSetup(TestDependencies.battleSetupFactory());

        assertEquals(PostGameConfig.preset(configuration), request.replayConfiguration());
        assertEquals("=== Replaying same settings ===", request.intro());
        assertEquals(3, setup.getInitialEnemies().size());
        assertEquals(0, setup.getBackupEnemies().size());
    }
}
