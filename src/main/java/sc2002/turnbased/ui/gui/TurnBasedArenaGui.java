package sc2002.turnbased.ui.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.bootstrap.CombatantFactories;
import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.status.DefaultStatusEffectRegistryFactory;
import sc2002.turnbased.domain.status.StatusEffectRegistry;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.ui.gui.controller.BattleController;
import sc2002.turnbased.ui.gui.model.PlayerTurnRequest;
import sc2002.turnbased.ui.gui.view.ArenaScenePanel;
import sc2002.turnbased.ui.gui.view.BattleCommandPanel;
import sc2002.turnbased.ui.gui.view.BattleSetupPanel;
import sc2002.turnbased.ui.gui.view.BattleView;
import sc2002.turnbased.ui.gui.view.PostGameChoice;

/**
 * Swing implementation of the battle view. Battle flow lives in {@link BattleController};
 * battle rules stay in the engine/domain layer.
 * Run: {@code java -cp out sc2002.turnbased.ui.gui.TurnBasedArenaGui}
 */
public class TurnBasedArenaGui extends JFrame implements BattleView {
    private final JTextArea log = new JTextArea();
    private final ArenaScenePanel arenaScene = new ArenaScenePanel();
    private final BattleCommandPanel commandPanel = new BattleCommandPanel();
    private final BattleSetupPanel setupPanel;
    private final BattleController controller;

    public TurnBasedArenaGui(BattleSetupFactory setupFactory) {
        super("SC2002 Turn-Based Arena");
        controller = new BattleController(this, setupFactory);
        setupPanel = new BattleSetupPanel(controller::startBattle, this::pack);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });

        add(setupPanel, BorderLayout.NORTH);
        add(arenaScene, BorderLayout.CENTER);
        add(buildEventFeed(), BorderLayout.EAST);
        commandPanel.setCommandListener(controller::handleCommand);
        arenaScene.setTargetSelectionListener(commandPanel::updateTarget);
        add(commandPanel, BorderLayout.SOUTH);
        showSetupPreview();

        pack();
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
    }

    @Override
    public void showSetupPreview() {
        arenaScene.showSetupPreview();
    }

    @Override
    public void setSetupControlsEnabled(boolean enabled) {
        setupPanel.setSetupControlsEnabled(enabled);
    }

    @Override
    public void clearBattleLog() {
        log.setText("");
    }

    @Override
    public void appendLog(String line) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> appendLog(line));
            return;
        }
        log.append(line + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    @Override
    public void showBattleLoading() {
        commandPanel.setIdle("Loading arena...");
    }

    @Override
    public void showBattleLoaded(BattleSetup setup) {
        arenaScene.startBattle(setup);
        commandPanel.setIdle("Battle loaded. Waiting for your first turn.");
    }

    @Override
    public void showBattleEvent(BattleEvent event, String battleMessage, List<String> transcriptLines) {
        arenaScene.applyBattleEvent(event);
        commandPanel.showBattleMessage(battleMessage);
        for (String line : transcriptLines) {
            appendLog(line);
        }
    }

    @Override
    public void showPlayerTurn(PlayerTurnRequest turn) {
        arenaScene.showPlayerTurn(turn.roundNumber(), turn.player(), turn.livingEnemies());
        commandPanel.showTurn(
            turn.roundNumber(),
            turn.player(),
            turn.livingEnemies(),
            arenaScene.getSelectedEnemyLabel()
        );
    }

    @Override
    public void showCommandResolving(String actionName) {
        arenaScene.completePlayerTurn(actionName);
        commandPanel.setResolving(actionName);
    }

    @Override
    public void showBattleComplete() {
        commandPanel.showBattleComplete();
    }

    @Override
    public void showBattleError(String message) {
        commandPanel.setIdle("Battle stopped after an error.");
        JOptionPane.showMessageDialog(this, message, "Battle error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showUnavailableCommand() {
        appendLog("That command is unavailable right now.");
    }

    @Override
    public void showNewSetupPrompt() {
        appendLog("\n--- Configure a new battle from the setup panel. ---\n");
    }

    @Override
    public CombatantId selectedEnemyId() {
        return arenaScene.getSelectedEnemyId();
    }

    @Override
    public void selectNextEnemy(int direction) {
        arenaScene.selectNextEnemy(direction);
        commandPanel.updateTarget(arenaScene.getSelectedEnemyLabel());
    }

    @Override
    public PostGameChoice askPostGameChoice() {
        String[] opts = { "Replay same settings", "New setup", "Exit" };
        int n = JOptionPane.showOptionDialog(
            this,
            "What next?",
            "Game over",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opts,
            opts[0]
        );
        return switch (n) {
            case 0 -> PostGameChoice.REPLAY;
            case 1 -> PostGameChoice.NEW_SETUP;
            default -> PostGameChoice.EXIT;
        };
    }

    @Override
    public void exitGame() {
        controller.shutdown();
        System.exit(0);
    }

    private JScrollPane buildEventFeed() {
        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        log.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(340, 520));
        scroll.setBorder(BorderFactory.createTitledBorder("Battle Feed"));
        return scroll;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CombatantFactory combatantFactory = CombatantFactories.createDefault(
                new DefaultStatusEffectRegistryFactory(StatusEffectRegistry::new),
                new BasicAttackAction(),
                new ShieldBashAction(),
                new ArcaneBlastAction()
            );
            TurnBasedArenaGui w = new TurnBasedArenaGui(new BattleSetupFactory(combatantFactory));
            w.setVisible(true);
            w.appendLog("SC2002 Turn-Based Combat Arena (2D)");
            w.appendLog("Choose class, difficulty, and two items, then Start battle.");
            w.appendLog("Move with WASD/arrows, click enemies to target, and use the 1-4 battle menu.");
        });
    }
}
