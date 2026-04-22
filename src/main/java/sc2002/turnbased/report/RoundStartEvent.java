package sc2002.turnbased.report;

public record RoundStartEvent(int roundNumber) implements BattleEvent {
    @Override
    public <T> T visit(Visitor<T> visitor) {
        return visitor.onRoundStart(this);
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
