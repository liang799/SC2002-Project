package sc2002.turnbased.domain.status;

import java.util.Objects;

public record DamageModifier(StatusEffectKind source, DamageModifierType type) implements StatusEffectOutcome {
    public DamageModifier {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
    }
}
