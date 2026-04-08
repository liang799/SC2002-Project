package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public interface StatusEffectObserver {
    void onStatusEffectEvent(StatusEffectEvent event);
}
