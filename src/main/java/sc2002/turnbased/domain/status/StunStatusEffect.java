package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Optional;

import sc2002.turnbased.domain.Combatant;

public class StunStatusEffect implements StatusEffect {
    private int blockedTurnsRemaining;

    public StunStatusEffect(int blockedTurnsRemaining) {
        if (blockedTurnsRemaining <= 0) {
            throw new IllegalArgumentException("blockedTurnsRemaining must be positive");
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
    public List<StatusEffectOutcome> onApply(Combatant owner) {
        return List.of(StatusEffectChange.applied(kind(), 0, blockedTurnsRemaining));
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
    public List<StatusEffectOutcome> onExpire(Combatant owner) {
        return List.of(StatusEffectChange.expired(kind()));
    }

    @Override
    public boolean isExpired() {
        return blockedTurnsRemaining == 0;
    }
}
