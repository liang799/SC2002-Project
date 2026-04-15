package sc2002.turnbased.ui.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;

public class BattleCommandPanel extends JPanel {
    private static final Color BUTTON_BACKGROUND = new Color(238, 244, 231);
    private static final Color BUTTON_HOVER_BACKGROUND = new Color(255, 226, 108);
    private static final Color BUTTON_DISABLED_BACKGROUND = new Color(185, 194, 187);
    private static final Color BUTTON_FOREGROUND = new Color(30, 38, 38);
    private static final Color BUTTON_DISABLED_FOREGROUND = new Color(94, 105, 101);

    public enum Command {
        BASIC_ATTACK,
        DEFEND,
        POTION,
        SPECIAL_SKILL,
        POWER_STONE,
        SMOKE_BOMB,
        PREVIOUS_TARGET,
        NEXT_TARGET
    }

    private enum MenuState {
        IDLE,
        ROOT,
        FIGHT,
        BAG,
        TARGET,
        RESOLVING
    }

    private final JLabel promptLabel = new JLabel("No battle running");
    private final JLabel statsLabel = new JLabel(" ");
    private final JLabel targetLabel = new JLabel("Target: none");
    private final JButton[] optionButtons = new JButton[4];
    private final Runnable[] optionActions = new Runnable[4];

    private Consumer<Command> commandListener = command -> {
    };
    private MenuState menuState = MenuState.IDLE;
    private PlayerCharacter currentPlayer;
    private boolean hasLivingEnemies;
    private Timer messageTimer;
    private String animatedMessage = "";
    private int visibleMessageCharacters;

    public BattleCommandPanel() {
        setLayout(new BorderLayout(10, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        setBackground(new Color(22, 28, 28));

        JPanel dialogue = new JPanel(new BorderLayout(4, 4));
        dialogue.setBackground(new Color(246, 248, 239));
        dialogue.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(28, 36, 36), 3),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        dialogue.setPreferredSize(new Dimension(470, 104));

        promptLabel.setForeground(new Color(27, 35, 35));
        promptLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        statsLabel.setForeground(new Color(58, 73, 71));
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        targetLabel.setForeground(new Color(147, 64, 58));
        targetLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        dialogue.add(promptLabel, BorderLayout.NORTH);
        dialogue.add(statsLabel, BorderLayout.CENTER);
        dialogue.add(targetLabel, BorderLayout.SOUTH);

        JPanel menu = new JPanel(new GridLayout(2, 2, 8, 8));
        menu.setOpaque(false);
        menu.setPreferredSize(new Dimension(430, 104));
        for (int i = 0; i < optionButtons.length; i++) {
            JButton button = createMenuButton();
            final int index = i;
            button.addActionListener(e -> pressOption(index));
            optionButtons[i] = button;
            menu.add(button);
        }

        add(dialogue, BorderLayout.CENTER);
        add(menu, BorderLayout.EAST);
        installHotkeys();
        setIdle("Start a battle to unlock actions.");
    }

    public void setCommandListener(Consumer<Command> commandListener) {
        this.commandListener = Objects.requireNonNull(commandListener, "commandListener");
    }

    public void setIdle(String message) {
        stopMessageAnimation();
        currentPlayer = null;
        hasLivingEnemies = false;
        menuState = MenuState.IDLE;
        promptLabel.setText(message);
        statsLabel.setText("Move with WASD or arrows once the arena starts.");
        targetLabel.setText("Target: none");
        configureButton(0, "FIGHT", false, null);
        configureButton(1, "BAG", false, null);
        configureButton(2, "DEFEND", false, null);
        configureButton(3, "TARGET", false, null);
    }

    public void showTurn(
        int roundNumber,
        PlayerCharacter player,
        List<Combatant> livingEnemies,
        String selectedTargetLabel
    ) {
        stopMessageAnimation();
        currentPlayer = Objects.requireNonNull(player, "player");
        hasLivingEnemies = !livingEnemies.isEmpty();
        menuState = MenuState.ROOT;
        updateTurnText(roundNumber, player);
        updateTarget(selectedTargetLabel);
        renderRootMenu();
    }

    public void updateTarget(String selectedTargetLabel) {
        targetLabel.setText("Target: " + selectedTargetLabel);
    }

    public void setResolving(String actionName) {
        stopMessageAnimation();
        currentPlayer = null;
        hasLivingEnemies = false;
        menuState = MenuState.RESOLVING;
        promptLabel.setText("Resolving " + actionName + "...");
        statsLabel.setText("Enemy turns and status effects are being processed.");
        configureButton(0, "WAIT", false, null);
        configureButton(1, "WAIT", false, null);
        configureButton(2, "WAIT", false, null);
        configureButton(3, "WAIT", false, null);
    }

    public void showBattleMessage(String message) {
        currentPlayer = null;
        hasLivingEnemies = false;
        menuState = MenuState.RESOLVING;
        statsLabel.setText(" ");
        configureButton(0, "WAIT", false, null);
        configureButton(1, "WAIT", false, null);
        configureButton(2, "WAIT", false, null);
        configureButton(3, "WAIT", false, null);
        animateMessage(message);
    }

    public void showBattleComplete() {
        stopMessageAnimation();
        currentPlayer = null;
        hasLivingEnemies = false;
        menuState = MenuState.IDLE;
        promptLabel.setText("Battle complete");
        statsLabel.setText("Replay the same setup or configure a new battle.");
        configureButton(0, "FIGHT", false, null);
        configureButton(1, "BAG", false, null);
        configureButton(2, "DEFEND", false, null);
        configureButton(3, "TARGET", false, null);
    }

    private void updateTurnText(int roundNumber, PlayerCharacter player) {
        promptLabel.setText("What will " + player.getName() + " do?");
        statsLabel.setText(
            "Round " + roundNumber
                + " | HP " + player.getCurrentHp() + "/" + player.getMaxHp()
                + " | ATK " + player.getAttack()
                + " | DEF " + player.getDefense()
                + " | SPD " + player.getSpeed()
                + " | Special CD " + player.getSpecialSkillCooldown()
        );
    }

    private void renderRootMenu() {
        if (!isTurnOpen()) {
            return;
        }
        menuState = MenuState.ROOT;
        configureButton(0, "1 FIGHT", hasLivingEnemies, this::renderFightMenu);
        configureButton(1, "2 BAG", true, this::renderBagMenu);
        configureButton(2, "3 DEFEND", true, () -> commandListener.accept(Command.DEFEND));
        configureButton(3, "4 TARGET", hasLivingEnemies, this::renderTargetMenu);
    }

    private void renderFightMenu() {
        if (!isTurnOpen()) {
            return;
        }
        menuState = MenuState.FIGHT;
        configureButton(0, "1 Basic Attack", hasLivingEnemies, () -> commandListener.accept(Command.BASIC_ATTACK));
        configureButton(
            1,
            "2 " + currentPlayer.getSpecialSkillName(),
            hasLivingEnemies && currentPlayer.canUseSpecialSkill(),
            () -> commandListener.accept(Command.SPECIAL_SKILL)
        );
        configureButton(2, "3 Back", true, this::renderRootMenu);
        configureButton(3, "4 Target", hasLivingEnemies, this::renderTargetMenu);
    }

    private void renderBagMenu() {
        if (!isTurnOpen()) {
            return;
        }
        menuState = MenuState.BAG;
        configureButton(
            0,
            "1 Potion x" + count(ItemType.POTION),
            count(ItemType.POTION) > 0,
            () -> commandListener.accept(Command.POTION)
        );
        configureButton(
            1,
            "2 Power Stone x" + count(ItemType.POWER_STONE),
            count(ItemType.POWER_STONE) > 0 && hasLivingEnemies,
            () -> commandListener.accept(Command.POWER_STONE)
        );
        configureButton(
            2,
            "3 Smoke Bomb x" + count(ItemType.SMOKE_BOMB),
            count(ItemType.SMOKE_BOMB) > 0,
            () -> commandListener.accept(Command.SMOKE_BOMB)
        );
        configureButton(3, "4 Back", true, this::renderRootMenu);
    }

    private void renderTargetMenu() {
        if (!isTurnOpen()) {
            return;
        }
        menuState = MenuState.TARGET;
        configureButton(0, "1 Prev Target", hasLivingEnemies, () -> commandListener.accept(Command.PREVIOUS_TARGET));
        configureButton(1, "2 Next Target", hasLivingEnemies, () -> commandListener.accept(Command.NEXT_TARGET));
        configureButton(2, "3 Back", true, this::renderRootMenu);
        configureButton(3, "4 Click Enemy", false, null);
    }

    private int count(ItemType itemType) {
        if (currentPlayer == null) {
            return 0;
        }
        return currentPlayer.getInventory().countOf(itemType);
    }

    private boolean isTurnOpen() {
        return currentPlayer != null
            && menuState != MenuState.IDLE
            && menuState != MenuState.RESOLVING;
    }

    private JButton createMenuButton() {
        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(BUTTON_FOREGROUND);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 53, 53), 2),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_HOVER_BACKGROUND);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                applyButtonStateColors(button);
            }
        });
        return button;
    }

    private void configureButton(int index, String text, boolean enabled, Runnable action) {
        JButton button = optionButtons[index];
        optionActions[index] = action;
        button.setText(text);
        button.setEnabled(enabled);
        applyButtonStateColors(button);
    }

    private void animateMessage(String message) {
        stopMessageAnimation();
        animatedMessage = Objects.requireNonNull(message, "message");
        visibleMessageCharacters = 0;
        setPromptHtml("");
        messageTimer = new Timer(18, e -> {
            visibleMessageCharacters = Math.min(animatedMessage.length(), visibleMessageCharacters + 2);
            setPromptHtml(animatedMessage.substring(0, visibleMessageCharacters));
            if (visibleMessageCharacters >= animatedMessage.length()) {
                stopMessageAnimation();
            }
        });
        messageTimer.setInitialDelay(0);
        messageTimer.start();
    }

    private void stopMessageAnimation() {
        if (messageTimer != null) {
            messageTimer.stop();
            messageTimer = null;
        }
    }

    private void setPromptHtml(String message) {
        promptLabel.setText("<html><body style='width:420px'>"
            + escapeHtml(message)
            + "</body></html>");
    }

    private static String escapeHtml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private void applyButtonStateColors(JButton button) {
        if (button.isEnabled()) {
            button.setBackground(BUTTON_BACKGROUND);
            button.setForeground(BUTTON_FOREGROUND);
        } else {
            button.setBackground(BUTTON_DISABLED_BACKGROUND);
            button.setForeground(BUTTON_DISABLED_FOREGROUND);
        }
    }

    private void pressOption(int index) {
        if (!optionButtons[index].isEnabled() || optionActions[index] == null) {
            return;
        }
        optionActions[index].run();
    }

    private void installHotkeys() {
        bindOptionKey(KeyEvent.VK_1, 0);
        bindOptionKey(KeyEvent.VK_2, 1);
        bindOptionKey(KeyEvent.VK_3, 2);
        bindOptionKey(KeyEvent.VK_4, 3);
        bindKey(KeyEvent.VK_Q, () -> cycleTarget(Command.PREVIOUS_TARGET));
        bindKey(KeyEvent.VK_E, () -> cycleTarget(Command.NEXT_TARGET));
        bindKey(KeyEvent.VK_ESCAPE, this::renderRootMenu);
    }

    private void cycleTarget(Command command) {
        if (isTurnOpen() && hasLivingEnemies) {
            commandListener.accept(command);
            if (menuState == MenuState.TARGET) {
                renderTargetMenu();
            }
        }
    }

    private void bindOptionKey(int keyCode, int optionIndex) {
        bindKey(keyCode, () -> pressOption(optionIndex));
    }

    private void bindKey(int keyCode, Runnable action) {
        String actionKey = "battle.command." + keyCode;
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), actionKey);
        getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
