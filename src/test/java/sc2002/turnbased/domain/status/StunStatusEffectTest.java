package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.support.FakeStatusEffectEventPublisher;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class StunStatusEffectTest {
    @Test
    void onTurnOpportunity_WhenCurrentAndNextTurnAreBlocked_BlocksExactlyTwoTurnsThenAllowsAction() {
        StunStatusEffect effect = new StunStatusEffect(2);
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        TurnEffectResolution firstResolution = effect.onTurnOpportunity(owner, eventPublisher);
        TurnEffectResolution secondResolution = effect.onTurnOpportunity(owner, eventPublisher);
        TurnEffectResolution thirdResolution = effect.onTurnOpportunity(owner, eventPublisher);

        assertAll(
            () -> assertEquals("STUNNED", effect.name()),
            () -> assertTrue(firstResolution.blocksAction()),
            () -> assertEquals("STUNNED", firstResolution.blockerLabel()),
            () -> assertTrue(secondResolution.blocksAction()),
            () -> assertEquals("STUNNED", secondResolution.blockerLabel()),
            () -> assertFalse(thirdResolution.blocksAction()),
            () -> assertNull(thirdResolution.blockerLabel()),
            () -> assertTrue(effect.isExpired())
        );
    }

    @Test
    void onTurnOpportunity_WhenAlreadyExpired_AllowsTurnWithoutNotes() {
        StunStatusEffect effect = new StunStatusEffect(0);
        Combatant owner = TestCombatantBuilder.aCombatant().build();
        FakeStatusEffectEventPublisher eventPublisher = new FakeStatusEffectEventPublisher();

        TurnEffectResolution resolution = effect.onTurnOpportunity(owner, eventPublisher);

        assertFalse(resolution.blocksAction());
        assertNull(resolution.blockerLabel());
        assertTrue(effect.isExpired());
    }
}
