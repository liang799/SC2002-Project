package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class UsePotionAction implements BattleAction {
    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        context.getInventory().use(ItemType.POTION);
        int hpBefore = actor.getCurrentHp();
        actor.heal(100);
        return List.of(new NarrationEvent(
            actor.getName() + " -> Item -> Potion used: HP: " + hpBefore + " -> " + actor.getCurrentHp() + " (+100)"
        ));
    }
}
