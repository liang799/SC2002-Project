package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

/**
 * This is a design pattern
 * <a href="https://refactoring.guru/design-patterns/observer">Observer Design Pattern</a>
 */
public class StatusEffectObservationScope implements AutoCloseable, StatusEffectObserver {
    private final StatusEffectEventPublisher eventPublisher;
    private final List<StatusEffectEvent> observedEvents = new ArrayList<>();
    private boolean closed;

    public StatusEffectObservationScope(StatusEffectEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.eventPublisher.addObserver(this);
    }

    @Override
    public void onStatusEffectEvent(StatusEffectEvent event) {
        observedEvents.add(Objects.requireNonNull(event, "event"));
    }

    public List<StatusEffectEvent> observedEvents() {
        return List.copyOf(observedEvents);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        eventPublisher.removeObserver(this);
        closed = true;
    }
}
