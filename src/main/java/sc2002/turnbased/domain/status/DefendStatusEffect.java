package sc2002.turnbased.domain.status;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.StatType;

public class DefendStatusEffect implements StatusEffect {
    private static final int DEFENSE_BONUS = 10;
    private int roundsRemaining;

    public DefendStatusEffect(int roundsRemaining) {
        if (roundsRemaining < 0) {
            throw new IllegalArgumentException("roundsRemaining must not be negative");
        }
        this.roundsRemaining = roundsRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.DEFEND;
    }

    @Override
    public String description() {
        return "DEFENDING";
    }

    @Override
    public List<StatusEffectOutcome> onApply(Combatant owner) {
        return List.of(StatusEffectChange.applied(kind(), DEFENSE_BONUS, roundsRemaining));
    }

    @Override
    public CombatStats modifyStats(CombatStats stats) {
        if (isExpired()) {
            return stats;
        }
        return stats.addFlat(StatType.DEFENSE, DEFENSE_BONUS);
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
