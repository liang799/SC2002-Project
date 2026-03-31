package sc2002.turnbased.actions;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class UsePowerStoneSkillAction implements BattleAction {
    private final BattleAction delegatedSkillAction;

    public UsePowerStoneSkillAction(BattleAction delegatedSkillAction) {
        this.delegatedSkillAction = delegatedSkillAction;
    }

    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean advancesCooldown() {
        return false;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        context.getInventory().use(ItemType.POWER_STONE);

        List<BattleEvent> events = new ArrayList<>();
        String intro = actor.getName() + " -> Item -> Power Stone used -> " + delegatedSkillAction.getName() + " triggered";
        if (target != null) {
            intro += " on " + target.getName();
        }
        events.add(new NarrationEvent(intro));
        events.addAll(delegatedSkillAction.execute(context, actor, target));
        return events;
    }
}
