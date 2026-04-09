package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public class EnemyCombatant extends Combatant {
    private final BattleAction turnAction;

    public EnemyCombatant(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        BattleAction turnAction
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
        this.turnAction = Objects.requireNonNull(turnAction, "turnAction");
    }

    public List<BattleEvent> takeTurn(ActionExecutionContext context, Combatant target) {
        Objects.requireNonNull(context, "context");
        return turnAction.execute(context, this, Objects.requireNonNull(target, "target"));
    }
}
