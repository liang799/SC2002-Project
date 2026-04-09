package sc2002.turnbased.domain.status;

import java.util.Objects;

public record StatusEffectChange(
    StatusEffectKind source,
    StatusEffectChangeType type,
    int magnitude,
    int duration
) implements StatusEffectOutcome {
    public StatusEffectChange {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        if (magnitude < 0) {
            throw new IllegalArgumentException("magnitude must not be negative");
        }
        if (duration < 0) {
            throw new IllegalArgumentException("duration must not be negative");
        }
    }

    public static StatusEffectChange applied(StatusEffectKind source) {
        return new StatusEffectChange(source, StatusEffectChangeType.APPLIED, 0, 0);
    }

    public static StatusEffectChange applied(StatusEffectKind source, int magnitude) {
        return new StatusEffectChange(source, StatusEffectChangeType.APPLIED, magnitude, 0);
    }

    public static StatusEffectChange applied(StatusEffectKind source, int magnitude, int duration) {
        return new StatusEffectChange(source, StatusEffectChangeType.APPLIED, magnitude, duration);
    }

    public static StatusEffectChange expired(StatusEffectKind source) {
        return new StatusEffectChange(source, StatusEffectChangeType.EXPIRED, 0, 0);
    }
}
