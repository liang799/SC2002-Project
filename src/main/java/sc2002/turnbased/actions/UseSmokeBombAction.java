package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.status.SmokeBombStatusEffect;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

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
        actor.getInventory().use(ItemType.SMOKE_BOMB);
        return List.of(
            new NarrationEvent(actor.getName() + " -> Item -> Smoke Bomb used"),
            StatusEffectReportEvent.fromStatusEffectOutcomes(actor.addStatusEffect(new SmokeBombStatusEffect(2)))
        );
    }
}
