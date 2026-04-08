package sc2002.turnbased.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleEventListener;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.TurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;

public class TurnBasedArenaCli {
    private final ConsoleBattleUi ui;
    private final BattleSetupFactory battleSetupFactory;
    private final BattleConsoleFormatter formatter;
    private final TurnOrderStrategy turnOrderStrategy;

    public TurnBasedArenaCli(
        ConsoleBattleUi ui,
        BattleSetupFactory battleSetupFactory,
        BattleConsoleFormatter formatter,
        TurnOrderStrategy turnOrderStrategy
    ) {
        this.ui = Objects.requireNonNull(ui, "ui");
        this.battleSetupFactory = Objects.requireNonNull(battleSetupFactory, "battleSetupFactory");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
        this.turnOrderStrategy = Objects.requireNonNull(turnOrderStrategy, "turnOrderStrategy");
    }

    public static void main(String[] args) {
        ConsoleBattleUi ui = new ConsoleBattleUi(new Scanner(System.in), System.out);
        new TurnBasedArenaCli(
            ui,
            new BattleSetupFactory(),
            new BattleConsoleFormatter(),
            new SpeedTurnOrderStrategy()
        ).run();
    }

    public void run() {
        ui.showLoadingScreen();

        // A Supplier<BattleSetup> lets us replay either preset or custom setups
        // without duplicating the branching logic.
        Supplier<BattleSetup> setupSupplier = null;
        boolean replaySameSettings = false;

        while (true) {
            if (!replaySameSettings || setupSupplier == null) {
                setupSupplier = promptForSetupSupplier();
            }
            replaySameSettings = false;

            BattleSetup battleSetup = setupSupplier.get();
            PlayerDecisionProvider decisionProvider = new CliPlayerDecisionProvider(ui, battleSetup.getInventory());
            BattleEngine battleEngine = new BattleEngine(battleSetup, turnOrderStrategy);
            BattleEventListener battleEventListener = event -> ui.showBattleLines(formatter.format(List.of(event)));
            battleEngine.runUntilBattleEnds(decisionProvider, battleEventListener);

            PostGameChoice choice = ui.promptPostGameChoice();
            if (choice == PostGameChoice.REPLAY) {
                replaySameSettings = true;
                continue;
            }
            if (choice == PostGameChoice.NEW_GAME) {
                continue;
            }
            return;
        }
    }

    private Supplier<BattleSetup> promptForSetupSupplier() {
        PlayerType playerType = ui.promptForPlayerType();
        List<ItemType> items = ui.promptForItems(2);
        DifficultyLevel difficulty = ui.promptForDifficultyOrCustom();

        if (difficulty != null) {
            // Preset mode
            GameConfiguration config = new GameConfiguration(playerType, difficulty, items);
            ui.showConfigurationSummary(config);
            return () -> battleSetupFactory.create(config);
        }

        // Custom mode
        int waveCount = ui.promptForWaveCount();
        List<WaveSpec> waves = new ArrayList<>();
        for (int i = 1; i <= waveCount; i++) {
            waves.add(ui.promptForWaveSpec(i));
        }
        CustomGameConfiguration customConfig = new CustomGameConfiguration(playerType, items, waves);
        showCustomConfigurationSummary(customConfig);
        return () -> battleSetupFactory.createCustom(customConfig);
    }

    private void showCustomConfigurationSummary(CustomGameConfiguration config) {
        ui.showMessage("");
        ui.showMessage("=== Custom Mode Configuration ===");
        ui.showMessage("Player Class: " + config.playerType().getDisplayName());
        ui.showMessage("Items: " + config.selectedItems().get(0).getDisplayName()
            + ", " + config.selectedItems().get(1).getDisplayName());
        for (int i = 0; i < config.waves().size(); i++) {
            WaveSpec wave = config.waves().get(i);
            ui.showMessage("Wave " + (i + 1) + ": "
                + wave.describe() + " — "
                + wave.totalEnemies() + " enemies total");
        }
        ui.showMessage("");
    }
}
