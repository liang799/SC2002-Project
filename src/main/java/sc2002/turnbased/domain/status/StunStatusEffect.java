package sc2002.turnbased.domain.status;

import java.util.ArrayList;
import java.util.List;

public class StunStatusEffect implements StatusEffect, TurnInterferingEffect {
    private int blockedTurnsRemaining;

    public StunStatusEffect(int blockedTurnsRemaining) {
        this.blockedTurnsRemaining = blockedTurnsRemaining;
    }

    @Override
    public String name() {
        return "STUNNED";
    }

    @Override
    public TurnEffectResolution onTurnOpportunity() {
        boolean blocksAction = blockedTurnsRemaining > 0;
        List<String> notes = new ArrayList<>();
        if (blocksAction) {
            blockedTurnsRemaining--;
            if (blockedTurnsRemaining == 0) {
                notes.add("Stun expires");
            }
        }
        return new TurnEffectResolution(blocksAction, name(), notes);
    }

    @Override
    public boolean isExpired() {
        return blockedTurnsRemaining == 0;
    }
}
