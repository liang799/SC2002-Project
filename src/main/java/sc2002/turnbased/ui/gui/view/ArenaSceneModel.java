package sc2002.turnbased.ui.gui.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

final class ArenaSceneModel {
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

    void setTargetSelectionListener(Consumer<String> targetSelectionListener) {
        this.targetSelectionListener = Objects.requireNonNull(targetSelectionListener, "targetSelectionListener");
    }

    void showSetupPreview() {
        clearSceneState();
        banner = "Configure a battle, then enter the arena.";
        hint = "WASD or arrows move your hero. Click enemies, then use the action bar.";
    }

    void startBattle(BattleSetup setup, int arenaWidth, int arenaHeight) {
        Objects.requireNonNull(setup, "setup");
        clearSceneState();
        battleActive = true;

        FighterSpriteDto player = FighterSpriteDto.fromCombatant(setup.getPlayer(), true);
        player.x = arenaWidth * 0.18;
        player.y = floorBottom(arenaHeight) - 42;
        sprites.put(player.id, player);
        playerId = player.id;

        for (Combatant enemy : setup.getInitialEnemies()) {
            ensureEnemySprite(enemy);
        }
        chooseDefaultTarget();
        layoutEnemies(arenaWidth, arenaHeight);
        banner = "Battle started. Position your hero and pick a target.";
        hint = "Movement is live during combat. The engine resolves attacks from your chosen command.";
    }

    void showPlayerTurn(int currentRound, PlayerCharacter player, List<Combatant> livingEnemies, int arenaWidth, int arenaHeight) {
        roundNumber = currentRound;
        acceptingPlayerTurn = true;
        battleActive = true;
        updatePlayer(player, arenaWidth, arenaHeight);
        for (Combatant enemy : livingEnemies) {
            ensureEnemySprite(enemy);
        }
        chooseDefaultTarget();
        layoutEnemies(arenaWidth, arenaHeight);
        banner = "Round " + currentRound + ": choose your action.";
        hint = "Click an enemy to target. Use the 1-4 battle menu, Q/E to cycle targets, and Esc to go back.";
    }

    void completePlayerTurn(String actionName) {
        acceptingPlayerTurn = false;
        banner = Objects.requireNonNull(actionName, "actionName") + " queued.";
        hint = "Battle is resolving. You can keep moving while enemies take their turns.";
    }

    CombatantId selectedEnemyId() {
        if (selectedEnemyId != null) {
            FighterSpriteDto selected = sprites.get(selectedEnemyId);
            if (selected != null && selected.alive && !selected.player) {
                return selectedEnemyId;
            }
        }
        chooseDefaultTarget();
        return selectedEnemyId;
    }

    String selectedEnemyLabel() {
        CombatantId targetId = selectedEnemyId();
        FighterSpriteDto target = targetId == null ? null : sprites.get(targetId);
        if (target == null) {
            return "No target";
        }
        return target.name + " HP " + target.hp + "/" + target.maxHp;
    }

    void selectNextEnemy(int direction) {
        List<CombatantId> living = livingEnemyIds();
        if (living.isEmpty()) {
            selectedEnemyId = null;
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
    }

    boolean selectEnemyAt(Point point) {
        CombatantId clicked = enemyAt(Objects.requireNonNull(point, "point"));
        if (clicked == null) {
            return false;
        }
        selectedEnemyId = clicked;
        notifyTargetSelection();
        return true;
    }

    void setDirectionPressed(String direction, boolean pressed) {
        Objects.requireNonNull(direction, "direction");
        if (pressed) {
            pressedDirections.add(direction);
        } else {
            pressedDirections.remove(direction);
        }
    }

    void applyBattleEvent(BattleEvent event, int arenaWidth, int arenaHeight) {
        applyBattleEvent(event, arenaWidth, arenaHeight, System.nanoTime());
    }

    void applyBattleEvent(BattleEvent event, int arenaWidth, int arenaHeight, long now) {
        Objects.requireNonNull(event, "event");
        if (event instanceof ActionEvent actionEvent) {
            applyActionEvent(actionEvent, now);
        } else if (event instanceof RoundStartEvent roundStartEvent) {
            roundNumber = roundStartEvent.getRoundNumber();
            banner = "Round " + roundNumber;
        } else if (event instanceof RoundSummaryEvent roundSummaryEvent) {
            applyRoundSummary(roundSummaryEvent, arenaWidth, arenaHeight);
        } else if (event instanceof NarrationEvent narrationEvent) {
            banner = narrationEvent.getText();
            if (banner.startsWith("Victory") || banner.startsWith("Defeat")) {
                acceptingPlayerTurn = false;
                battleActive = false;
            }
        } else if (event instanceof SkippedTurnEvent skippedTurnEvent) {
            FighterSpriteDto skipped = sprites.get(skippedTurnEvent.getCombatantId());
            if (skipped != null) {
                skipped.pulseUntil = now + ACTION_ANIMATION_NANOS;
            }
            banner = skippedTurnEvent.getCombatantName() + " cannot act: " + skippedTurnEvent.getReason();
        } else if (event instanceof StatusEffectReportEvent statusEvent && !statusEvent.statusEffectNotes().isEmpty()) {
            banner = String.join("  |  ", statusEvent.statusEffectNotes());
        }
        chooseDefaultTarget();
        layoutEnemies(arenaWidth, arenaHeight);
    }

    void relayout(int arenaWidth, int arenaHeight) {
        layoutEnemies(arenaWidth, arenaHeight);
    }

    void tick(int arenaWidth, int arenaHeight) {
        tick(System.nanoTime(), arenaWidth, arenaHeight);
    }

    void tick(long now, int arenaWidth, int arenaHeight) {
        if (lastTickAt == 0) {
            lastTickAt = now;
        }
        double deltaSeconds = Math.min(0.05, (now - lastTickAt) / 1_000_000_000.0);
        lastTickAt = now;
        movePlayer(deltaSeconds, arenaWidth, arenaHeight);
        updateActionAnimation(now);
        updateFloatingTexts(now);
    }

    List<FighterSpriteDto> spritesByDrawOrder() {
        List<FighterSpriteDto> ordered = new ArrayList<>(sprites.values());
        ordered.sort(Comparator.comparingDouble(FighterSpriteDto::drawY));
        return ordered;
    }

    List<FloatingText> floatingTexts() {
        return List.copyOf(floatingTexts);
    }

    FighterSpriteDto playerSprite() {
        return playerId == null ? null : sprites.get(playerId);
    }

    CombatantId currentSelectedEnemyId() {
        return selectedEnemyId;
    }

    FighterSpriteDto currentSelectedEnemySprite() {
        return selectedEnemyId == null ? null : sprites.get(selectedEnemyId);
    }

    int roundNumber() {
        return roundNumber;
    }

    String banner() {
        return banner;
    }

    String hint() {
        return hint;
    }

    boolean acceptingPlayerTurn() {
        return acceptingPlayerTurn;
    }

    boolean battleActive() {
        return battleActive;
    }

    private void clearSceneState() {
        pressedDirections.clear();
        sprites.clear();
        enemyOrder.clear();
        floatingTexts.clear();
        playerId = null;
        selectedEnemyId = null;
        actionActorId = null;
        actionTargetId = null;
        actionStartedAt = 0;
        lastTickAt = 0;
        roundNumber = 0;
        acceptingPlayerTurn = false;
        battleActive = false;
    }

    private void applyActionEvent(ActionEvent event, long now) {
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
            actor.pulseUntil = now + ACTION_ANIMATION_NANOS;
        }
        actionActorId = event.getActorId();
        actionTargetId = event.getTargetId();
        actionStartedAt = now;
        banner = event.getActorName() + " -> " + event.getActionName() + " -> " + event.getTargetName();
    }

    private void applyRoundSummary(RoundSummaryEvent event, int arenaWidth, int arenaHeight) {
        roundNumber = event.getRoundNumber();
        updatePlayer(event.getPlayerSummary(), arenaWidth, arenaHeight);
        for (CombatantSummary enemySummary : event.getEnemySummaries()) {
            ensureEnemySprite(enemySummary);
        }
        banner = "Round " + roundNumber + " summary";
    }

    private void updatePlayer(Combatant combatant, int arenaWidth, int arenaHeight) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            combatant.combatantId(),
            id -> FighterSpriteDto.fromCombatant(combatant, true)
        );
        double oldX = Double.isNaN(sprite.x) ? arenaWidth * 0.18 : sprite.x;
        double oldY = Double.isNaN(sprite.y) ? floorBottom(arenaHeight) - 42 : sprite.y;
        sprite.updateFrom(combatant);
        sprite.x = oldX;
        sprite.y = oldY;
        sprite.setPlayer(true);
        playerId = sprite.id;
    }

    private void updatePlayer(CombatantSummary summary, int arenaWidth, int arenaHeight) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            summary.getCombatantId(),
            id -> FighterSpriteDto.fromSummary(summary, true)
        );
        double oldX = Double.isNaN(sprite.x) ? arenaWidth * 0.18 : sprite.x;
        double oldY = Double.isNaN(sprite.y) ? floorBottom(arenaHeight) - 42 : sprite.y;
        sprite.updateFrom(summary);
        sprite.x = oldX;
        sprite.y = oldY;
        sprite.setPlayer(true);
        playerId = sprite.id;
    }

    private void ensureEnemySprite(Combatant combatant) {
        FighterSpriteDto sprite = sprites.computeIfAbsent(
            combatant.combatantId(),
            id -> FighterSpriteDto.fromCombatant(combatant, false)
        );
        sprite.updateFrom(combatant);
        sprite.setPlayer(false);
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
        sprite.setPlayer(false);
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
        targetSelectionListener.accept(selectedEnemyLabel());
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

    private void layoutEnemies(int arenaWidth, int arenaHeight) {
        int livingSlot = 0;
        double top = floorTop(arenaHeight) + 38;
        double bottom = floorBottom(arenaHeight) - 42;
        double rowGap = Math.max(62, Math.min(92, (bottom - top) / 3.0));
        for (CombatantId id : enemyOrder) {
            FighterSpriteDto sprite = sprites.get(id);
            if (sprite == null || !sprite.alive) {
                continue;
            }
            int column = livingSlot % 2;
            int row = livingSlot / 2;
            sprite.x = arenaWidth - 138 - column * 126;
            sprite.y = Math.min(bottom, top + row * rowGap);
            livingSlot++;
        }
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

    private void movePlayer(double deltaSeconds, int arenaWidth, int arenaHeight) {
        FighterSpriteDto player = playerSprite();
        if (player == null || !player.alive || !battleActive) {
            return;
        }
        player.x = clamp(player.x, 76, arenaWidth * 0.47);
        player.y = clamp(player.y, floorTop(arenaHeight) + 58, floorBottom(arenaHeight) - 34);
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
        player.x = clamp(player.x + dx / length * PLAYER_SPEED * deltaSeconds, 76, arenaWidth * 0.47);
        player.y = clamp(player.y + dy / length * PLAYER_SPEED * deltaSeconds, floorTop(arenaHeight) + 58, floorBottom(arenaHeight) - 34);
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

    private static int floorTop(int arenaHeight) {
        return (int) (arenaHeight * 0.55);
    }

    private static int floorBottom(int arenaHeight) {
        return arenaHeight - 36;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    record FloatingText(String text, double x, double y, long createdAt, boolean damage) {
    }
}
