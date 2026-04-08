package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

public record DamageAdjustment(int damage, List<String> notes) {
    public DamageAdjustment {
        notes = List.copyOf(Objects.requireNonNull(notes, "notes"));
    }

    public static DamageAdjustment unchanged(int damage) {
        return new DamageAdjustment(damage, List.of());
    }
}
