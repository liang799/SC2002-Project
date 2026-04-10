package sc2002.turnbased.domain;

import java.util.Objects;
import java.util.UUID;

public record CombatantId(UUID value) {
    public CombatantId {
        Objects.requireNonNull(value, "value");
    }

    public static CombatantId generate() {
        return new CombatantId(UUID.randomUUID());
    }

    public static CombatantId of(UUID value) {
        return new CombatantId(value);
    }

    public static CombatantId of(String value) {
        String normalizedValue = Objects.requireNonNull(value, "value").trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        return new CombatantId(UUID.fromString(normalizedValue));
    }
}
