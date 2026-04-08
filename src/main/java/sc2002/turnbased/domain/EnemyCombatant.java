package sc2002.turnbased.domain;

import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public class EnemyCombatant extends Combatant {
    private final BattleAction basicAttackAction;

    public EnemyCombatant(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        BattleAction basicAttackAction
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
        this.basicAttackAction = Objects.requireNonNull(basicAttackAction, "basicAttackAction");
    }

    public BattleAction selectAction(ActionExecutionContext context) {
        return basicAttackAction;
    }
}
