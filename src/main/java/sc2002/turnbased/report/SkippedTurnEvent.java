package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.CombatantStatusOutcome;

public class SkippedTurnEvent implements BattleEvent {
    private final String combatantName;
    private final String reason;
    private final List<String> statusEffectNotes;

    public SkippedTurnEvent(String combatantName, String reason, List<String> statusEffectNotes) {
        this.combatantName = combatantName;
        this.reason = reason;
        this.statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public static SkippedTurnEvent fromStatusEffectOutcomes(
        String combatantName,
        String reason,
        List<CombatantStatusOutcome> statusEffectOutcomes
    ) {
        return new SkippedTurnEvent(combatantName, reason, StatusEffectReportMapper.toNotes(statusEffectOutcomes));
    }

    public String getCombatantName() {
        return combatantName;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getStatusEffectNotes() {
        return statusEffectNotes;
    }
}
