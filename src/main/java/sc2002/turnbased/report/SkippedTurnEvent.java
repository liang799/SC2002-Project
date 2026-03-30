package sc2002.turnbased.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkippedTurnEvent implements BattleEvent {
    private final String combatantName;
    private final String reason;
    private final List<String> notes;

    public SkippedTurnEvent(String combatantName, String reason, List<String> notes) {
        this.combatantName = combatantName;
        this.reason = reason;
        this.notes = new ArrayList<>(notes);
    }

    public String getCombatantName() {
        return combatantName;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}
