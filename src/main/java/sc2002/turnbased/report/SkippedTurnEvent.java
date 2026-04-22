package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.status.CombatantStatusOutcome;

public record SkippedTurnEvent(
    CombatantId combatantId,
    String combatantName,
    String reason,
    List<String> statusEffectNotes
) implements BattleEvent {
    public SkippedTurnEvent {
        combatantId = Objects.requireNonNull(combatantId, "combatantId");
        combatantName = Objects.requireNonNull(combatantName, "combatantName");
        reason = Objects.requireNonNull(reason, "reason");
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
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

    @Override
    public <T> T visit(Visitor<T> visitor) {
        return visitor.onSkippedTurn(this);
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
