package sc2002.turnbased.domain.status.event;

public record ArcanePowerAppliedEvent(String ownerName, int totalAttackBonus) implements StatusEffectEvent {
}
