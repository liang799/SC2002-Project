package sc2002.turnbased.domain.status;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultStatusEffectRegistryFactory implements StatusEffectRegistryFactory {
    private final Supplier<StatusEffectRegistry> statusEffectRegistrySupplier;

    public DefaultStatusEffectRegistryFactory(Supplier<StatusEffectRegistry> statusEffectRegistrySupplier) {
        this.statusEffectRegistrySupplier = Objects.requireNonNull(
            statusEffectRegistrySupplier,
            "statusEffectRegistrySupplier"
        );
    }

    @Override
    public StatusEffectRegistry create() {
        return Objects.requireNonNull(
            statusEffectRegistrySupplier.get(),
            "statusEffectRegistrySupplier returned null StatusEffectRegistry"
        );
    }
}
