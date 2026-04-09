package sc2002.turnbased.domain;

import java.util.Objects;

public record CombatantId(String value) {
    public CombatantId {
        String normalizedValue = Objects.requireNonNull(value, "value").trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        value = normalizedValue;
    }

    public static CombatantId of(String value) {
        return new CombatantId(value);
    }
}
