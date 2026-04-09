package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.AttackResolution;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.StunStatusEffect;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;

public class ShieldBashAction implements BattleAction {
    @Override
    public String getName() {
        return "Shield Bash";
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        AttackResolution attackResolution = actor.attack(target);
        if (!attackResolution.targetEliminated()) {
            attackResolution = attackResolution.appendStatusEffectNotes(target.addStatusEffect(new StunStatusEffect(2)));
        }
        return List.of(new ActionEvent(actor.getName(), getName(), target.getName(), attackResolution));
    }
}
