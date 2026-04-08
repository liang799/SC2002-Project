package sc2002.turnbased.domain.status;

public class DefaultStatusEffectRegistryFactory implements StatusEffectRegistryFactory {
    @Override
    public StatusEffectRegistry create() {
        return new StatusEffectRegistry(new StatusEffectEventPublisher());
    }
}
