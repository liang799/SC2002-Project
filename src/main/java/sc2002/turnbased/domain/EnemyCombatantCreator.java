package sc2002.turnbased.domain;

import sc2002.turnbased.domain.status.StatusEffectRegistry;

@FunctionalInterface
public interface EnemyCombatantCreator {
    EnemyCombatant create(String name, StatusEffectRegistry statusEffectRegistry);
}
