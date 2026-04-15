package sc2002.turnbased.ui.gui.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

/**
 * Paints the battle as a playable 2D arena while the engine remains the rules source of truth.
 */
public class ArenaScenePanel extends JPanel {
    private static final int PREFERRED_WIDTH = 940;
    private static final int PREFERRED_HEIGHT = 560;
    private static final long ACTION_ANIMATION_NANOS = 520_000_000L;
    private static final long FLOATING_TEXT_NANOS = 1_000_000_000L;
    private static final double PLAYER_SPEED = 210.0;

    private final Map<CombatantId, FighterSpriteDto> sprites = new LinkedHashMap<>();
    private final List<CombatantId> enemyOrder = new ArrayList<>();
    private final Set<String> pressedDirections = new HashSet<>();
    private final List<FloatingText> floatingTexts = new ArrayList<>();

    private CombatantId playerId;
    private CombatantId selectedEnemyId;
    private CombatantId actionActorId;
    private CombatantId actionTargetId;
    private long actionStartedAt;
    private long lastTickAt;
    private int roundNumber;
    private String banner = "Configure a battle, then enter the arena.";
    private String hint = "Move with WASD or arrows. Click enemies to target them.";
    private boolean acceptingPlayerTurn;
    private boolean battleActive;
    private Consumer<String> targetSelectionListener = targetLabel -> {
    };

    public ArenaScenePanel() {
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        setMinimumSize(new Dimension(720, 440));
        setFocusable(true);
        setBackground(new Color(20, 29, 33));
        installMovementBindings();
        installTargetSelection();

        Timer timer = new Timer(16, this::onTick);
        timer.start();
    }

    public void setTargetSelectionListener(Consumer<String> targetSelectionListener) {
        this.targetSelectionListener = Objects.requireNonNull(targetSelectionListener, "targetSelectionListener");
    }

    public void showSetupPreview() {
        pressedDirections.clear();
        sprites.clear();
        enemyOrder.clear();
        playerId = null;
        selectedEnemyId = null;
        actionActorId = null;
        actionTargetId = null;
        roundNumber = 0;
        acceptingPlayerTurn = false;
        battleActive = false;
        banner = "Configure a battle, then enter the arena.";
        hint = "WASD or arrows move your hero. Click enemies, then use the action bar.";
        repaint();
    }

    public void startBattle(BattleSetup setup) {
        Objects.requireNonNull(setup, "setup");
        pressedDirections.clear();
        sprites.clear();
        enemyOrder.clear();
        floatingTexts.clear();
        actionActorId = null;
        actionTargetId = null;
        roundNumber = 0;
        acceptingPlayerTurn = false;
        battleActive = true;

        FighterSpriteDto player = FighterSpriteDto.fromCombatant(setup.getPlayer(), true);
        player.x = getArenaWidth() * 0.18;
        player.y = floorBottom() - 42;
        sprites.put(player.id, player);
        playerId = player.id;

        for (Combatant enemy : setup.getInitialEnemies()) {
            ensureEnemySprite(enemy);
        }
        chooseDefaultTarget();
        layoutEnemies();
        banner = "Battle started. Position your hero and pick a target.";
        hint = "Movement is live during combat. The engine resolves attacks from your chosen command.";
        requestFocusInWindow();
        repaint();
    }

    public void showPlayerTurn(int currentRound, PlayerCharacter player, List<Combatant> livingEnemies) {
        roundNumber = currentRound;
        acceptingPlayerTurn = true;
        battleActive = true;
        updatePlayer(player);
        for (Combatant enemy : livingEnemies) {
            ensureEnemySprite(enemy);
        }
        chooseDefaultTarget();
        layoutEnemies();
        banner = "Round " + currentRound + ": choose your action.";
        hint = "Click an enemy to target. Use the 1-4 battle menu, Q/E to cycle targets, and Esc to go back.";
        requestFocusInWindow();
        repaint();
    }

    public void completePlayerTurn(String actionName) {
        acceptingPlayerTurn = false;
        banner = actionName + " queued.";
        hint = "Battle is resolving. You can keep moving while enemies take their turns.";
        repaint();
    }

    public CombatantId getSelectedEnemyId() {
        if (selectedEnemyId != null) {
            FighterSpriteDto selected = sprites.get(selectedEnemyId);
            if (selected != null && selected.alive && !selected.player) {
                return selectedEnemyId;
            }
        }
        chooseDefaultTarget();
        return selectedEnemyId;
    }

    public String getSelectedEnemyLabel() {
        CombatantId targetId = getSelectedEnemyId();
        FighterSpriteDto target = targetId == null ? null : sprites.get(targetId);
        if (target == null) {
            return "No target";
        }
        return target.name + " HP " + target.hp + "/" + target.maxHp;
    }

    public void selectNextEnemy(int direction) {
        List<CombatantId> living = livingEnemyIds();
        if (living.isEmpty()) {
            selectedEnemyId = null;
            repaint();
            return;
        }
        int index = living.indexOf(selectedEnemyId);
        if (index < 0) {
            index = 0;
        } else {
            index = Math.floorMod(index + direction, living.size());
        }
        selectedEnemyId = living.get(index);
        notifyTargetSelection();
        repaint();
    }

    public void applyBattleEvent(BattleEvent event) {
        Objects.requireNonNull(event, "event");
        long now = System.nanoTime();
        if (event instanceof sc2002.turnbased.report.ActionEvent actionEvent) {
            applyActionEvent(actionEvent, now);
        } else if (event instanceof RoundStartEvent roundStartEvent) {
            roundNumber = roundStartEvent.getRoundNumber();
            banner = "Round " + roundNumber;
        } else if (event instanceof RoundSummaryEvent roundSummaryEvent) {
            applyRoundSummary(roundSummaryEvent);
        } else if (event instanceof NarrationEvent narrationEvent) {
            banner = narrationEvent.getText();
            if (banner.startsWith("Victory") || banner.startsWith("Defeat")) {
                acceptingPlayerTurn = false;
                battleActive = false;
            }
        } else if (event instanceof SkippedTurnEvent skippedTurnEvent) {
            FighterSpriteDto skipped = sprites.get(skippedTurnEvent.getCombatantId());
            if (skipped != null) {
                skipped.pulseUntil = now + 520_000_000L;
            }
            banner = skippedTurnEvent.getCombatantName() + " cannot act: " + skippedTurnEvent.getReason();
        } else if (event instanceof StatusEffectReportEvent statusEvent && !statusEvent.statusEffectNotes().isEmpty()) {
            banner = String.join("  |  ", statusEvent.statusEffectNotes());
        }
        chooseDefaultTarget();
        layoutEnemies();
        repaint();
    }

    private void applyActionEvent(sc2002.turnbased.report.ActionEvent event, long now) {
        FighterSpriteDto target = sprites.get(event.getTargetId());
        FighterSpriteDto actor = sprites.get(event.getActorId());
        if (target != null) {
            target.hp = event.getHpAfter();
            target.alive = !event.isTargetEliminated();
            target.hurtUntil = now + 500_000_000L;
            String damageText = event.getDamage() > 0 ? "-" + event.getDamage() : "Blocked";
            floatingTexts.add(new FloatingText(damageText, target.x, target.y - 74, now, event.getDamage() > 0));
            if (event.isTargetEliminated()) {
                floatingTexts.add(new FloatingText("KO", target.x, target.y - 104, now, true));
            }
        }
        if (actor != null) {
            actor.pulseUntil = now + 520_000_000L;
        }
        actionActorId = event.getActorId();
        actionTargetId = event.getTargetId();
        actionStartedAt = now;
        banner = event.getActorName() + " -> " + event.getActionName() + " -> " + event.getTargetName();
    }

    private void applyRoundSummary(RoundSummaryEvent event) {
        roundNumber = event.getRoundNumber();
        updatePlayer(event.getPlayerSummary());
        for (CombatantSummary enemySummary : event.getEnemySummaries()) {
            ensureEnemySprite(enemySummary);
        }
        banner = "Round " + roundNumber + " summary";
    }

    private void updatePlayer(Combatant combatant) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            combatant.combatantId(),
            id -> FighterSpriteDto.fromCombatant(combatant, true)
        );
        double oldX = sprite.x == 0 ? getArenaWidth() * 0.18 : sprite.x;
        double oldY = sprite.y == 0 ? floorBottom() - 42 : sprite.y;
        sprite.updateFrom(combatant);
        sprite.x = oldX;
        sprite.y = oldY;
        sprite.player = true;
        playerId = sprite.id;
    }

    private void updatePlayer(CombatantSummary summary) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            summary.getCombatantId(),
            id -> FighterSpriteDto.fromSummary(summary, true)
        );
        double oldX = sprite.x == 0 ? getArenaWidth() * 0.18 : sprite.x;
        double oldY = sprite.y == 0 ? floorBottom() - 42 : sprite.y;
        sprite.updateFrom(summary);
        sprite.x = oldX;
        sprite.y = oldY;
        sprite.player = true;
        playerId = sprite.id;
    }

    private void ensureEnemySprite(Combatant combatant) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            combatant.combatantId(),
            id -> FighterSpriteDto.fromCombatant(combatant, false)
        );
        sprite.updateFrom(combatant);
        sprite.player = false;
        if (!enemyOrder.contains(sprite.id)) {
            enemyOrder.add(sprite.id);
        }
    }

    private void ensureEnemySprite(CombatantSummary summary) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            summary.getCombatantId(),
            id -> FighterSpriteDto.fromSummary(summary, false)
        );
        sprite.updateFrom(summary);
        sprite.player = false;
        if (!enemyOrder.contains(sprite.id)) {
            enemyOrder.add(sprite.id);
        }
    }

    private void chooseDefaultTarget() {
        if (selectedEnemyId != null) {
            FighterSpriteDto selected = sprites.get(selectedEnemyId);
            if (selected != null && selected.alive && !selected.player) {
                return;
            }
        }
        List<CombatantId> living = livingEnemyIds();
        CombatantId previousTarget = selectedEnemyId;
        selectedEnemyId = living.isEmpty() ? null : living.get(0);
        if (!Objects.equals(previousTarget, selectedEnemyId)) {
            notifyTargetSelection();
        }
    }

    private void notifyTargetSelection() {
        targetSelectionListener.accept(getSelectedEnemyLabel());
    }

    private List<CombatantId> livingEnemyIds() {
        List<CombatantId> living = new ArrayList<>();
        for (CombatantId id : enemyOrder) {
            FighterSpriteDto sprite = sprites.get(id);
            if (sprite != null && sprite.alive && !sprite.player) {
                living.add(id);
            }
        }
        return living;
    }

    private void layoutEnemies() {
        int livingSlot = 0;
        double width = getArenaWidth();
        double top = floorTop() + 38;
        double bottom = floorBottom() - 42;
        double rowGap = Math.max(62, Math.min(92, (bottom - top) / 3.0));
        for (CombatantId id : enemyOrder) {
            FighterSpriteDto sprite = sprites.get(id);
            if (sprite == null || !sprite.alive) {
                continue;
            }
            int column = livingSlot % 2;
            int row = livingSlot / 2;
            sprite.x = width - 138 - column * 126;
            sprite.y = Math.min(bottom, top + row * rowGap);
            livingSlot++;
        }
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
                pressedDirections.add(direction);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), "released." + keyCode + "." + direction);
        actionMap.put("released." + keyCode + "." + direction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressedDirections.remove(direction);
            }
        });
    }

    private void installTargetSelection() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                CombatantId clicked = enemyAt(e.getPoint());
                if (clicked != null) {
                    selectedEnemyId = clicked;
                    notifyTargetSelection();
                    repaint();
                }
                requestFocusInWindow();
            }
        });
    }

    private CombatantId enemyAt(Point point) {
        for (int i = enemyOrder.size() - 1; i >= 0; i--) {
            FighterSpriteDto sprite = sprites.get(enemyOrder.get(i));
            if (sprite != null && sprite.alive && sprite.bounds().contains(point)) {
                return sprite.id;
            }
        }
        return null;
    }

    private void onTick(ActionEvent event) {
        long now = System.nanoTime();
        if (lastTickAt == 0) {
            lastTickAt = now;
        }
        double deltaSeconds = Math.min(0.05, (now - lastTickAt) / 1_000_000_000.0);
        lastTickAt = now;
        movePlayer(deltaSeconds);
        updateActionAnimation(now);
        updateFloatingTexts(now);
        repaint();
    }

    private void movePlayer(double deltaSeconds) {
        FighterSpriteDto player = playerId == null ? null : sprites.get(playerId);
        if (player == null || !player.alive || !battleActive) {
            return;
        }
        player.x = clamp(player.x, 76, getArenaWidth() * 0.47);
        player.y = clamp(player.y, floorTop() + 58, floorBottom() - 34);
        double dx = 0;
        double dy = 0;
        if (pressedDirections.contains("left")) {
            dx -= 1;
        }
        if (pressedDirections.contains("right")) {
            dx += 1;
        }
        if (pressedDirections.contains("up")) {
            dy -= 1;
        }
        if (pressedDirections.contains("down")) {
            dy += 1;
        }
        if (dx == 0 && dy == 0) {
            player.walkPhase *= 0.88;
            return;
        }
        double length = Math.hypot(dx, dy);
        player.x = clamp(player.x + dx / length * PLAYER_SPEED * deltaSeconds, 76, getArenaWidth() * 0.47);
        player.y = clamp(player.y + dy / length * PLAYER_SPEED * deltaSeconds, floorTop() + 58, floorBottom() - 34);
        player.walkPhase += deltaSeconds * 10.0;
    }

    private void updateActionAnimation(long now) {
        for (FighterSpriteDto sprite : sprites.values()) {
            sprite.offsetX = 0;
            sprite.offsetY = 0;
        }
        if (actionActorId == null || actionTargetId == null) {
            return;
        }
        long elapsed = now - actionStartedAt;
        if (elapsed > ACTION_ANIMATION_NANOS) {
            actionActorId = null;
            actionTargetId = null;
            return;
        }
        FighterSpriteDto actor = sprites.get(actionActorId);
        FighterSpriteDto target = sprites.get(actionTargetId);
        if (actor == null || target == null) {
            return;
        }
        double progress = elapsed / (double) ACTION_ANIMATION_NANOS;
        double lunge = Math.sin(Math.PI * progress) * 34;
        double dx = Math.signum(target.x - actor.x);
        actor.offsetX = dx * lunge;
        actor.offsetY = -Math.sin(Math.PI * progress) * 8;
        if (progress > 0.35 && progress < 0.72) {
            target.offsetX = Math.sin(progress * 46) * 7;
            target.offsetY = Math.cos(progress * 32) * 3;
        }
    }

    private void updateFloatingTexts(long now) {
        Iterator<FloatingText> iterator = floatingTexts.iterator();
        while (iterator.hasNext()) {
            FloatingText text = iterator.next();
            if (now - text.createdAt > FLOATING_TEXT_NANOS) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        layoutEnemies();
        drawBackground(g);
        drawTargetPath(g);
        drawSprites(g);
        drawFloatingTexts(g, System.nanoTime());
        drawOverlay(g);
        g.dispose();
    }

    private void drawBackground(Graphics2D g) {
        int width = getWidth();
        int height = getHeight();
        g.setPaint(new GradientPaint(0, 0, new Color(34, 107, 124), 0, height, new Color(232, 86, 76)));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(255, 210, 99, 185));
        g.fillOval(width - 178, 48, 86, 86);

        drawMountain(g, -40, floorTop() - 160, 270, new Color(60, 102, 91));
        drawMountain(g, 190, floorTop() - 145, 270, new Color(55, 84, 93));
        drawMountain(g, 470, floorTop() - 152, 300, new Color(63, 105, 81));
        drawForest(g);
        drawRuins(g);

        int floorTop = floorTop();
        g.setPaint(new GradientPaint(0, floorTop, new Color(58, 69, 58), 0, height, new Color(34, 48, 45)));
        g.fillRect(0, floorTop, width, height - floorTop);

        g.setColor(new Color(88, 116, 91, 140));
        for (int x = -40; x < width + 90; x += 86) {
            g.fillRoundRect(x, floorTop + 22, 58, 18, 8, 8);
            g.fillRoundRect(x + 28, floorTop + 116, 74, 22, 8, 8);
        }
        g.setColor(new Color(18, 29, 31, 90));
        for (int y = floorTop + 30; y < height; y += 48) {
            g.drawLine(0, y, width, y - 26);
        }
    }

    private void drawMountain(Graphics2D g, int x, int baseY, int size, Color color) {
        Polygon mountain = new Polygon();
        mountain.addPoint(x, baseY + size);
        mountain.addPoint(x + size / 2, baseY);
        mountain.addPoint(x + size, baseY + size);
        g.setColor(color);
        g.fillPolygon(mountain);
        g.setColor(new Color(235, 238, 214, 88));
        Polygon cap = new Polygon();
        cap.addPoint(x + size / 2, baseY);
        cap.addPoint(x + size / 2 - 32, baseY + 70);
        cap.addPoint(x + size / 2 + 12, baseY + 48);
        cap.addPoint(x + size / 2 + 42, baseY + 86);
        g.fillPolygon(cap);
    }

    private void drawForest(Graphics2D g) {
        int horizon = floorTop() - 40;
        for (int x = -20; x < getWidth() + 60; x += 42) {
            int height = 54 + Math.floorMod(x * 13, 48);
            g.setColor(new Color(34, 78, 57, 180));
            Path2D tree = new Path2D.Double();
            tree.moveTo(x, horizon);
            tree.lineTo(x + 20, horizon - height);
            tree.lineTo(x + 42, horizon);
            tree.closePath();
            g.fill(tree);
            g.setColor(new Color(50, 63, 48, 190));
            g.fillRect(x + 18, horizon - 10, 8, 18);
        }
    }

    private void drawRuins(Graphics2D g) {
        int base = floorTop() - 26;
        g.setColor(new Color(74, 77, 72, 155));
        for (int x = 44; x < getWidth(); x += 245) {
            g.fillRect(x, base - 94, 26, 94);
            g.fillRect(x + 76, base - 118, 28, 118);
            g.fillRect(x - 10, base - 120, 128, 18);
            g.setColor(new Color(120, 58, 58, 150));
            g.fillRect(x + 24, base - 108, 50, 12);
            g.setColor(new Color(74, 77, 72, 155));
        }
    }

    private void drawTargetPath(Graphics2D g) {
        FighterSpriteDto player = playerId == null ? null : sprites.get(playerId);
        FighterSpriteDto target = selectedEnemyId == null ? null : sprites.get(selectedEnemyId);
        if (player == null || target == null || !target.alive) {
            return;
        }
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 8f, 9f }, 0));
        g.setColor(acceptingPlayerTurn ? new Color(255, 230, 120, 120) : new Color(210, 230, 220, 65));
        g.drawLine((int) player.drawX(), (int) (player.drawY() - 46), (int) target.drawX(), (int) (target.drawY() - 42));
        g.setStroke(new BasicStroke(1f));
    }

    private void drawSprites(Graphics2D g) {
        List<FighterSpriteDto> ordered = new ArrayList<>(sprites.values());
        ordered.sort((a, b) -> Double.compare(a.drawY(), b.drawY()));
        for (FighterSpriteDto sprite : ordered) {
            drawSprite(g, sprite);
        }
    }

    private void drawSprite(Graphics2D g, FighterSpriteDto sprite) {
        Graphics2D copy = (Graphics2D) g.create();
        double x = sprite.drawX();
        double y = sprite.drawY();
        copy.translate(x, y);
        if (!sprite.alive) {
            copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.52f));
            copy.rotate(sprite.player ? -0.25 : 0.32);
            copy.translate(0, 24);
        }

        copy.setColor(new Color(0, 0, 0, sprite.alive ? 72 : 42));
        copy.fill(new Ellipse2D.Double(-36, -8, 72, 18));

        if (sprite.id.equals(selectedEnemyId) && sprite.alive) {
            copy.setStroke(new BasicStroke(3f));
            copy.setColor(new Color(255, 229, 87, 210));
            copy.draw(new Ellipse2D.Double(-44, -15, 88, 28));
            copy.setColor(new Color(255, 84, 74, 130));
            copy.draw(new Ellipse2D.Double(-52, -21, 104, 40));
        }

        if (sprite.player) {
            drawPlayerSprite(copy, sprite);
        } else if (sprite.name.toLowerCase().contains("wolf")) {
            drawWolfSprite(copy, sprite);
        } else {
            drawGoblinSprite(copy, sprite);
        }

        copy.dispose();
        drawHealthBar(g, sprite, (int) x, (int) y);
    }

    private void drawPlayerSprite(Graphics2D g, FighterSpriteDto sprite) {
        boolean wizard = sprite.name.toLowerCase().contains("wizard");
        double bob = Math.sin(sprite.walkPhase) * 3.0;
        if (wizard) {
            g.setColor(new Color(54, 54, 76));
            g.fillRoundRect(-19, (int) (-68 + bob), 38, 62, 18, 18);
            g.setColor(new Color(212, 66, 78));
            g.fillRoundRect(-14, (int) (-56 + bob), 28, 46, 14, 14);
            g.setColor(new Color(246, 218, 174));
            g.fillOval(-13, (int) (-88 + bob), 26, 26);
            g.setColor(new Color(47, 43, 65));
            g.fillArc(-18, (int) (-96 + bob), 36, 35, 0, 180);
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(104, 68, 45));
            g.drawLine(22, (int) (-75 + bob), 35, (int) (-13 + bob));
            g.setColor(new Color(105, 231, 214, 220));
            g.fillOval(27, (int) (-88 + bob), 15, 15);
        } else {
            g.setColor(new Color(74, 83, 92));
            g.fillRoundRect(-18, (int) (-65 + bob), 36, 50, 12, 12);
            g.setColor(new Color(184, 199, 196));
            g.fillRoundRect(-13, (int) (-62 + bob), 26, 42, 10, 10);
            g.setColor(new Color(241, 207, 164));
            g.fillOval(-13, (int) (-89 + bob), 26, 26);
            g.setColor(new Color(69, 75, 80));
            g.fillArc(-16, (int) (-96 + bob), 32, 31, 0, 180);
            g.setColor(new Color(67, 112, 126));
            g.fillOval(-37, (int) (-58 + bob), 25, 35);
            g.setColor(new Color(222, 227, 213));
            g.drawOval(-37, (int) (-58 + bob), 25, 35);
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(218, 226, 218));
            g.drawLine(22, (int) (-66 + bob), 39, (int) (-103 + bob));
            g.setColor(new Color(105, 68, 42));
            g.drawLine(20, (int) (-65 + bob), 26, (int) (-43 + bob));
        }
        g.setColor(new Color(35, 35, 34));
        g.fillRoundRect(-17, -20, 11, 22, 6, 6);
        g.fillRoundRect(6, -20, 11, 22, 6, 6);
        drawPulse(g, sprite);
    }

    private void drawGoblinSprite(Graphics2D g, FighterSpriteDto sprite) {
        g.setColor(new Color(56, 137, 80));
        g.fillOval(-21, -76, 42, 38);
        Path2D leftEar = new Path2D.Double();
        leftEar.moveTo(-18, -63);
        leftEar.lineTo(-42, -73);
        leftEar.lineTo(-22, -51);
        leftEar.closePath();
        Path2D rightEar = new Path2D.Double();
        rightEar.moveTo(18, -63);
        rightEar.lineTo(42, -73);
        rightEar.lineTo(22, -51);
        rightEar.closePath();
        g.fill(leftEar);
        g.fill(rightEar);
        g.setColor(new Color(72, 98, 61));
        g.fillRoundRect(-17, -42, 34, 35, 12, 12);
        g.setColor(new Color(125, 68, 49));
        g.fillRect(-18, -28, 36, 9);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(92, 58, 36));
        g.drawLine(25, -42, 38, -88);
        g.setColor(new Color(42, 53, 42));
        g.fillOval(-9, -64, 5, 5);
        g.fillOval(6, -64, 5, 5);
        g.setColor(new Color(38, 52, 39));
        g.drawArc(-8, -57, 16, 10, 200, 140);
        drawPulse(g, sprite);
    }

    private void drawWolfSprite(Graphics2D g, FighterSpriteDto sprite) {
        g.setColor(new Color(82, 91, 94));
        g.fillRoundRect(-34, -55, 62, 32, 24, 24);
        g.setColor(new Color(116, 130, 128));
        g.fillOval(13, -72, 38, 34);
        Path2D snout = new Path2D.Double();
        snout.moveTo(42, -57);
        snout.lineTo(66, -50);
        snout.lineTo(42, -43);
        snout.closePath();
        g.fill(snout);
        g.setColor(new Color(68, 75, 78));
        Path2D ear = new Path2D.Double();
        ear.moveTo(22, -69);
        ear.lineTo(28, -94);
        ear.lineTo(41, -68);
        ear.closePath();
        g.fill(ear);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(-30, -48, -54, -70);
        g.setColor(new Color(37, 43, 45));
        g.fillOval(38, -60, 5, 5);
        g.fillOval(58, -52, 6, 5);
        g.setColor(new Color(61, 67, 70));
        g.fillRoundRect(-24, -29, 8, 27, 5, 5);
        g.fillRoundRect(13, -29, 8, 27, 5, 5);
        drawPulse(g, sprite);
    }

    private void drawPulse(Graphics2D g, FighterSpriteDto sprite) {
        long now = System.nanoTime();
        if (sprite.pulseUntil <= now && sprite.hurtUntil <= now) {
            return;
        }
        float alpha = sprite.hurtUntil > now ? 0.28f : 0.18f;
        Color color = sprite.hurtUntil > now ? new Color(255, 70, 61) : new Color(108, 232, 215);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.fillOval(-48, -105, 96, 102);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawHealthBar(Graphics2D g, FighterSpriteDto sprite, int centerX, int baseY) {
        int barWidth = sprite.player ? 118 : 96;
        int x = centerX - barWidth / 2;
        int y = baseY - (sprite.player ? 126 : 112);
        double ratio = sprite.maxHp == 0 ? 0 : Math.max(0, sprite.hp / (double) sprite.maxHp);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        FontMetrics metrics = g.getFontMetrics();
        String label = sprite.name;
        int labelX = centerX - metrics.stringWidth(label) / 2;
        g.setColor(new Color(14, 18, 20, 175));
        g.fillRoundRect(labelX - 6, y - 18, metrics.stringWidth(label) + 12, 17, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString(label, labelX, y - 5);

        g.setColor(new Color(17, 22, 22, 205));
        g.fillRoundRect(x, y, barWidth, 12, 7, 7);
        Color fill = ratio > 0.55 ? new Color(91, 212, 114) : ratio > 0.25 ? new Color(239, 192, 72) : new Color(225, 70, 66);
        g.setColor(fill);
        g.fillRoundRect(x + 2, y + 2, Math.max(0, (int) ((barWidth - 4) * ratio)), 8, 6, 6);
        g.setColor(new Color(236, 241, 226, 160));
        g.drawRoundRect(x, y, barWidth, 12, 7, 7);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        String hp = sprite.hp + "/" + sprite.maxHp;
        g.setColor(new Color(240, 244, 233));
        g.drawString(hp, centerX - g.getFontMetrics().stringWidth(hp) / 2, y + 10);

        if (!sprite.statuses.isEmpty()) {
            String statuses = String.join(", ", sprite.statuses);
            int statusY = y + 27;
            g.setColor(new Color(20, 27, 26, 168));
            int textWidth = g.getFontMetrics().stringWidth(statuses);
            g.fillRoundRect(centerX - textWidth / 2 - 5, statusY - 12, textWidth + 10, 16, 8, 8);
            g.setColor(new Color(176, 235, 217));
            g.drawString(statuses, centerX - textWidth / 2, statusY);
        }
    }

    private void drawFloatingTexts(Graphics2D g, long now) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        for (FloatingText text : floatingTexts) {
            double progress = (now - text.createdAt) / (double) FLOATING_TEXT_NANOS;
            float alpha = (float) Math.max(0, 1.0 - progress);
            int y = (int) (text.y - progress * 46);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(text.damage ? new Color(255, 87, 76) : new Color(155, 235, 222));
            g.drawString(text.text, (int) text.x - g.getFontMetrics().stringWidth(text.text) / 2, y);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawOverlay(Graphics2D g) {
        int width = getWidth();
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.setColor(new Color(12, 18, 19, 172));
        g.fillRoundRect(18, 18, Math.min(width - 36, 640), 72, 8, 8);
        g.setColor(new Color(244, 248, 233));
        String round = roundNumber > 0 ? "Round " + roundNumber + "  |  " : "";
        g.drawString(fitText(g, round + banner, Math.min(width - 84, 580)), 34, 48);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(new Color(205, 232, 219));
        g.drawString(fitText(g, hint, Math.min(width - 84, 580)), 34, 74);

        if (acceptingPlayerTurn) {
            g.setColor(new Color(255, 220, 87, 210));
            g.fillRoundRect(width - 202, 22, 178, 34, 8, 8);
            g.setColor(new Color(26, 32, 31));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            g.drawString("Your move", width - 136, 44);
        }
    }

    private int floorTop() {
        return (int) (getArenaHeight() * 0.55);
    }

    private int floorBottom() {
        return getArenaHeight() - 36;
    }

    private int getArenaWidth() {
        return getWidth() > 0 ? getWidth() : PREFERRED_WIDTH;
    }

    private int getArenaHeight() {
        return getHeight() > 0 ? getHeight() : PREFERRED_HEIGHT;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String fitText(Graphics2D g, String text, int maxWidth) {
        if (g.getFontMetrics().stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int end = text.length();
        while (end > 0 && g.getFontMetrics().stringWidth(text.substring(0, end) + ellipsis) > maxWidth) {
            end--;
        }
        return text.substring(0, end) + ellipsis;
    }

    private record FloatingText(String text, double x, double y, long createdAt, boolean damage) {
    }
}
