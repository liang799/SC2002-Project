package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.AttackResolution;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;

public class BasicAttackAction implements BattleAction {
    @Override
    public String getName() {
        return "BasicAttack";
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        AttackResolution attackResolution = actor.attack(target);
        return List.of(new ActionEvent(actor, getName(), target, attackResolution));
    }
}
