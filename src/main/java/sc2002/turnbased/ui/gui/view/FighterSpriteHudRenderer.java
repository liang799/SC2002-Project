package sc2002.turnbased.ui.gui.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

final class FighterSpriteHudRenderer {
    void render(Graphics2D g, FighterSpriteDto sprite, int centerX, int baseY) {
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
}
