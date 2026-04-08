package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public class EnemyCombatant extends Combatant {
    private final BattleAction attackAction;

    public EnemyCombatant(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        BattleAction attackAction
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
        this.attackAction = Objects.requireNonNull(attackAction, "attackAction");
    }

    public List<BattleEvent> attackPlayer(ActionExecutionContext context, PlayerCharacter player) {
        Objects.requireNonNull(context, "context");
        return attackAction.execute(context, this, Objects.requireNonNull(player, "player"));
    }
}
