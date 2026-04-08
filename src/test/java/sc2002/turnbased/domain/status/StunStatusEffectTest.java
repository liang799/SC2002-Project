package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StunStatusEffectTest {
    @Test
    void onTurnOpportunity_whenMultipleTurnsRemain_blocksAndExpiresOnLastBlockedTurn() {
        StunStatusEffect effect = new StunStatusEffect(2);

        TurnEffectResolution firstResolution = effect.onTurnOpportunity();

        assertEquals("STUNNED", effect.name());
        assertTrue(firstResolution.blocksAction());
        assertEquals("STUNNED", firstResolution.blockerLabel());
        assertEquals(List.of(), firstResolution.notes());
        assertFalse(effect.isExpired());

        TurnEffectResolution secondResolution = effect.onTurnOpportunity();

        assertTrue(secondResolution.blocksAction());
        assertEquals(List.of("Stun expires"), secondResolution.notes());
        assertTrue(effect.isExpired());
    }

    @Test
    void onTurnOpportunity_whenAlreadyExpired_allowsTurnWithoutNotes() {
        StunStatusEffect effect = new StunStatusEffect(0);

        TurnEffectResolution resolution = effect.onTurnOpportunity();

        assertFalse(resolution.blocksAction());
        assertNull(resolution.blockerLabel());
        assertEquals(List.of(), resolution.notes());
        assertTrue(effect.isExpired());
    }
}
