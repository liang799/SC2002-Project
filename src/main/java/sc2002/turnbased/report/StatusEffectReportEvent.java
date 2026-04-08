package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public record StatusEffectReportEvent(List<StatusEffectEvent> statusEffectEvents) implements BattleEvent {
    public StatusEffectReportEvent {
        statusEffectEvents = List.copyOf(Objects.requireNonNull(statusEffectEvents, "statusEffectEvents"));
    }
}
