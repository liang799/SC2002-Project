package sc2002.turnbased.ui.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

final class GoblinBodyRenderer implements FighterBodyRenderer {
    @Override
    public void renderBody(Graphics2D g, FighterSpriteDto sprite) {
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
    }
}
