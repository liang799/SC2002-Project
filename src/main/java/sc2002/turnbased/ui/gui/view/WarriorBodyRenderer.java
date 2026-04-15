package sc2002.turnbased.ui.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

final class WarriorBodyRenderer implements FighterBodyRenderer {
    @Override
    public void renderBody(Graphics2D g, FighterSpriteDto sprite) {
        double bob = Math.sin(sprite.walkPhase) * 3.0;
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
        g.setColor(new Color(35, 35, 34));
        g.fillRoundRect(-17, -20, 11, 22, 6, 6);
        g.fillRoundRect(6, -20, 11, 22, 6, 6);
    }
}
