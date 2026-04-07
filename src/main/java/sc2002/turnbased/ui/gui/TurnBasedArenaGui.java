package sc2002.turnbased.ui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleEventListener;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.ui.BattleConsoleFormatter;

/**
 * Graphical front-end: setup panel, battle transcript, and post-game flow.
 * Uses the same {@link BattleEngine} and rules as the CLI.
 * Run: {@code java -cp out sc2002.turnbased.ui.gui.TurnBasedArenaGui}
 */
public class TurnBasedArenaGui extends JFrame {
    private static final Object CUSTOM_DIFFICULTY = new Object() {
        @Override
        public String toString() {
            return "Custom Mode (build your own waves)";
        }
    };

    private final JTextArea log = new JTextArea();
    private final BattleConsoleFormatter formatter = new BattleConsoleFormatter();
    private final BattleSetupFactory setupFactory = new BattleSetupFactory();
    private final ExecutorService battleExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "battle-engine");
        t.setDaemon(true);
        return t;
    });

    private JComboBox<Object> diffBox;
    private JPanel customWavePanel;
    private JSpinner w1Goblins;
    private JSpinner w1Wolves;
    private JSpinner w2Goblins;
    private JSpinner w2Wolves;
    private JCheckBox secondWaveCheck;

    private JComboBox<PlayerType> playerBox;
    private JComboBox<ItemType> item1Box;
    private JComboBox<ItemType> item2Box;

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
        JPanel outer = new JPanel();
        outer.setLayout(new BorderLayout(0, 8));
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerBox = new JComboBox<>(PlayerType.values());
        playerBox.setRenderer(displayNameRenderer());
        diffBox = new JComboBox<>();
        for (DifficultyLevel d : DifficultyLevel.values()) {
            diffBox.addItem(d);
        }
        diffBox.addItem(CUSTOM_DIFFICULTY);
        diffBox.setRenderer(difficultyComboRenderer());
        item1Box = new JComboBox<>(ItemType.values());
        item1Box.setRenderer(displayNameRenderer());
        item2Box = new JComboBox<>(ItemType.values());
        item2Box.setRenderer(displayNameRenderer());

        JButton start = new JButton("Start battle");
        start.addActionListener(e -> onStartBattleClicked());

        row.add(new JLabel("Class:"));
        row.add(playerBox);
        row.add(new JLabel("Difficulty:"));
        row.add(diffBox);
        row.add(new JLabel("Item 1:"));
        row.add(item1Box);
        row.add(new JLabel("Item 2:"));
        row.add(item2Box);
        row.add(start);

        customWavePanel = buildCustomWavePanel();
        customWavePanel.setVisible(false);

        diffBox.addActionListener(e -> {
            boolean custom = diffBox.getSelectedItem() == CUSTOM_DIFFICULTY;
            customWavePanel.setVisible(custom);
            outer.revalidate();
            pack();
        });

        outer.add(row, BorderLayout.NORTH);
        outer.add(customWavePanel, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildCustomWavePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout(4, 4));
        p.setBorder(BorderFactory.createTitledBorder(
            "Custom waves (max 4 enemies per wave, max 3 per type)"));

        w1Goblins = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
        w1Wolves = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));
        w2Goblins = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
        w2Wolves = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));

        secondWaveCheck = new JCheckBox("Second wave");
        secondWaveCheck.addActionListener(e -> updateSecondWaveEnabled());
        updateSecondWaveEnabled();

        JPanel wave1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wave1.add(new JLabel("Wave 1 — Goblins:"));
        wave1.add(w1Goblins);
        wave1.add(new JLabel("Wolves:"));
        wave1.add(w1Wolves);

        JPanel wave2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wave2.add(secondWaveCheck);
        wave2.add(new JLabel("Wave 2 — Goblins:"));
        wave2.add(w2Goblins);
        wave2.add(new JLabel("Wolves:"));
        wave2.add(w2Wolves);

        JPanel stack = new JPanel();
        stack.setLayout(new javax.swing.BoxLayout(stack, javax.swing.BoxLayout.Y_AXIS));
        stack.add(wave1);
        stack.add(wave2);
        p.add(stack, BorderLayout.CENTER);
        return p;
    }

    private void updateSecondWaveEnabled() {
        boolean on = secondWaveCheck.isSelected();
        w2Goblins.setEnabled(on);
        w2Wolves.setEnabled(on);
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

    private static DefaultListCellRenderer difficultyComboRenderer() {
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
                if (value instanceof DifficultyLevel difficultyLevel) {
                    setText(difficultyLevel.getDisplayName());
                } else if (value == CUSTOM_DIFFICULTY) {
                    setText(value.toString());
                }
                return this;
            }
        };
    }

    private void appendLog(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void onStartBattleClicked() {
        PlayerType playerType = (PlayerType) playerBox.getSelectedItem();
        ItemType item1 = (ItemType) item1Box.getSelectedItem();
        ItemType item2 = (ItemType) item2Box.getSelectedItem();
        List<ItemType> items = List.of(item1, item2);
        Object diffSel = diffBox.getSelectedItem();

        if (diffSel instanceof DifficultyLevel difficulty) {
            GameConfiguration configuration = new GameConfiguration(playerType, difficulty, items);
            appendLog("=== Selected: " + playerType.getDisplayName() + " | " + difficulty.getDisplayName()
                + " | Items: " + item1.getDisplayName() + ", " + item2.getDisplayName() + " ===\n");
            startPresetBattle(configuration);
            return;
        }

        try {
            List<WaveSpec> waves = new ArrayList<>();
            waves.add(new WaveSpec((int) w1Goblins.getValue(), (int) w1Wolves.getValue()));
            if (secondWaveCheck.isSelected()) {
                waves.add(new WaveSpec((int) w2Goblins.getValue(), (int) w2Wolves.getValue()));
            }
            CustomGameConfiguration customConfig = new CustomGameConfiguration(playerType, items, waves);
            appendCustomConfigurationLog(customConfig);
            startCustomBattle(customConfig);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Invalid custom setup",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void appendCustomConfigurationLog(CustomGameConfiguration config) {
        appendLog("=== Custom Mode Configuration ===");
        appendLog("Player Class: " + config.playerType().getDisplayName());
        appendLog("Items: " + config.selectedItems().get(0).getDisplayName()
            + ", " + config.selectedItems().get(1).getDisplayName());
        for (int i = 0; i < config.waves().size(); i++) {
            WaveSpec wave = config.waves().get(i);
            appendLog("Wave " + (i + 1) + ": "
                + wave.goblinCount() + " Goblin(s), "
                + wave.wolfCount() + " Wolf/Wolves — "
                + wave.totalEnemies() + " enemies total");
        }
        appendLog("");
    }

    private void startPresetBattle(GameConfiguration configuration) {
        battleExecutor.submit(() -> runBattle(() -> setupFactory.create(configuration), configuration));
    }

    private void startCustomBattle(CustomGameConfiguration configuration) {
        battleExecutor.submit(() -> runBattle(() -> setupFactory.createCustom(configuration), configuration));
    }

    private void runBattle(Supplier<BattleSetup> setupSupplier, Object lastConfigForPostGame) {
        try {
            BattleSetup setup = setupSupplier.get();
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
                promptPostGame(lastConfigForPostGame);
            });
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                appendLog("Error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Battle error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void promptPostGame(Object lastConfig) {
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
            if (lastConfig instanceof GameConfiguration g) {
                startPresetBattle(g);
            } else if (lastConfig instanceof CustomGameConfiguration c) {
                startCustomBattle(c);
            }
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
                + "Choose class, difficulty (or Custom Mode), two items, then Start battle.\n"
                + "Custom mode: set Wave 1 (and optional Wave 2) — same rules as CLI.\n");
        });
    }
}
