package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.CombatantStatusOutcome;
import sc2002.turnbased.domain.status.DamageModifier;
import sc2002.turnbased.domain.status.StatusEffectChange;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.StatusEffectOutcome;

final class StatusEffectReportMapper {
    private StatusEffectReportMapper() {
    }

    static List<String> toNotes(List<CombatantStatusOutcome> statusEffectOutcomes) {
        return List.copyOf(
            Objects.requireNonNull(statusEffectOutcomes, "statusEffectOutcomes").stream()
                .map(StatusEffectReportMapper::toNote)
                .toList()
        );
    }

    private static String toNote(CombatantStatusOutcome statusEffectOutcome) {
        Objects.requireNonNull(statusEffectOutcome, "statusEffectOutcome");
        String combatantName = statusEffectOutcome.combatantName();
        StatusEffectOutcome outcome = statusEffectOutcome.outcome();

        if (outcome instanceof DamageModifier damageModifier) {
            return formatDamageModifier(damageModifier);
        }
        if (outcome instanceof StatusEffectChange statusEffectChange) {
            return formatStatusEffectChange(combatantName, statusEffectChange);
        }

        throw new IllegalArgumentException("Unsupported status effect outcome: " + outcome.getClass().getName());
    }

    private static String formatDamageModifier(DamageModifier damageModifier) {
        return switch (damageModifier.type()) {
            case BLOCKED -> displayName(damageModifier.source()) + " blocked the attack";
            case REDUCED -> displayName(damageModifier.source()) + " reduced the damage";
            case INCREASED -> displayName(damageModifier.source()) + " increased the damage";
        };
    }

    private static String formatStatusEffectChange(String combatantName, StatusEffectChange statusEffectChange) {
        return switch (statusEffectChange.type()) {
            case APPLIED -> formatAppliedChange(combatantName, statusEffectChange);
            case EXPIRED -> displayName(statusEffectChange.source()) + " expired";
        };
    }

    private static String formatAppliedChange(String combatantName, StatusEffectChange statusEffectChange) {
        return switch (statusEffectChange.source()) {
            case ARCANE_POWER -> combatantName + " gains ARCANE POWER +" + statusEffectChange.magnitude();
            case DEFEND -> combatantName + " DEF +" + statusEffectChange.magnitude()
                + " for " + statusEffectChange.duration() + " rounds";
            case SMOKE_BOMB -> combatantName + " gains Smoke Bomb protection for "
                + statusEffectChange.duration() + " enemy attacks";
            case STRENGTH_BOOST -> combatantName + " gains STRENGTH BOOST +" + statusEffectChange.magnitude()
                + " for " + statusEffectChange.duration() + " rounds";
            case STUN -> combatantName + " STUNNED (" + statusEffectChange.duration() + " turns)";
        };
    }

    private static String displayName(StatusEffectKind statusEffectKind) {
        return switch (statusEffectKind) {
            case ARCANE_POWER -> "Arcane Power";
            case DEFEND -> "Defend";
            case SMOKE_BOMB -> "Smoke Bomb";
            case STRENGTH_BOOST -> "Strength Boost";
            case STUN -> "Stun";
        };
    }
}
