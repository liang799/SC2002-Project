package sc2002.turnbased.ui.gui.view;

import java.awt.Color;
import java.awt.Graphics2D;

final class UnknownFighterBodyRenderer implements FighterBodyRenderer {
    @Override
    public void renderBody(Graphics2D g, FighterSpriteDto sprite) {
        g.setColor(new Color(83, 99, 98));
        g.fillOval(-24, -76, 48, 42);
        g.setColor(new Color(64, 78, 78));
        g.fillRoundRect(-20, -43, 40, 36, 14, 14);
        g.setColor(new Color(36, 43, 43));
        g.fillOval(-10, -62, 5, 5);
        g.fillOval(6, -62, 5, 5);
        g.drawArc(-9, -54, 18, 10, 200, 140);
        g.fillRoundRect(-15, -22, 9, 24, 5, 5);
        g.fillRoundRect(6, -22, 9, 24, 5, 5);
    }
}
