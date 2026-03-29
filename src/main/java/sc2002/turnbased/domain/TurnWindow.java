package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnWindow {
    private final boolean blocked;
    private final String blockerLabel;
    private final List<String> notes;

    public TurnWindow(boolean blocked, String blockerLabel, List<String> notes) {
        this.blocked = blocked;
        this.blockerLabel = blockerLabel;
        this.notes = new ArrayList<>(notes);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String getBlockerLabel() {
        return blockerLabel;
    }

    public List<String> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}
