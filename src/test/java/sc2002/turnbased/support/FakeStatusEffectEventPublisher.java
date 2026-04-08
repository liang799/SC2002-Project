package sc2002.turnbased.support;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.status.StatusEffectEventPublisher;
import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public class FakeStatusEffectEventPublisher extends StatusEffectEventPublisher {
    private final List<StatusEffectEvent> publishedEvents = new ArrayList<>();

    @Override
    public void publish(StatusEffectEvent event) {
        publishedEvents.add(event);
    }

    public List<StatusEffectEvent> publishedEvents() {
        return List.copyOf(publishedEvents);
    }
}
