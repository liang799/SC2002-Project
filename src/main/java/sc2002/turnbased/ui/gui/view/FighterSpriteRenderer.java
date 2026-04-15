package sc2002.turnbased.ui.gui.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import sc2002.turnbased.domain.CombatantId;

public final class FighterSpriteRenderer {
    private final Map<FighterType, FighterBodyRenderer> bodyRenderers;
    private final FighterSpriteEffectRenderer effectRenderer;
    private final FighterSpriteHudRenderer hudRenderer;

    public FighterSpriteRenderer() {
        this(defaultBodyRenderers(), new FighterSpriteEffectRenderer(), new FighterSpriteHudRenderer());
    }

    FighterSpriteRenderer(
        Map<FighterType, FighterBodyRenderer> bodyRenderers,
        FighterSpriteEffectRenderer effectRenderer,
        FighterSpriteHudRenderer hudRenderer
    ) {
        this.bodyRenderers = new EnumMap<>(FighterType.class);
        this.bodyRenderers.putAll(Objects.requireNonNull(bodyRenderers, "bodyRenderers"));
        this.effectRenderer = Objects.requireNonNull(effectRenderer, "effectRenderer");
        this.hudRenderer = Objects.requireNonNull(hudRenderer, "hudRenderer");
    }

    public void render(Graphics2D g, FighterSpriteDto sprite, CombatantId selectedEnemyId) {
        Objects.requireNonNull(g, "g");
        Objects.requireNonNull(sprite, "sprite");

        Graphics2D copy = (Graphics2D) g.create();
        double x = sprite.drawX();
        double y = sprite.drawY();
        copy.translate(x, y);
        if (!sprite.alive) {
            copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.52f));
            copy.rotate(sprite.player ? -0.25 : 0.32);
            copy.translate(0, 24);
        }

        copy.setColor(new Color(0, 0, 0, sprite.alive ? 72 : 42));
        copy.fill(new Ellipse2D.Double(-36, -8, 72, 18));

        if (sprite.id.equals(selectedEnemyId) && sprite.alive) {
            copy.setStroke(new BasicStroke(3f));
            copy.setColor(new Color(255, 229, 87, 210));
            copy.draw(new Ellipse2D.Double(-44, -15, 88, 28));
            copy.setColor(new Color(255, 84, 74, 130));
            copy.draw(new Ellipse2D.Double(-52, -21, 104, 40));
        }

        bodyRendererFor(sprite).renderBody(copy, sprite);
        effectRenderer.renderPulse(copy, sprite);

        copy.dispose();
        hudRenderer.render(g, sprite, (int) x, (int) y);
    }

    FighterBodyRenderer bodyRendererFor(FighterSpriteDto sprite) {
        Objects.requireNonNull(sprite, "sprite");
        return bodyRenderers.getOrDefault(sprite.type, bodyRenderers.get(FighterType.UNKNOWN));
    }

    private static Map<FighterType, FighterBodyRenderer> defaultBodyRenderers() {
        Map<FighterType, FighterBodyRenderer> renderers = new EnumMap<>(FighterType.class);
        renderers.put(FighterType.WARRIOR, new WarriorBodyRenderer());
        renderers.put(FighterType.WIZARD, new WizardBodyRenderer());
        renderers.put(FighterType.GOBLIN, new GoblinBodyRenderer());
        renderers.put(FighterType.WOLF, new WolfBodyRenderer());
        renderers.put(FighterType.UNKNOWN, new UnknownFighterBodyRenderer());
        return renderers;
    }
}
