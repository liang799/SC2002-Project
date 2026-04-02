package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.BattleEvent;

public interface BattleAction {
    String getName();

    default boolean advancesCooldown() {
        return true;
    }

    default TargetingMode targetingMode(Combatant actor) {
        return TargetingMode.SINGLE_ENEMY;
    }

    List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target);
}
