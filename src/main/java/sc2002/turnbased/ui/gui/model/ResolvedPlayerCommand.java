package sc2002.turnbased.ui.gui.model;

import sc2002.turnbased.engine.PlayerDecision;

public record ResolvedPlayerCommand(
    PlayerDecision decision,
    String actionName,
    String targetLabel
) {
}
