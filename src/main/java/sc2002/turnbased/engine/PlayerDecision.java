package sc2002.turnbased.engine;

import sc2002.turnbased.actions.BattleAction;

public class PlayerDecision {
    private final BattleAction action;
    private final String targetName;

    public PlayerDecision(BattleAction action, String targetName) {
        this.action = action;
        this.targetName = targetName;
    }

    public BattleAction getAction() {
        return action;
    }

    public String getTargetName() {
        return targetName;
    }
}
