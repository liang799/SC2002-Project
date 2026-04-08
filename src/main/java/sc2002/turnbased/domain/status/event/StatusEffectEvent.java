package sc2002.turnbased.domain.status.event;

public sealed interface StatusEffectEvent permits ArcanePowerAppliedEvent, DefendAppliedEvent, SmokeBombActivatedEvent,
    SmokeBombAppliedEvent, StatusEffectExpiredEvent, StunAppliedEvent {
    String ownerName();
}
