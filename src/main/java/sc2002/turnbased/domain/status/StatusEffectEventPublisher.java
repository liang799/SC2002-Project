package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public class StatusEffectEventPublisher {
    private final List<StatusEffectObserver> observers = new ArrayList<>();

    public void addObserver(StatusEffectObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }

    public void removeObserver(StatusEffectObserver observer) {
        observers.remove(observer);
    }

    public void publish(StatusEffectEvent event) {
        StatusEffectEvent publishedEvent = Objects.requireNonNull(event, "event");
        for (StatusEffectObserver observer : List.copyOf(observers)) {
            observer.onStatusEffectEvent(publishedEvent);
        }
    }
}
