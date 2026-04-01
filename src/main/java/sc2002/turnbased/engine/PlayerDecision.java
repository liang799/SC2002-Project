package sc2002.turnbased.engine;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;

public record PlayerDecision(BattleAction action, TargetReference targetReference) {
    public PlayerDecision {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(targetReference, "targetReference");
    }

    public static PlayerDecision targeted(BattleAction action, String targetName) {
        return new PlayerDecision(action, TargetReference.enemy(targetName));
    }

    public static PlayerDecision untargeted(BattleAction action) {
        return new PlayerDecision(action, TargetReference.none());
    }
}
