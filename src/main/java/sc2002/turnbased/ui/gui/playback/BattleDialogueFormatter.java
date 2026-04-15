package sc2002.turnbased.ui.gui.playback;

import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

public final class BattleDialogueFormatter {
    public String format(BattleEvent event) {
        if (event instanceof ActionEvent actionEvent) {
            return actionMessage(actionEvent);
        }
        if (event instanceof NarrationEvent narrationEvent) {
            return cleanBattleText(narrationEvent.getText());
        }
        if (event instanceof SkippedTurnEvent skippedTurnEvent) {
            return skippedTurnMessage(skippedTurnEvent);
        }
        if (event instanceof StatusEffectReportEvent statusEffectReportEvent) {
            return statusEffectMessage(statusEffectReportEvent);
        }
        if (event instanceof RoundStartEvent roundStartEvent) {
            return "Round " + roundStartEvent.getRoundNumber() + " started!";
        }
        if (event instanceof RoundSummaryEvent roundSummaryEvent) {
            return "Round " + roundSummaryEvent.getRoundNumber() + " is over.";
        }
        return "The battle continues.";
    }

    public int playbackDelayMillis(BattleEvent event) {
        int baseDelay = 1_000;
        if (event instanceof ActionEvent) {
            baseDelay = 1_350;
        } else if (event instanceof NarrationEvent) {
            baseDelay = 1_250;
        } else if (event instanceof SkippedTurnEvent) {
            baseDelay = 1_150;
        } else if (event instanceof StatusEffectReportEvent) {
            baseDelay = 1_100;
        } else if (event instanceof RoundStartEvent) {
            baseDelay = 850;
        } else if (event instanceof RoundSummaryEvent) {
            baseDelay = 900;
        }
        int typewriterDelay = format(event).length() * 12 + 520;
        return Math.max(baseDelay, Math.min(2_000, typewriterDelay));
    }

    private String actionMessage(ActionEvent actionEvent) {
        StringBuilder message = new StringBuilder();
        message.append(actionEvent.getActorName())
            .append(" used ")
            .append(actionEvent.getActionName())
            .append("!");
        if (actionEvent.getDamage() > 0) {
            message.append(" ")
                .append(actionEvent.getTargetName())
                .append(" took ")
                .append(actionEvent.getDamage())
                .append(" damage.");
        } else {
            message.append(" ")
                .append(actionEvent.getTargetName())
                .append(" blocked the hit.");
        }
        if (actionEvent.isTargetEliminated()) {
            message.append(" ")
                .append(actionEvent.getTargetName())
                .append(" fainted!");
        }
        if (!actionEvent.getStatusEffectNotes().isEmpty()) {
            message.append(" ")
                .append(String.join(" ", actionEvent.getStatusEffectNotes()));
        }
        return message.toString();
    }

    private String skippedTurnMessage(SkippedTurnEvent skippedTurnEvent) {
        if ("ELIMINATED".equals(skippedTurnEvent.getReason())) {
            return skippedTurnEvent.getCombatantName() + " has fainted.";
        }
        return skippedTurnEvent.getCombatantName() + " cannot move: "
            + cleanBattleText(skippedTurnEvent.getReason()) + ".";
    }

    private String statusEffectMessage(StatusEffectReportEvent statusEffectReportEvent) {
        if (statusEffectReportEvent.statusEffectNotes().isEmpty()) {
            return "Status effects shifted.";
        }
        return String.join(" ", statusEffectReportEvent.statusEffectNotes());
    }

    private String cleanBattleText(String text) {
        return text.replace(" -> ", " ");
    }
}
