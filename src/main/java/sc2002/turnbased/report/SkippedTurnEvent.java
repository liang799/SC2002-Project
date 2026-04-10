package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.status.CombatantStatusOutcome;

public class SkippedTurnEvent implements BattleEvent {
    private final CombatantId combatantId;
    private final String combatantName;
    private final String reason;
    private final List<String> statusEffectNotes;

    public SkippedTurnEvent(CombatantId combatantId, String combatantName, String reason, List<String> statusEffectNotes) {
        this.combatantId = Objects.requireNonNull(combatantId, "combatantId");
        this.combatantName = combatantName;
        this.reason = reason;
        this.statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public static SkippedTurnEvent fromStatusEffectOutcomes(
        Combatant combatant,
        String reason,
        List<CombatantStatusOutcome> statusEffectOutcomes
    ) {
        Combatant skippedCombatant = Objects.requireNonNull(combatant, "combatant");
        return new SkippedTurnEvent(
            skippedCombatant.combatantId(),
            skippedCombatant.getName(),
            reason,
            StatusEffectReportMapper.toNotes(statusEffectOutcomes)
        );
    }

    public CombatantId getCombatantId() {
        return combatantId;
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
