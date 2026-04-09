package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Objects;

public record DamageAdjustment(int damage, List<DamageModifier> modifiers) {
    public DamageAdjustment {
        modifiers = List.copyOf(Objects.requireNonNull(modifiers, "modifiers"));
    }

    public DamageAdjustment(int damage) {
        this(damage, List.of());
    }

    public static DamageAdjustment unchanged(int damage) {
        return new DamageAdjustment(damage);
    }
}
