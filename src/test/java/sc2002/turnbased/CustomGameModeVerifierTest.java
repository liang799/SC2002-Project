package sc2002.turnbased;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.support.TestDependencies;

class CustomGameModeVerifierTest {
    private final BattleSetupFactory battleSetupFactory = TestDependencies.battleSetupFactory();

    @Test
    @Tag("unit")
    @DisplayName("WaveSpec accepts valid enemy combinations")
    void givenValidEnemyCounts_whenCreatingWaveSpec_thenInstantiatesSuccessfully() {
        assertAll(
            () -> assertDoesNotThrow(() -> waveSpec(1, 0)),
            () -> assertDoesNotThrow(() -> waveSpec(0, 1)),
            () -> assertDoesNotThrow(() -> waveSpec(3, 1)),
            () -> assertDoesNotThrow(() -> waveSpec(1, 3)),
            () -> assertDoesNotThrow(() -> waveSpec(0, 3)),
            () -> assertDoesNotThrow(() -> waveSpec(2, 2))
        );
    }

    @Test
    @Tag("unit")
    @DisplayName("WaveSpec rejects invalid enemy combinations")
    void givenInvalidEnemyCounts_whenCreatingWaveSpec_thenThrowsIllegalArgumentException() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(0, 0)),
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(3, 2)),
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(4, 0)),
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(0, 4)),
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(-1, 1)),
            () -> assertThrows(IllegalArgumentException.class, () -> waveSpec(1, -1))
        );
    }

    @Test
    @Tag("unit")
    @DisplayName("WaveSpec totals all configured enemies")
    void givenValidEnemyCounts_whenCountingTotalEnemies_thenReturnsCombinedTotal() {
        WaveSpec waveSpec = waveSpec(2, 1);

        assertEquals(3, waveSpec.totalEnemies());
    }

    @Test
    @Tag("unit")
    @DisplayName("CustomGameConfiguration accepts one-wave and two-wave setups")
    void givenValidInputs_whenCreatingCustomGameConfiguration_thenInstantiatesSuccessfully() {
        List<ItemType> selectedItems = selectedItems(ItemType.POTION, ItemType.SMOKE_BOMB);

        assertAll(
            () -> assertDoesNotThrow(() -> customConfig(PlayerType.WARRIOR, selectedItems, waveSpec(2, 1))),
            () -> assertDoesNotThrow(() -> customConfig(PlayerType.WIZARD, selectedItems, waveSpec(1, 1), waveSpec(0, 2)))
        );
    }

    @Test
    @Tag("unit")
    @DisplayName("CustomGameConfiguration rejects invalid setup inputs")
    void givenInvalidInputs_whenCreatingCustomGameConfiguration_thenThrows() {
        List<ItemType> selectedItems = selectedItems(ItemType.POTION, ItemType.SMOKE_BOMB);

        assertAll(
            () -> assertThrows(
                IllegalArgumentException.class,
                () -> customConfig(PlayerType.WARRIOR, List.of(ItemType.POTION), waveSpec(1, 0))
            ),
            () -> assertThrows(
                IllegalArgumentException.class,
                () -> new CustomGameConfiguration(PlayerType.WARRIOR, selectedItems, List.of())
            ),
            () -> assertThrows(
                IllegalArgumentException.class,
                () -> customConfig(PlayerType.WARRIOR, selectedItems, waveSpec(1, 0), waveSpec(1, 0), waveSpec(1, 0))
            ),
            () -> assertThrows(
                NullPointerException.class,
                () -> customConfig(null, selectedItems, waveSpec(1, 0))
            )
        );
    }

    @Test
    @Tag("integration")
    @DisplayName("Custom battle setup keeps a single wave in the initial enemy list")
    void givenSingleWaveCustomConfiguration_whenCreatingBattleSetup_thenCreatesOnlyInitialEnemies() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(PlayerType.WARRIOR, selectedItems(ItemType.POTION, ItemType.POWER_STONE), waveSpec(1, 0))
        );

        assertAll(
            () -> assertEquals(1, battleSetup.getInitialEnemies().size()),
            () -> assertEquals(0, battleSetup.getBackupEnemies().size())
        );
    }

    @Test
    @Tag("integration")
    @DisplayName("Custom battle setup creates every configured enemy in a wave")
    void givenFourEnemyCustomConfiguration_whenCreatingBattleSetup_thenCreatesAllConfiguredEnemies() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(PlayerType.WIZARD, selectedItems(ItemType.POTION, ItemType.POWER_STONE), waveSpec(2, 2))
        );

        assertAll(
            () -> assertEquals(4, battleSetup.getInitialEnemies().size()),
            () -> assertEquals(0, battleSetup.getBackupEnemies().size())
        );
    }

    @Test
    @Tag("integration")
    @DisplayName("Custom battle setup splits configured waves into initial and backup enemies")
    void givenTwoWaveCustomConfiguration_whenCreatingBattleSetup_thenCreatesInitialAndBackupEnemies() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(
                PlayerType.WARRIOR,
                selectedItems(ItemType.POTION, ItemType.POWER_STONE),
                waveSpec(2, 0),
                waveSpec(0, 2)
            )
        );

        assertAll(
            () -> assertEquals(2, battleSetup.getInitialEnemies().size()),
            () -> assertEquals(2, battleSetup.getBackupEnemies().size())
        );
    }

    @Test
    @Tag("integration")
    @DisplayName("Custom battle setup assigns distinct names across repeated enemy types")
    void givenRepeatedEnemyTypesAcrossWaves_whenCreatingBattleSetup_thenAssignsDistinctEnemyNames() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(
                PlayerType.WARRIOR,
                selectedItems(ItemType.POTION, ItemType.POWER_STONE),
                waveSpec(3, 0),
                waveSpec(3, 0)
            )
        );

        List<String> enemyNames = new ArrayList<>();
        for (Combatant enemy : battleSetup.getInitialEnemies()) {
            enemyNames.add(enemy.getName());
        }
        for (Combatant enemy : battleSetup.getBackupEnemies()) {
            enemyNames.add(enemy.getName());
        }

        assertEquals(6L, enemyNames.stream().distinct().count());
    }

    @Test
    @Tag("integration")
    @DisplayName("Custom battle setup preserves player selection and chosen items")
    void givenPlayerTypeAndSelectedItems_whenCreatingBattleSetup_thenCreatesMatchingPlayerStatsAndInventory() {
        BattleSetup wizardSetup = battleSetupFactory.createCustom(
            customConfig(PlayerType.WIZARD, selectedItems(ItemType.POTION, ItemType.POWER_STONE), waveSpec(1, 0))
        );
        BattleSetup warriorSetup = battleSetupFactory.createCustom(
            customConfig(PlayerType.WARRIOR, selectedItems(ItemType.POTION, ItemType.POWER_STONE), waveSpec(1, 0))
        );

        assertAll(
            () -> assertEquals(50, wizardSetup.getPlayer().getAttack()),
            () -> assertEquals(40, warriorSetup.getPlayer().getAttack()),
            () -> assertEquals(1, wizardSetup.getInventory().countOf(ItemType.POTION)),
            () -> assertEquals(1, wizardSetup.getInventory().countOf(ItemType.POWER_STONE))
        );
    }

    @Test
    @Tag("e2e")
    @DisplayName("Single-wave custom battles run to victory")
    void givenSingleWaveCustomBattle_whenRunningUntilBattleEnds_thenPlayerWins() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(PlayerType.WARRIOR, selectedItems(ItemType.POTION, ItemType.SMOKE_BOMB), waveSpec(1, 0))
        );

        List<BattleEvent> events = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy())
            .runUntilBattleEnds(new AlwaysAttackFirstEnemyProvider());

        RoundSummaryEvent lastSummary = lastSummary(events);

        assertNotNull(lastSummary, "Expected the battle to emit at least one round summary");
        assertAll(
            () -> assertTrue(lastSummary.getPlayerSummary().getCurrentHp() > 0),
            () -> assertTrue(lastSummary.getEnemySummaries().stream().noneMatch(enemy -> enemy.getCurrentHp() > 0))
        );
    }

    @Test
    @Tag("e2e")
    @DisplayName("Two-wave custom battles trigger the backup spawn and still end in victory")
    void givenTwoWaveCustomBattle_whenRunningUntilBattleEnds_thenBackupSpawnTriggersAndPlayerWins() {
        BattleSetup battleSetup = battleSetupFactory.createCustom(
            customConfig(
                PlayerType.WARRIOR,
                selectedItems(ItemType.POTION, ItemType.SMOKE_BOMB),
                waveSpec(1, 0),
                waveSpec(0, 1)
            )
        );

        List<BattleEvent> events = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy())
            .runUntilBattleEnds(new AlwaysAttackFirstEnemyProvider());

        boolean backupSpawned = events.stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch(text -> text.startsWith("Backup Spawn triggered"));
        RoundSummaryEvent lastSummary = lastSummary(events);

        assertNotNull(lastSummary, "Expected the battle to emit at least one round summary");
        assertAll(
            () -> assertTrue(backupSpawned),
            () -> assertTrue(lastSummary.getPlayerSummary().getCurrentHp() > 0),
            () -> assertTrue(lastSummary.getEnemySummaries().stream().noneMatch(enemy -> enemy.getCurrentHp() > 0))
        );
    }

    private static CustomGameConfiguration customConfig(PlayerType playerType, List<ItemType> items, WaveSpec... waves) {
        return new CustomGameConfiguration(playerType, items, List.of(waves));
    }

    private static List<ItemType> selectedItems(ItemType firstItem, ItemType secondItem) {
        return List.of(firstItem, secondItem);
    }

    private static WaveSpec waveSpec(int goblinCount, int wolfCount) {
        return WaveSpec.of(
            EnemyCount.of(EnemyType.GOBLIN, goblinCount),
            EnemyCount.of(EnemyType.WOLF, wolfCount)
        );
    }

    private static RoundSummaryEvent lastSummary(List<BattleEvent> events) {
        return events.stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .reduce((first, second) -> second)
            .orElse(null);
    }

    private static final class AlwaysAttackFirstEnemyProvider implements PlayerDecisionProvider {
        @Override
        public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
            return PlayerDecision.targeted(new BasicAttackAction(), livingEnemies.get(0).getName());
        }
    }
}
