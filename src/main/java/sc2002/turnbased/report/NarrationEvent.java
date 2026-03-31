package sc2002.turnbased.report;

public class NarrationEvent implements BattleEvent {
    private final String text;

    public NarrationEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
