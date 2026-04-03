package sc2002.turnbased.ui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleEventListener;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.ui.BattleConsoleFormatter;

/**
 * Graphical front-end: setup panel, battle transcript, and post-game flow.
 * Uses the same {@link BattleEngine} and rules as the CLI.
 * Run: {@code java -cp out sc2002.turnbased.ui.gui.TurnBasedArenaGui}
 */
public class TurnBasedArenaGui extends JFrame {
    private final JTextArea log = new JTextArea();
    private final BattleConsoleFormatter formatter = new BattleConsoleFormatter();
    private final BattleSetupFactory setupFactory = new BattleSetupFactory();
    private final ExecutorService battleExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "battle-engine");
        t.setDaemon(true);
        return t;
    });

    public TurnBasedArenaGui() {
        super("SC2002 Turn-Based Arena");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        log.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(720, 420));
        add(scroll, BorderLayout.CENTER);

        JPanel setup = buildSetupPanel();
        add(setup, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildSetupPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JComboBox<PlayerType> playerBox = new JComboBox<>(PlayerType.values());
        playerBox.setRenderer(displayNameRenderer());
        JComboBox<DifficultyLevel> diffBox = new JComboBox<>(DifficultyLevel.values());
        diffBox.setRenderer(displayNameRenderer());
        JComboBox<ItemType> item1Box = new JComboBox<>(ItemType.values());
        item1Box.setRenderer(displayNameRenderer());
        JComboBox<ItemType> item2Box = new JComboBox<>(ItemType.values());
        item2Box.setRenderer(displayNameRenderer());

        JButton start = new JButton("Start battle");
        start.addActionListener(e -> startBattle(
            (PlayerType) playerBox.getSelectedItem(),
            (DifficultyLevel) diffBox.getSelectedItem(),
            (ItemType) item1Box.getSelectedItem(),
            (ItemType) item2Box.getSelectedItem()
        ));

        p.add(new JLabel("Class:"));
        p.add(playerBox);
        p.add(new JLabel("Difficulty:"));
        p.add(diffBox);
        p.add(new JLabel("Item 1:"));
        p.add(item1Box);
        p.add(new JLabel("Item 2:"));
        p.add(item2Box);
        p.add(start);
        return p;
    }

    private static DefaultListCellRenderer displayNameRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PlayerType playerType) {
                    setText(playerType.getDisplayName());
                } else if (value instanceof DifficultyLevel difficultyLevel) {
                    setText(difficultyLevel.getDisplayName());
                } else if (value instanceof ItemType itemType) {
                    setText(itemType.getDisplayName());
                }
                return this;
            }
        };
    }

    private void appendLog(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void startBattle(PlayerType playerType, DifficultyLevel difficulty, ItemType item1, ItemType item2) {
        GameConfiguration configuration = new GameConfiguration(playerType, difficulty, List.of(item1, item2));
        appendLog("=== Selected: " + playerType.getDisplayName() + " | " + difficulty.getDisplayName()
            + " | Items: " + item1.getDisplayName() + ", " + item2.getDisplayName() + " ===\n");

        battleExecutor.submit(() -> {
            try {
                BattleSetup setup = setupFactory.create(configuration);
                BattleEngine engine = new BattleEngine(setup, new SpeedTurnOrderStrategy());
                GuiPlayerDecisionProvider decisions = new GuiPlayerDecisionProvider(this, setup.getInventory());
                BattleEventListener listener = event -> SwingUtilities.invokeLater(() -> {
                    for (String line : formatter.format(List.of(event))) {
                        appendLog(line);
                    }
                });
                engine.runUntilBattleEnds(decisions, listener);
                SwingUtilities.invokeLater(() -> {
                    appendLog("Battle complete.");
                    promptPostGame(configuration);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    appendLog("Error: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Battle error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    private void promptPostGame(GameConfiguration lastConfig) {
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
        if (n == 0) {
            startBattle(lastConfig.playerType(), lastConfig.difficultyLevel(),
                lastConfig.selectedItems().get(0), lastConfig.selectedItems().get(1));
        } else if (n == 1) {
            appendLog("\n--- Configure a new battle from the panel above. ---\n");
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TurnBasedArenaGui w = new TurnBasedArenaGui();
            w.setVisible(true);
            w.appendLog("SC2002 Turn-Based Combat Arena (GUI)\n"
                + "Choose class, difficulty, two items, then Start battle.\n");
        });
    }
}
