package sc2002.turnbased.actions;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class UsePowerStoneSkillAction implements BattleAction {
    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean advancesCooldown() {
        return false;
    }

    @Override
    public TargetingMode targetingMode(Combatant actor) {
        if (actor instanceof PlayerCharacter player) {
            return player.createSpecialSkillAction(false).targetingMode(actor);
        }
        return TargetingMode.NONE;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        context.getInventory().use(ItemType.POWER_STONE);
        if (!(actor instanceof PlayerCharacter player)) {
            throw new IllegalStateException("Power Stone is only supported for player characters");
        }
        BattleAction specialSkillAction = player.createSpecialSkillAction(false);

        List<BattleEvent> events = new ArrayList<>();
        String intro = actor.getName() + " -> Item -> Power Stone used -> " + specialSkillAction.getName() + " triggered";
        if (target != null) {
            intro += " on " + target.getName();
        }
        events.add(new NarrationEvent(intro));
        events.addAll(specialSkillAction.execute(context, actor, target));
        return events;
    }
}
