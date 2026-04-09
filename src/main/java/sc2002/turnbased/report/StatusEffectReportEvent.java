package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

public record StatusEffectReportEvent(List<String> statusEffectNotes) implements BattleEvent {
    public StatusEffectReportEvent {
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }
}
