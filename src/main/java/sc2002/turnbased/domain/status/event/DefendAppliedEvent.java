package sc2002.turnbased.domain.status.event;

public record DefendAppliedEvent(String ownerName, int defenseBonus, int roundsRemaining) implements StatusEffectEvent {
}
