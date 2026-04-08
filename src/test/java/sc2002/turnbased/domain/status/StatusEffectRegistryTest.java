package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Warrior;

@Tag("unit")
class StatusEffectRegistryTest {
    @Test
    void resolveTurnWindow_whenStunBlocksNextTurn_marksTurnBlockedAndExpiresEffect() {
        StatusEffectRegistry registry = new StatusEffectRegistry();

        registry.add(new StunStatusEffect(1));

        TurnWindow turnWindow = registry.resolveTurnWindow();

        assertTrue(turnWindow.isBlocked());
        assertEquals("STUNNED", turnWindow.getBlockerLabel());
        assertEquals(List.of("Stun expires"), turnWindow.getNotes());
        assertEquals(List.of(), registry.activeStatusNames(true));
    }

    @Test
    void adjustIncomingDamage_whenSmokeBombBlocksEnemyAttack_returnsZeroAndExpiresEffect() {
        Warrior warrior = new Warrior();
        Goblin goblin = new Goblin("Goblin");

        warrior.addStatusEffect(new SmokeBombStatusEffect(1));

        DamageAdjustment adjustment = warrior.statusEffects().adjustIncomingDamage(warrior, goblin, 15);

        assertEquals(0, adjustment.damage());
        assertEquals(List.of("Smoke Bomb active", "Smoke Bomb effect expires"), adjustment.notes());
        assertEquals(List.of(), warrior.getActiveStatusNames());
    }
}
