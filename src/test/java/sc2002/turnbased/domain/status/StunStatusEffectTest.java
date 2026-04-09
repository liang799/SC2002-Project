package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class StunStatusEffectTest {
    @Test
    void constructor_WhenBlockedTurnsRemainingIsZero_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new StunStatusEffect(0)
        );

        assertEquals("blockedTurnsRemaining must be positive", exception.getMessage());
    }

    @Test
    void onTurnOpportunity_WhenCurrentAndNextTurnAreBlocked_BlocksExactlyTwoTurnsThenAllowsAction() {
        StunStatusEffect effect = new StunStatusEffect(2);
        Combatant owner = TestCombatantBuilder.aCombatant().build();

        java.util.Optional<String> firstReason = effect.getTurnBlockReason(owner);
        java.util.Optional<String> secondReason = effect.getTurnBlockReason(owner);
        java.util.Optional<String> thirdReason = effect.getTurnBlockReason(owner);

        assertAll(
            () -> assertEquals("STUNNED", effect.description()),
            () -> assertTrue(firstReason.isPresent()),
            () -> assertEquals("STUNNED", firstReason.get()),
            () -> assertTrue(secondReason.isPresent()),
            () -> assertEquals("STUNNED", secondReason.get()),
            () -> assertTrue(thirdReason.isEmpty()),
            () -> assertTrue(effect.isExpired())
        );
    }

}
