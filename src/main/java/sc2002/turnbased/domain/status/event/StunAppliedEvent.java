package sc2002.turnbased.domain.status.event;

public record StunAppliedEvent(String ownerName, int blockedTurnsRemaining) implements StatusEffectEvent {
}
