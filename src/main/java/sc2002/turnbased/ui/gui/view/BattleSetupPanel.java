package sc2002.turnbased.ui.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.ui.gui.setup.BattleLaunchRequest;

public final class BattleSetupPanel extends JPanel {
    private static final Object CUSTOM_DIFFICULTY = new Object() {
        @Override
        public String toString() {
            return "Custom Mode (build your own waves)";
        }
    };

    private final Consumer<BattleLaunchRequest> startListener;
    private final Runnable layoutChangedListener;
    private final JComboBox<PlayerType> playerBox = new JComboBox<>(PlayerType.values());
    private final JComboBox<Object> diffBox = new JComboBox<>();
    private final JComboBox<ItemType> item1Box = new JComboBox<>(ItemType.values());
    private final JComboBox<ItemType> item2Box = new JComboBox<>(ItemType.values());
    private final JButton startButton = new JButton("Start battle");
    private final JPanel customWavePanel;
    private final JSpinner w1Goblins = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
    private final JSpinner w1Wolves = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));
    private final JSpinner w2Goblins = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
    private final JSpinner w2Wolves = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));
    private final JCheckBox secondWaveCheck = new JCheckBox("Second wave");
    private boolean battleRunning;

    public BattleSetupPanel(Consumer<BattleLaunchRequest> startListener, Runnable layoutChangedListener) {
        this.startListener = Objects.requireNonNull(startListener, "startListener");
        this.layoutChangedListener = Objects.requireNonNull(layoutChangedListener, "layoutChangedListener");
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        playerBox.setRenderer(displayNameRenderer());
        diffBox.setRenderer(difficultyComboRenderer());
        item1Box.setRenderer(displayNameRenderer());
        item2Box.setRenderer(displayNameRenderer());
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            diffBox.addItem(difficulty);
        }
        diffBox.addItem(CUSTOM_DIFFICULTY);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel("Class:"));
        row.add(playerBox);
        row.add(new JLabel("Difficulty:"));
        row.add(diffBox);
        row.add(new JLabel("Item 1:"));
        row.add(item1Box);
        row.add(new JLabel("Item 2:"));
        row.add(item2Box);
        row.add(startButton);

        customWavePanel = buildCustomWavePanel();
        customWavePanel.setVisible(false);

        diffBox.addActionListener(e -> updateCustomWaveVisibility());
        startButton.addActionListener(e -> onStartBattleClicked());

        add(row, BorderLayout.NORTH);
        add(customWavePanel, BorderLayout.CENTER);
    }

    public void setSetupControlsEnabled(boolean enabled) {
        battleRunning = !enabled;
        setComponentTreeEnabled(this, enabled);
        updateSecondWaveEnabled();
    }

    private JPanel buildCustomWavePanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(BorderFactory.createTitledBorder(
            "Custom waves (max 4 enemies per wave, max 3 per type)"));

        secondWaveCheck.addActionListener(e -> updateSecondWaveEnabled());
        updateSecondWaveEnabled();

        JPanel wave1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wave1.add(new JLabel("Wave 1 - Goblins:"));
        wave1.add(w1Goblins);
        wave1.add(new JLabel("Wolves:"));
        wave1.add(w1Wolves);

        JPanel wave2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wave2.add(secondWaveCheck);
        wave2.add(new JLabel("Wave 2 - Goblins:"));
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

    private void updateCustomWaveVisibility() {
        customWavePanel.setVisible(diffBox.getSelectedItem() == CUSTOM_DIFFICULTY);
        revalidate();
        layoutChangedListener.run();
    }

    private void updateSecondWaveEnabled() {
        boolean enabled = secondWaveCheck.isSelected() && !battleRunning;
        w2Goblins.setEnabled(enabled);
        w2Wolves.setEnabled(enabled);
    }

    private void onStartBattleClicked() {
        PlayerType playerType = (PlayerType) playerBox.getSelectedItem();
        ItemType item1 = (ItemType) item1Box.getSelectedItem();
        ItemType item2 = (ItemType) item2Box.getSelectedItem();
        List<ItemType> items = List.of(item1, item2);

        Object difficultySelection = diffBox.getSelectedItem();
        if (difficultySelection instanceof DifficultyLevel difficultyLevel) {
            startListener.accept(BattleLaunchRequest.preset(
                new GameConfiguration(playerType, difficultyLevel, items)
            ));
            return;
        }

        try {
            List<WaveSpec> waves = new ArrayList<>();
            waves.add(WaveSpec.of(
                EnemyCount.of(EnemyType.GOBLIN, (int) w1Goblins.getValue()),
                EnemyCount.of(EnemyType.WOLF, (int) w1Wolves.getValue())
            ));
            if (secondWaveCheck.isSelected()) {
                waves.add(WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, (int) w2Goblins.getValue()),
                    EnemyCount.of(EnemyType.WOLF, (int) w2Wolves.getValue())
                ));
            }
            startListener.accept(BattleLaunchRequest.custom(
                new CustomGameConfiguration(playerType, items, waves)
            ));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Invalid custom setup",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private static void setComponentTreeEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setComponentTreeEnabled(child, enabled);
            }
        }
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
}
