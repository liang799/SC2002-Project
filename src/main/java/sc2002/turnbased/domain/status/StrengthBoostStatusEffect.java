package sc2002.turnbased.domain.status;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.StatType;

public class StrengthBoostStatusEffect implements StatusEffect {
    private final int attackBonus;
    private int roundsRemaining;

    /** Creates an active strength boost; roundsRemaining must start positive. */
    public StrengthBoostStatusEffect(int attackBonus, int roundsRemaining) {
        if (attackBonus <= 0) {
            throw new IllegalArgumentException("attackBonus must be positive");
        }
        if (roundsRemaining <= 0) {
            throw new IllegalArgumentException("roundsRemaining must be positive");
        }
        this.attackBonus = attackBonus;
        this.roundsRemaining = roundsRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.STRENGTH_BOOST;
    }

    @Override
    public String description() {
        return "STRENGTH BOOST +" + attackBonus;
    }

    @Override
    public List<StatusEffectOutcome> onApply(Combatant owner) {
        return List.of(StatusEffectChange.applied(kind(), attackBonus, roundsRemaining));
    }

    @Override
    public CombatStats modifyStats(CombatStats stats) {
        if (isExpired()) {
            return stats;
        }
        return stats.addFlat(StatType.ATTACK, attackBonus);
    }

    @Override
    public List<StatusEffectOutcome> onRoundEnd(Combatant owner) {
        if (roundsRemaining > 0) {
            roundsRemaining--;
        }
        return List.of();
    }

    @Override
    public List<StatusEffectOutcome> onExpire(Combatant owner) {
        return List.of(StatusEffectChange.expired(kind()));
    }

    @Override
    public boolean isExpired() {
        return roundsRemaining == 0;
    }
}
