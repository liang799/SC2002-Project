package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public class SkippedTurnEvent implements BattleEvent {
    private final String combatantName;
    private final String reason;
    private final List<StatusEffectEvent> statusEffectEvents;

    public SkippedTurnEvent(String combatantName, String reason, List<StatusEffectEvent> statusEffectEvents) {
        this.combatantName = combatantName;
        this.reason = reason;
        this.statusEffectEvents = List.copyOf(Objects.requireNonNull(statusEffectEvents, "statusEffectEvents"));
    }

    public String getCombatantName() {
        return combatantName;
    }

    public String getReason() {
        return reason;
    }

    public List<StatusEffectEvent> getStatusEffectEvents() {
        return statusEffectEvents;
    }
}
