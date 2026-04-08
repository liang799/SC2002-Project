package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.event.StunAppliedEvent;

public class StunStatusEffect implements StatusEffect, TurnInterferingEffect {
    private int blockedTurnsRemaining;

    public StunStatusEffect(int blockedTurnsRemaining) {
        this.blockedTurnsRemaining = blockedTurnsRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.STUN;
    }

    @Override
    public String name() {
        return "STUNNED";
    }

    @Override
    public void onRegistered(String ownerName, StatusEffectEventPublisher eventPublisher) {
        eventPublisher.publish(new StunAppliedEvent(ownerName, blockedTurnsRemaining));
    }

    @Override
    public TurnEffectResolution onTurnOpportunity(Combatant owner, StatusEffectEventPublisher eventPublisher) {
        boolean blocksAction = blockedTurnsRemaining > 0;
        if (!blocksAction) {
            return TurnEffectResolution.allow();
        }

        blockedTurnsRemaining--;
        return new TurnEffectResolution(blocksAction, name());
    }

    @Override
    public boolean isExpired() {
        return blockedTurnsRemaining == 0;
    }
}
