package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnEffectResolution {
    private final boolean blocksAction;
    private final String blockerLabel;
    private final List<String> notes;

    public TurnEffectResolution(boolean blocksAction, String blockerLabel, List<String> notes) {
        this.blocksAction = blocksAction;
        this.blockerLabel = blockerLabel;
        this.notes = new ArrayList<>(notes);
    }

    public static TurnEffectResolution allow() {
        return new TurnEffectResolution(false, null, List.of());
    }

    public boolean blocksAction() {
        return blocksAction;
    }

    public String blockerLabel() {
        return blockerLabel;
    }

    public List<String> notes() {
        return Collections.unmodifiableList(notes);
    }
}
