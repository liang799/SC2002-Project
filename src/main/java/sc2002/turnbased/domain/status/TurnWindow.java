package sc2002.turnbased.domain.status;

public class TurnWindow {
    private final boolean blocked;
    private final String blockerLabel;

    public TurnWindow(boolean blocked, String blockerLabel) {
        this.blocked = blocked;
        this.blockerLabel = blockerLabel;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String getBlockerLabel() {
        return blockerLabel;
    }
}
