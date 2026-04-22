package sc2002.turnbased.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sc2002.turnbased.domain.ItemType;
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
            lines.addAll(formatEvent(event));
        }
        return lines;
    }

    private List<String> formatEvent(BattleEvent event) {
        return event.visit(new BattleEvent.Visitor<>() {
            @Override
            public List<String> onAction(ActionEvent actionEvent) {
                return List.of(formatAction(actionEvent));
            }

            @Override
            public List<String> onNarration(NarrationEvent narrationEvent) {
                return List.of(narrationEvent.getText());
            }

            @Override
            public List<String> onRoundStart(RoundStartEvent roundStartEvent) {
                return List.of("Round " + roundStartEvent.getRoundNumber());
            }

            @Override
            public List<String> onRoundSummary(RoundSummaryEvent roundSummaryEvent) {
                return formatRoundSummary(roundSummaryEvent);
            }

            @Override
            public List<String> onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
                return List.of(formatSkippedTurn(skippedTurnEvent));
            }

            @Override
            public List<String> onStatusEffectReport(StatusEffectReportEvent statusEffectReportEvent) {
                return formatStatusEffectNotes(statusEffectReportEvent.statusEffectNotes());
            }
        });
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

        List<String> extraNotes = formatStatusEffectNotes(actionEvent.getStatusEffectNotes());
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

        List<String> statusNotes = formatStatusEffectNotes(skippedTurnEvent.getStatusEffectNotes());
        if (!statusNotes.isEmpty()) {
            builder.append(" | ").append(String.join(" | ", statusNotes));
        }

        return builder.toString();
    }

    private List<String> formatStatusEffectNotes(List<String> statusEffectNotes) {
        return List.copyOf(statusEffectNotes);
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
