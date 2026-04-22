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
        return event.visit(new BattleEvent.Visitor<>() {
            @Override
            public String onAction(ActionEvent actionEvent) {
                return actionMessage(actionEvent);
            }

            @Override
            public String onNarration(NarrationEvent narrationEvent) {
                return cleanBattleText(narrationEvent.getText());
            }

            @Override
            public String onRoundStart(RoundStartEvent roundStartEvent) {
                return "Round " + roundStartEvent.getRoundNumber() + " started!";
            }

            @Override
            public String onRoundSummary(RoundSummaryEvent roundSummaryEvent) {
                return "Round " + roundSummaryEvent.getRoundNumber() + " is over.";
            }

            @Override
            public String onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
                return skippedTurnMessage(skippedTurnEvent);
            }

            @Override
            public String onStatusEffectReport(StatusEffectReportEvent statusEffectReportEvent) {
                return statusEffectMessage(statusEffectReportEvent);
            }
        });
    }

    public int playbackDelayMillis(BattleEvent event) {
        int baseDelay = event.visit(new BattleEvent.Visitor<>() {
            @Override
            public Integer onAction(ActionEvent actionEvent) {
                return 1_350;
            }

            @Override
            public Integer onNarration(NarrationEvent narrationEvent) {
                return 1_250;
            }

            @Override
            public Integer onRoundStart(RoundStartEvent roundStartEvent) {
                return 850;
            }

            @Override
            public Integer onRoundSummary(RoundSummaryEvent roundSummaryEvent) {
                return 900;
            }

            @Override
            public Integer onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
                return 1_150;
            }

            @Override
            public Integer onStatusEffectReport(StatusEffectReportEvent statusEffectReportEvent) {
                return 1_100;
            }
        });
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
