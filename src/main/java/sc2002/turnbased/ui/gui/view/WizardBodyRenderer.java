package sc2002.turnbased.ui.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

final class WizardBodyRenderer implements FighterBodyRenderer {
    @Override
    public void renderBody(Graphics2D g, FighterSpriteDto sprite) {
        double bob = Math.sin(sprite.walkPhase) * 3.0;
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
        g.setColor(new Color(35, 35, 34));
        g.fillRoundRect(-17, -20, 11, 22, 6, 6);
        g.fillRoundRect(6, -20, 11, 22, 6, 6);
    }
}
