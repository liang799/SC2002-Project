package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Optional;

import sc2002.turnbased.domain.Combatant;

public class StunStatusEffect implements StatusEffect {
    private int blockedTurnsRemaining;

    public StunStatusEffect(int blockedTurnsRemaining) {
        if (blockedTurnsRemaining < 0) {
            throw new IllegalArgumentException("blockedTurnsRemaining must not be negative");
        }
        this.blockedTurnsRemaining = blockedTurnsRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.STUN;
    }

    @Override
    public String description() {
        return "STUNNED";
    }

    @Override
    public List<String> onApply(Combatant owner) {
        return List.of(owner.getName() + " STUNNED (" + blockedTurnsRemaining + " turns)");
    }

    @Override
    public Optional<String> getTurnBlockReason(Combatant owner) {
        if (blockedTurnsRemaining == 0) {
            return Optional.empty();
        }

        blockedTurnsRemaining--;
        return Optional.of(description());
    }

    @Override
    public List<String> onExpire(Combatant owner) {
        return List.of("Stun expired");
    }

    @Override
    public boolean isExpired() {
        return blockedTurnsRemaining == 0;
    }
}
