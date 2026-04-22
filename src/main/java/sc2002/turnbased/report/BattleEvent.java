package sc2002.turnbased.report;

public interface BattleEvent {
    <T> T visit(Visitor<T> visitor);

    interface Visitor<T> {
        T onAction(ActionEvent actionEvent);

        T onNarration(NarrationEvent narrationEvent);

        T onRoundStart(RoundStartEvent roundStartEvent);

        T onRoundSummary(RoundSummaryEvent roundSummaryEvent);

        T onSkippedTurn(SkippedTurnEvent skippedTurnEvent);

        T onStatusEffectReport(StatusEffectReportEvent statusEffectReportEvent);
    }
}
