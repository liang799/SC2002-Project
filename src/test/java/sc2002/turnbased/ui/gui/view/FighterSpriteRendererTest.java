package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.report.CombatantSummary;

class FighterSpriteRendererTest {
    private final FighterSpriteRenderer renderer = new FighterSpriteRenderer();

    @Test
    void routesWizardSpriteFromDtoType() {
        assertRenderer(WizardBodyRenderer.class, sprite("Wizard", true));
    }

    @Test
    void routesWarriorSpriteFromDtoType() {
        assertRenderer(WarriorBodyRenderer.class, sprite("Warrior", true));
    }

    @Test
    void routesGoblinSpriteFromDtoType() {
        assertRenderer(GoblinBodyRenderer.class, sprite("Goblin A", false));
    }

    @Test
    void routesWolfSpriteFromDtoType() {
        assertRenderer(WolfBodyRenderer.class, sprite("Wolf A", false));
    }

    @Test
    void routesUnknownEnemyFromDtoType() {
        assertRenderer(UnknownFighterBodyRenderer.class, sprite("Slime A", false));
    }

    @Test
    void rejectsNullInputs() {
        FighterSpriteDto sprite = sprite("Goblin A", false);

        assertThrows(NullPointerException.class, () -> renderer.bodyRendererFor(null));
        assertThrows(NullPointerException.class, () -> renderer.render(null, sprite, sprite.id));
    }

    @Test
    void rejectsRendererMapWithoutUnknownFallback() {
        Map<FighterType, FighterBodyRenderer> bodyRenderers = new EnumMap<>(FighterType.class);
        bodyRenderers.put(FighterType.GOBLIN, new GoblinBodyRenderer());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FighterSpriteRenderer(
            bodyRenderers,
            new FighterSpriteEffectRenderer(),
            new FighterSpriteHudRenderer()
        ));

        assertTrue(exception.getMessage().contains("FighterType.UNKNOWN"));
    }

    private void assertRenderer(Class<? extends FighterBodyRenderer> expectedType, FighterSpriteDto sprite) {
        assertEquals(expectedType, renderer.bodyRendererFor(sprite).getClass());
    }

    private static FighterSpriteDto sprite(String name, boolean player) {
        FighterSpriteDto sprite = FighterSpriteDto.fromSummary(
            new CombatantSummary(CombatantId.generate(), name, 35, 55, 20, 20, true, List.of("READY")),
            player
        );
        sprite.x = 150;
        sprite.y = 180;
        sprite.walkPhase = 0.5;
        sprite.hurtUntil = System.nanoTime() + 1_000_000_000L;
        return sprite;
    }
}
