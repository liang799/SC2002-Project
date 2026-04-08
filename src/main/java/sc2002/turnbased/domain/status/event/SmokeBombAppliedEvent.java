package sc2002.turnbased.domain.status.event;

public record SmokeBombAppliedEvent(String ownerName, int protectedEnemyAttacks) implements StatusEffectEvent {
}
