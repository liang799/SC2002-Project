package sc2002.turnbased.domain.status.event;

public record SmokeBombActivatedEvent(
    String ownerName,
    String attackerName,
    int protectedEnemyAttacksRemaining
) implements StatusEffectEvent {
}
