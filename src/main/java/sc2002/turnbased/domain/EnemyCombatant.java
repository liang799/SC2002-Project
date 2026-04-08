package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public abstract class EnemyCombatant extends Combatant {
    private final BattleAction basicAttackAction;

    protected EnemyCombatant(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        BattleAction basicAttackAction
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
        this.basicAttackAction = basicAttackAction;
    }

    public BattleAction selectAction(ActionExecutionContext context) {
        return basicAttackAction;
    }
}
