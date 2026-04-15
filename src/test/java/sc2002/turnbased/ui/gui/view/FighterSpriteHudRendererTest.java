package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.report.CombatantSummary;

class FighterSpriteHudRendererTest {
    @Test
    void healthRatioIsBoundedBetweenZeroAndOne() {
        assertEquals(1.0, FighterSpriteHudRenderer.healthRatio(sprite(130, 100)));
        assertEquals(0.0, FighterSpriteHudRenderer.healthRatio(sprite(-10, 100)));
        assertEquals(0.0, FighterSpriteHudRenderer.healthRatio(sprite(20, 0)));
        assertEquals(0.4, FighterSpriteHudRenderer.healthRatio(sprite(40, 100)));
    }

    private static FighterSpriteDto sprite(int hp, int maxHp) {
        return FighterSpriteDto.fromSummary(
            new CombatantSummary(CombatantId.generate(), "Goblin", hp, maxHp, 10, 10, true, List.of()),
            false
        );
    }
}
