package sc2002.turnbased.ui.gui.view;

import java.util.List;

import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.ui.gui.model.PlayerTurnRequest;

public interface BattleView {
    void showSetupPreview();

    void setSetupControlsEnabled(boolean enabled);

    void clearBattleLog();

    void appendLog(String line);

    void showBattleLoading();

    void showBattleLoaded(BattleSetup setup);

    void showBattleEvent(BattleEvent event, String battleMessage, List<String> transcriptLines);

    void showPlayerTurn(PlayerTurnRequest turn);

    void showCommandResolving(String actionName);

    void showBattleComplete();

    void showBattleError(String message);

    void showUnavailableCommand();

    void showNewSetupPrompt();

    CombatantId selectedEnemyId();

    void selectNextEnemy(int direction);

    PostGameChoice askPostGameChoice();

    void exitGame();
}
