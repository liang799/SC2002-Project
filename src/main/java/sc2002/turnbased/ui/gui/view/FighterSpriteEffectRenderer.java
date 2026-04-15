package sc2002.turnbased.ui.gui.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

final class FighterSpriteEffectRenderer {
    void renderPulse(Graphics2D g, FighterSpriteDto sprite) {
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
}
