package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.DefendStatusEffect;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class DefendAction implements BattleAction {
    @Override
    public String getName() {
        return "Defend";
    }

    @Override
    public TargetingMode targetingMode(Combatant actor) {
        return TargetingMode.NONE;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        actor.addStatusEffect(new DefendStatusEffect(2));
        return List.of(new NarrationEvent(
            actor.getName() + " -> Defend: DEF +10 for current and next round"
        ));
    }
}
