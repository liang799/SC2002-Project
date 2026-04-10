package sc2002.turnbased.engine;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;

public record PlayerDecision(BattleAction action, TargetReference targetReference) {
    public PlayerDecision {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(targetReference, "targetReference");
    }

    public static PlayerDecision targeted(BattleAction action, Combatant target) {
        return new PlayerDecision(action, TargetReference.enemy(target));
    }

    public static PlayerDecision targeted(BattleAction action, CombatantId targetId) {
        return new PlayerDecision(action, TargetReference.enemy(targetId));
    }

    public static PlayerDecision untargeted(BattleAction action) {
        return new PlayerDecision(action, TargetReference.none());
    }
}
