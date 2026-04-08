package sc2002.turnbased.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.event.ArcanePowerAppliedEvent;
import sc2002.turnbased.domain.status.event.DefendAppliedEvent;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.domain.status.event.SmokeBombAppliedEvent;
import sc2002.turnbased.domain.status.event.StatusEffectEvent;
import sc2002.turnbased.domain.status.event.StatusEffectExpiredEvent;
import sc2002.turnbased.domain.status.event.StunAppliedEvent;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

public class BattleConsoleFormatter {
    public List<String> format(List<BattleEvent> events) {
        List<String> lines = new ArrayList<>();
        for (BattleEvent event : events) {
            if (event instanceof RoundStartEvent roundStartEvent) {
                lines.add("Round " + roundStartEvent.getRoundNumber());
                continue;
            }
            if (event instanceof ActionEvent actionEvent) {
                lines.add(formatAction(actionEvent));
                continue;
            }
            if (event instanceof NarrationEvent narrationEvent) {
                lines.add(narrationEvent.getText());
                continue;
            }
            if (event instanceof SkippedTurnEvent skippedTurnEvent) {
                lines.add(formatSkippedTurn(skippedTurnEvent));
                continue;
            }
            if (event instanceof StatusEffectReportEvent statusEffectReportEvent) {
                lines.addAll(formatStatusEffectEvents(statusEffectReportEvent.statusEffectEvents()));
                continue;
            }
            if (event instanceof RoundSummaryEvent roundSummaryEvent) {
                lines.addAll(formatRoundSummary(roundSummaryEvent));
            }
        }
        return lines;
    }

    private String formatAction(ActionEvent actionEvent) {
        StringBuilder builder = new StringBuilder();
        builder.append(actionEvent.getActorName())
            .append(" -> ")
            .append(actionEvent.getActionName())
            .append(" -> ")
            .append(actionEvent.getTargetName())
            .append(": HP: ")
            .append(actionEvent.getHpBefore())
            .append(" -> ")
            .append(actionEvent.getHpAfter());

        if (actionEvent.isTargetEliminated()) {
            builder.append(" ELIMINATED");
        }

        builder.append(" (dmg: ")
            .append(actionEvent.getAttackerAttack())
            .append("-")
            .append(actionEvent.getTargetDefense())
            .append("=")
            .append(actionEvent.getDamage())
            .append(")");

        List<String> extraNotes = formatStatusEffectEvents(actionEvent.getStatusEffectEvents());
        if (!extraNotes.isEmpty()) {
            builder.append(" | ").append(String.join(" | ", extraNotes));
        }

        return builder.toString();
    }

    private String formatSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
        StringBuilder builder = new StringBuilder();
        builder.append(skippedTurnEvent.getCombatantName())
            .append(" -> ")
            .append(skippedTurnEvent.getReason())
            .append(": ");

        if ("ELIMINATED".equals(skippedTurnEvent.getReason())) {
            builder.append("Skipped");
        } else {
            builder.append("Turn skipped");
        }

        List<String> statusNotes = formatStatusEffectEvents(skippedTurnEvent.getStatusEffectEvents());
        if (!statusNotes.isEmpty()) {
            builder.append(" | ").append(String.join(" | ", statusNotes));
        }

        return builder.toString();
    }

    private List<String> formatStatusEffectEvents(List<StatusEffectEvent> statusEffectEvents) {
        List<String> lines = new ArrayList<>();
        for (StatusEffectEvent statusEffectEvent : statusEffectEvents) {
            if (statusEffectEvent instanceof SmokeBombActivatedEvent) {
                lines.add("Smoke Bomb active");
                continue;
            }
            if (statusEffectEvent instanceof SmokeBombAppliedEvent smokeBombAppliedEvent) {
                lines.add(smokeBombAppliedEvent.ownerName()
                    + " gains Smoke Bomb protection for "
                    + smokeBombAppliedEvent.protectedEnemyAttacks()
                    + " enemy attacks");
                continue;
            }
            if (statusEffectEvent instanceof StunAppliedEvent stunAppliedEvent) {
                lines.add(stunAppliedEvent.ownerName() + " STUNNED (" + stunAppliedEvent.blockedTurnsRemaining() + " turns)");
                continue;
            }
            if (statusEffectEvent instanceof DefendAppliedEvent defendAppliedEvent) {
                lines.add(defendAppliedEvent.ownerName()
                    + " DEF +"
                    + defendAppliedEvent.defenseBonus()
                    + " for "
                    + defendAppliedEvent.roundsRemaining()
                    + " rounds");
                continue;
            }
            if (statusEffectEvent instanceof ArcanePowerAppliedEvent arcanePowerAppliedEvent) {
                lines.add(arcanePowerAppliedEvent.ownerName() + " gains ARCANE POWER +" + arcanePowerAppliedEvent.totalAttackBonus());
                continue;
            }
            if (statusEffectEvent instanceof StatusEffectExpiredEvent expiredEvent) {
                lines.add(formatExpiration(expiredEvent.effectKind()));
            }
        }
        return lines;
    }

    private String formatExpiration(StatusEffectKind statusEffectKind) {
        return switch (statusEffectKind) {
            case ARCANE_POWER -> "Arcane Power expires";
            case DEFEND -> "Defend expires";
            case SMOKE_BOMB -> "Smoke Bomb effect expires";
            case STUN -> "Stun expires";
        };
    }

    private List<String> formatRoundSummary(RoundSummaryEvent roundSummaryEvent) {
        List<String> lines = new ArrayList<>();
        lines.add("End of Round " + roundSummaryEvent.getRoundNumber() + ":");
        lines.add("- " + roundSummaryEvent.getPlayerSummary().getName() + " HP " + roundSummaryEvent.getPlayerSummary().getCurrentHp()
            + "/" + roundSummaryEvent.getPlayerSummary().getMaxHp());

        for (CombatantSummary enemySummary : roundSummaryEvent.getEnemySummaries()) {
            lines.add("- " + formatEnemySummary(enemySummary));
        }

        Map<ItemType, Integer> items = roundSummaryEvent.getInventorySnapshot();
        for (ItemType itemType : ItemType.values()) {
            if (items.containsKey(itemType)) {
                lines.add("- " + itemType.getDisplayName() + " " + items.get(itemType));
            }
        }
        int cooldown = roundSummaryEvent.getSpecialSkillCooldown();
        lines.add("- Special Skills Cooldown " + cooldown + " " + (cooldown == 1 ? "round" : "rounds"));
        if (roundSummaryEvent.getPlayerSummary().getCurrentAttack() != roundSummaryEvent.getPlayerSummary().getBaseAttack()) {
            lines.add("- " + roundSummaryEvent.getPlayerSummary().getName() + " ATK " + roundSummaryEvent.getPlayerSummary().getCurrentAttack());
        }
        return lines;
    }

    private String formatEnemySummary(CombatantSummary enemySummary) {
        if (!enemySummary.isAlive()) {
            return enemySummary.getName() + " eliminated";
        }
        if (enemySummary.getActiveStatuses().isEmpty()) {
            return enemySummary.getName() + " HP " + enemySummary.getCurrentHp();
        }
        return enemySummary.getName() + " HP " + enemySummary.getCurrentHp() + " [" + String.join(", ", enemySummary.getActiveStatuses()) + "]";
    }
}
