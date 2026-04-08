package sc2002.turnbased.domain.status;

public class TurnEffectResolution {
    private final boolean blocksAction;
    private final String blockerLabel;

    public TurnEffectResolution(boolean blocksAction, String blockerLabel) {
        this.blocksAction = blocksAction;
        this.blockerLabel = blockerLabel;
    }

    public static TurnEffectResolution allow() {
        return new TurnEffectResolution(false, null);
    }

    public boolean blocksAction() {
        return blocksAction;
    }

    public String blockerLabel() {
        return blockerLabel;
    }
}
