package sc2002.turnbased.ui;

import java.util.List;
import java.util.Scanner;

import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleEventListener;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;

public class TurnBasedArenaCli {
    private final ConsoleBattleUi ui;
    private final BattleSetupFactory battleSetupFactory;
    private final BattleConsoleFormatter formatter;

    public TurnBasedArenaCli(ConsoleBattleUi ui) {
        this.ui = ui;
        this.battleSetupFactory = new BattleSetupFactory();
        this.formatter = new BattleConsoleFormatter();
    }

    public static void main(String[] args) {
        ConsoleBattleUi ui = new ConsoleBattleUi(new Scanner(System.in), System.out);
        new TurnBasedArenaCli(ui).run();
    }

    public void run() {
        ui.showLoadingScreen();

        GameConfiguration previousConfiguration = null;
        boolean replaySameSettings = false;

        while (true) {
            GameConfiguration configuration = replaySameSettings && previousConfiguration != null
                ? previousConfiguration
                : promptForConfiguration();

            previousConfiguration = configuration;
            replaySameSettings = false;
            ui.showConfigurationSummary(configuration);

            BattleSetup battleSetup = battleSetupFactory.create(configuration);
            PlayerDecisionProvider decisionProvider = new CliPlayerDecisionProvider(ui, battleSetup.getInventory());
            BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
            BattleEventListener battleEventListener = event -> ui.showBattleLines(formatter.format(List.of(event)));
            battleEngine.runUntilBattleEnds(decisionProvider, battleEventListener);
            ui.showMessage("Battle complete.");

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

    private GameConfiguration promptForConfiguration() {
        PlayerType playerType = ui.promptForPlayerType();
        DifficultyLevel difficultyLevel = ui.promptForDifficultyLevel();
        return new GameConfiguration(playerType, difficultyLevel, ui.promptForItems(2));
    }
}
