package sc2002.turnbased.ui.gui.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

final class ArenaSceneRenderer {
    private static final long FLOATING_TEXT_NANOS = 1_000_000_000L;

    private final FighterSpriteRenderer fighterRenderer = new FighterSpriteRenderer();

    void render(Graphics2D g, ArenaSceneModel model, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawBackground(g, width, height);
        drawTargetPath(g, model);
        drawSprites(g, model);
        drawFloatingTexts(g, model, System.nanoTime());
        drawOverlay(g, model, width);
    }

    private void drawBackground(Graphics2D g, int width, int height) {
        int floorTop = floorTop(height);
        g.setPaint(new GradientPaint(0, 0, new Color(34, 107, 124), 0, height, new Color(232, 86, 76)));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(255, 210, 99, 185));
        g.fillOval(width - 178, 48, 86, 86);

        drawMountain(g, -40, floorTop - 160, 270, new Color(60, 102, 91));
        drawMountain(g, 190, floorTop - 145, 270, new Color(55, 84, 93));
        drawMountain(g, 470, floorTop - 152, 300, new Color(63, 105, 81));
        drawForest(g, width, floorTop);
        drawRuins(g, width, floorTop);

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

    private void drawForest(Graphics2D g, int width, int floorTop) {
        int horizon = floorTop - 40;
        for (int x = -20; x < width + 60; x += 42) {
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

    private void drawRuins(Graphics2D g, int width, int floorTop) {
        int base = floorTop - 26;
        g.setColor(new Color(74, 77, 72, 155));
        for (int x = 44; x < width; x += 245) {
            g.fillRect(x, base - 94, 26, 94);
            g.fillRect(x + 76, base - 118, 28, 118);
            g.fillRect(x - 10, base - 120, 128, 18);
            g.setColor(new Color(120, 58, 58, 150));
            g.fillRect(x + 24, base - 108, 50, 12);
            g.setColor(new Color(74, 77, 72, 155));
        }
    }

    private void drawTargetPath(Graphics2D g, ArenaSceneModel model) {
        FighterSpriteDto player = model.playerSprite();
        FighterSpriteDto target = model.currentSelectedEnemySprite();
        if (player == null || target == null || !target.alive) {
            return;
        }
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 8f, 9f }, 0));
        g.setColor(model.acceptingPlayerTurn() ? new Color(255, 230, 120, 120) : new Color(210, 230, 220, 65));
        g.drawLine((int) player.drawX(), (int) (player.drawY() - 46), (int) target.drawX(), (int) (target.drawY() - 42));
        g.setStroke(new BasicStroke(1f));
    }

    private void drawSprites(Graphics2D g, ArenaSceneModel model) {
        for (FighterSpriteDto sprite : model.spritesByDrawOrder()) {
            fighterRenderer.render(g, sprite, model.currentSelectedEnemyId());
        }
    }

    private void drawFloatingTexts(Graphics2D g, ArenaSceneModel model, long now) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        for (ArenaSceneModel.FloatingText text : model.floatingTexts()) {
            double progress = (now - text.createdAt()) / (double) FLOATING_TEXT_NANOS;
            float alpha = (float) Math.max(0, 1.0 - progress);
            int y = (int) (text.y() - progress * 46);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(text.damage() ? new Color(255, 87, 76) : new Color(155, 235, 222));
            g.drawString(text.text(), (int) text.x() - g.getFontMetrics().stringWidth(text.text()) / 2, y);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawOverlay(Graphics2D g, ArenaSceneModel model, int width) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.setColor(new Color(12, 18, 19, 172));
        g.fillRoundRect(18, 18, Math.min(width - 36, 640), 72, 8, 8);
        g.setColor(new Color(244, 248, 233));
        String round = model.roundNumber() > 0 ? "Round " + model.roundNumber() + "  |  " : "";
        g.drawString(fitText(g, round + model.banner(), Math.min(width - 84, 580)), 34, 48);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(new Color(205, 232, 219));
        g.drawString(fitText(g, model.hint(), Math.min(width - 84, 580)), 34, 74);

        if (model.acceptingPlayerTurn()) {
            g.setColor(new Color(255, 220, 87, 210));
            g.fillRoundRect(width - 202, 22, 178, 34, 8, 8);
            g.setColor(new Color(26, 32, 31));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            g.drawString("Your move", width - 136, 44);
        }
    }

    private static int floorTop(int height) {
        return (int) (height * 0.55);
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
}
