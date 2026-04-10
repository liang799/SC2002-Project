package sc2002.turnbased.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CombatantIdTest {
    @Test
    void equals_WhenIdsWrapTheSameUuid_ReturnsTrue() {
        UUID uuid = UUID.randomUUID();
        CombatantId firstId = CombatantId.of(uuid);
        CombatantId secondId = CombatantId.of(uuid);

        assertEquals(firstId, secondId);
    }

    @Test
    void equals_WhenIdsWrapDifferentUuids_ReturnsFalse() {
        CombatantId firstId = CombatantId.of(UUID.randomUUID());
        CombatantId secondId = CombatantId.of(UUID.randomUUID());

        assertNotEquals(firstId, secondId);
    }

    @Test
    void hashCode_WhenIdsWrapTheSameUuid_MatchesEqualsContract() {
        UUID uuid = UUID.randomUUID();
        CombatantId firstId = CombatantId.of(uuid);
        CombatantId secondId = CombatantId.of(uuid);

        assertEquals(firstId.hashCode(), secondId.hashCode());
    }

    @Test
    void ofString_WhenUuidStringIsProvided_ParsesTheSameUuidValue() {
        UUID uuid = UUID.randomUUID();

        assertEquals(CombatantId.of(uuid), CombatantId.of(uuid.toString()));
    }

    @Test
    void toString_WhenIdIsRendered_ReturnsWrappedUuidString() {
        UUID uuid = UUID.randomUUID();
        CombatantId combatantId = CombatantId.of(uuid);

        assertEquals(uuid.toString(), combatantId.toString());
    }
}
