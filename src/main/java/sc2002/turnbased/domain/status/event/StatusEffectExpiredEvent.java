package sc2002.turnbased.domain.status.event;

import sc2002.turnbased.domain.status.StatusEffectKind;

public record StatusEffectExpiredEvent(String ownerName, StatusEffectKind effectKind) implements StatusEffectEvent {
}
