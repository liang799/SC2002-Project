package sc2002.turnbased.ui.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

final class WolfBodyRenderer implements FighterBodyRenderer {
    @Override
    public void renderBody(Graphics2D g, FighterSpriteDto sprite) {
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
    }
}
