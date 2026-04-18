package sc2002.turnbased.ui.gui.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;

final class ArenaSceneRenderer {
    private static final long FLOATING_TEXT_NANOS = 1_000_000_000L;

    private final ArenaBackgroundRenderer backgroundRenderer;
    private final FighterSpriteRenderer fighterRenderer = new FighterSpriteRenderer();

    ArenaSceneRenderer() {
        this(new ArenaBackgroundRenderer());
    }

    ArenaSceneRenderer(ArenaBackgroundRenderer backgroundRenderer) {
        this.backgroundRenderer = Objects.requireNonNull(backgroundRenderer, "backgroundRenderer");
    }

    void render(Graphics2D g, ArenaSceneModel model, int width, int height, long now) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        backgroundRenderer.render(g, width, height);
        drawTargetPath(g, model);
        drawSprites(g, model);
        drawFloatingTexts(g, model, now);
        drawOverlay(g, model, width);
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
        Font originalFont = g.getFont();
        Composite originalComposite = g.getComposite();
        try {
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            for (ArenaSceneModel.FloatingText text : model.floatingTexts()) {
                double progress = (now - text.createdAt()) / (double) FLOATING_TEXT_NANOS;
                float alpha = (float) Math.max(0, 1.0 - progress);
                int y = (int) (text.y() - progress * 46);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.setColor(text.damage() ? new Color(255, 87, 76) : new Color(155, 235, 222));
                g.drawString(text.text(), (int) text.x() - g.getFontMetrics().stringWidth(text.text()) / 2, y);
            }
        } finally {
            g.setFont(originalFont);
            g.setComposite(originalComposite);
        }
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
