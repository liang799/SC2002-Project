package sc2002.turnbased.ui.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.report.BattleEvent;

/**
 * Swing shell for the playable arena. Scene state, ticking, and rendering live in collaborators.
 */
public class ArenaScenePanel extends JPanel {
    private static final int PREFERRED_WIDTH = 940;
    private static final int PREFERRED_HEIGHT = 560;

    private final ArenaSceneModel model = new ArenaSceneModel();
    private final ArenaSceneRenderer renderer = new ArenaSceneRenderer();
    private final ArenaSceneLoop sceneLoop = new ArenaSceneLoop(this::onTick);

    public ArenaScenePanel() {
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        setMinimumSize(new Dimension(720, 440));
        setFocusable(true);
        setBackground(new Color(20, 29, 33));
        installMovementBindings();
        installTargetSelection();
        installResizeLayout();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        sceneLoop.start();
    }

    @Override
    public void removeNotify() {
        sceneLoop.stop();
        super.removeNotify();
    }

    public void setTargetSelectionListener(Consumer<String> targetSelectionListener) {
        model.setTargetSelectionListener(targetSelectionListener);
    }

    public void showSetupPreview() {
        model.showSetupPreview();
        repaint();
    }

    public void startBattle(BattleSetup setup) {
        model.startBattle(Objects.requireNonNull(setup, "setup"), arenaWidth(), arenaHeight());
        requestFocusInWindow();
        repaint();
    }

    public void showPlayerTurn(int currentRound, PlayerCharacter player, List<Combatant> livingEnemies) {
        model.showPlayerTurn(currentRound, player, livingEnemies, arenaWidth(), arenaHeight());
        requestFocusInWindow();
        repaint();
    }

    public void completePlayerTurn(String actionName) {
        model.completePlayerTurn(actionName);
        repaint();
    }

    public CombatantId getSelectedEnemyId() {
        return model.selectedEnemyId();
    }

    public String getSelectedEnemyLabel() {
        return model.selectedEnemyLabel();
    }

    public void selectNextEnemy(int direction) {
        model.selectNextEnemy(direction);
        repaint();
    }

    public void applyBattleEvent(BattleEvent event) {
        model.applyBattleEvent(event, arenaWidth(), arenaHeight());
        repaint();
    }

    private void installMovementBindings() {
        bindDirection(KeyEvent.VK_W, "up");
        bindDirection(KeyEvent.VK_UP, "up");
        bindDirection(KeyEvent.VK_S, "down");
        bindDirection(KeyEvent.VK_DOWN, "down");
        bindDirection(KeyEvent.VK_A, "left");
        bindDirection(KeyEvent.VK_LEFT, "left");
        bindDirection(KeyEvent.VK_D, "right");
        bindDirection(KeyEvent.VK_RIGHT, "right");
    }

    private void bindDirection(int keyCode, String direction) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), "pressed." + keyCode + "." + direction);
        actionMap.put("pressed." + keyCode + "." + direction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setDirectionPressed(direction, true);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), "released." + keyCode + "." + direction);
        actionMap.put("released." + keyCode + "." + direction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setDirectionPressed(direction, false);
            }
        });
    }

    private void installTargetSelection() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (model.selectEnemyAt(e.getPoint())) {
                    repaint();
                }
                requestFocusInWindow();
            }
        });
    }

    private void installResizeLayout() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                model.relayout(arenaWidth(), arenaHeight());
                repaint();
            }
        });
    }

    private void onTick() {
        model.tick(arenaWidth(), arenaHeight());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        renderer.render(g, model, arenaWidth(), arenaHeight());
        g.dispose();
    }

    private int arenaWidth() {
        return getWidth() > 0 ? getWidth() : PREFERRED_WIDTH;
    }

    private int arenaHeight() {
        return getHeight() > 0 ? getHeight() : PREFERRED_HEIGHT;
    }
}
