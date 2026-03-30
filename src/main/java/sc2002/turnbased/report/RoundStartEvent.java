package sc2002.turnbased.report;

public class RoundStartEvent implements BattleEvent {
    private final int roundNumber;

    public RoundStartEvent(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
