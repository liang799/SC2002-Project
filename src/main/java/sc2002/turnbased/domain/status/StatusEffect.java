package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.status.event.StatusEffectExpiredEvent;

public interface StatusEffect {
    StatusEffectKind kind();

    String name();

    default void onRegistered(String ownerName, StatusEffectEventPublisher eventPublisher) {
    }

    default void onExpired(String ownerName, StatusEffectEventPublisher eventPublisher) {
        eventPublisher.publish(new StatusEffectExpiredEvent(ownerName, kind()));
    }

    default void onRoundCompleted() {
    }

    boolean isExpired();
}
