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

        if (actionEvent.getNotes().contains("ELIMINATED")) {
            builder.append(" ELIMINATED");
        }

        builder.append(" (dmg: ")
            .append(actionEvent.getAttackerAttack())
            .append("-")
            .append(actionEvent.getTargetDefense())
            .append("=")
            .append(actionEvent.getDamage())
            .append(")");

        List<String> extraNotes = new ArrayList<>();
        for (String note : actionEvent.getNotes()) {
            if (!"ELIMINATED".equals(note)) {
                extraNotes.add(note);
            }
        }
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

        if (!skippedTurnEvent.getNotes().isEmpty()) {
            builder.append(" | ").append(String.join(" | ", skippedTurnEvent.getNotes()));
        }

        return builder.toString();
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
