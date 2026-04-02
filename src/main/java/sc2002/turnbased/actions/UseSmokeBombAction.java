package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.SmokeBombStatusEffect;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class UseSmokeBombAction implements BattleAction {
    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public TargetingMode targetingMode(Combatant actor) {
        return TargetingMode.NONE;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        context.getInventory().use(ItemType.SMOKE_BOMB);
        actor.addStatusEffect(new SmokeBombStatusEffect(2));
        return List.of(new NarrationEvent(
            actor.getName() + " -> Item -> Smoke Bomb used: enemy attacks deal 0 damage for the next 2 enemy attacks"
        ));
    }
}
