package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.BattleAction;

public abstract class EnemyCombatant extends Combatant {
    private static final BattleAction BASIC_ATTACK = new BasicAttackAction();

    protected EnemyCombatant(String name, HitPoints baseHitPoints, CombatStats baseStats) {
        super(name, baseHitPoints, baseStats);
    }

    public BattleAction selectAction(ActionExecutionContext context) {
        return BASIC_ATTACK;
    }
}
