package sc2002.turnbased.domain;

import sc2002.turnbased.domain.status.StatusEffectRegistry;

@FunctionalInterface
public interface PlayerCharacterCreator {
    PlayerCharacter create(StatusEffectRegistry statusEffectRegistry);
}
