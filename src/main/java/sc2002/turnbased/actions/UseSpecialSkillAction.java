package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;

public class UseSpecialSkillAction implements BattleAction {
    @Override
    public String getName() {
        return "SpecialSkill";
    }

    @Override
    public TargetingMode targetingMode(Combatant actor) {
        if (actor instanceof PlayerCharacter player) {
            return player.createSpecialSkillAction(true).targetingMode(actor);
        }
        return TargetingMode.NONE;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        if (!(actor instanceof PlayerCharacter player)) {
            throw new IllegalStateException("Special skills are only supported for player characters");
        }
        return player.createSpecialSkillAction(true).execute(context, actor, target);
    }
}
