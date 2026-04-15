package sc2002.turnbased.ui.gui.model;

import java.util.Objects;

import sc2002.turnbased.engine.PlayerDecision;

public record ResolvedPlayerCommand(
    PlayerDecision decision,
    String actionName,
    String targetLabel
) {
    public ResolvedPlayerCommand {
        decision = Objects.requireNonNull(decision, "decision");
        actionName = Objects.requireNonNull(actionName, "actionName");
        targetLabel = Objects.requireNonNull(targetLabel, "targetLabel");
    }
}
