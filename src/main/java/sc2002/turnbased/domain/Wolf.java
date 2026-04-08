package sc2002.turnbased.domain;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public class Wolf extends EnemyCombatant {
    private static final HitPoints WOLF_HIT_POINTS = HitPoints.full(40);
    private static final CombatStats WOLF_STATS = CombatStats.builder()
        .attack(45)
        .defense(5)
        .speed(35)
        .build();

    public Wolf(String name, StatusEffectRegistry statusEffectRegistry, BattleAction basicAttackAction) {
        super(name, WOLF_HIT_POINTS, WOLF_STATS, statusEffectRegistry, basicAttackAction);
    }
}
