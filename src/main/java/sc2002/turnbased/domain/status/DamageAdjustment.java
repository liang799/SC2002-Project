package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Objects;

public record DamageAdjustment(int damage, List<String> notes) {
    public DamageAdjustment {
        notes = List.copyOf(Objects.requireNonNull(notes, "notes"));
    }

    public DamageAdjustment(int damage) {
        this(damage, List.of());
    }

    public static DamageAdjustment unchanged(int damage) {
        return new DamageAdjustment(damage);
    }
}
