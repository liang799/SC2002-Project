package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.CombatantStatusOutcome;

public record StatusEffectReportEvent(List<String> statusEffectNotes) implements BattleEvent {
    public StatusEffectReportEvent {
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public static StatusEffectReportEvent fromStatusEffectOutcomes(List<CombatantStatusOutcome> statusEffectOutcomes) {
        return new StatusEffectReportEvent(StatusEffectReportMapper.toNotes(statusEffectOutcomes));
    }
}
