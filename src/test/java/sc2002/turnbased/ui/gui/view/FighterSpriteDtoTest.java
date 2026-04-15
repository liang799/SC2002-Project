package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.support.TestDependencies;

class FighterSpriteDtoTest {
    @Test
    void isTopLevelPublicDtoClass() {
        assertTrue(Modifier.isPublic(FighterSpriteDto.class.getModifiers()));
        assertFalse(FighterSpriteDto.class.isMemberClass());
    }

    @Test
    void createsSpriteFromCombatantSnapshot() {
        Combatant goblin = TestDependencies.goblin("Goblin A");
        goblin.receiveDamage(12);

        FighterSpriteDto sprite = FighterSpriteDto.fromCombatant(goblin, false);

        assertEquals(goblin.combatantId(), sprite.id);
        assertEquals("Goblin A", sprite.name);
        assertFalse(sprite.player);
        assertEquals(goblin.getCurrentHp(), sprite.hp);
        assertEquals(goblin.getMaxHp(), sprite.maxHp);
        assertEquals(goblin.getAttack(), sprite.attack);
        assertEquals(goblin.getBaseAttack(), sprite.baseAttack);
        assertTrue(sprite.alive);
        assertEquals(List.of(), sprite.statuses);
    }

    @Test
    void updatesSpriteFromSummaryWithoutChangingPositionState() {
        CombatantId id = CombatantId.generate();
        FighterSpriteDto sprite = FighterSpriteDto.fromSummary(
            new CombatantSummary(id, "Wolf", 30, 45, 12, 12, true, List.of()),
            false
        );
        sprite.x = 140.0;
        sprite.y = 240.0;
        sprite.offsetX = 8.0;
        sprite.offsetY = -4.0;

        sprite.updateFrom(new CombatantSummary(
            id,
            "Wolf",
            0,
            45,
            17,
            12,
            false,
            List.of("STUN")
        ));

        assertEquals(0, sprite.hp);
        assertEquals(45, sprite.maxHp);
        assertEquals(17, sprite.attack);
        assertEquals(12, sprite.baseAttack);
        assertFalse(sprite.alive);
        assertEquals(List.of("STUN"), sprite.statuses);
        assertEquals(148.0, sprite.drawX());
        assertEquals(236.0, sprite.drawY());
    }

    @Test
    void defensivelyCopiesStatusLists() {
        List<String> statuses = new java.util.ArrayList<>(List.of("SMOKE BOMB"));
        FighterSpriteDto sprite = FighterSpriteDto.fromSummary(
            new CombatantSummary(CombatantId.generate(), "Wizard", 50, 80, 20, 20, true, statuses),
            true
        );

        statuses.add("MUTATED");

        assertEquals(List.of("SMOKE BOMB"), sprite.statuses);
        assertThrows(UnsupportedOperationException.class, () -> sprite.statuses.add("MUTATED"));
    }
}
