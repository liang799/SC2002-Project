package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.StatType;
import sc2002.turnbased.domain.status.event.DefendAppliedEvent;

public class DefendStatusEffect implements StatusEffect, StatModifierEffect {
    private int roundsRemaining;

    public DefendStatusEffect(int roundsRemaining) {
        this.roundsRemaining = roundsRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.DEFEND;
    }

    @Override
    public String name() {
        return "DEFENDING";
    }

    @Override
    public void onRegistered(String ownerName, StatusEffectEventPublisher eventPublisher) {
        eventPublisher.publish(new DefendAppliedEvent(ownerName, 10, roundsRemaining));
    }

    @Override
    public CombatStats modifyStats(CombatStats stats) {
        if (roundsRemaining > 0) {
            return stats.addFlat(StatType.DEFENSE, 10);
        }
        return stats;
    }

    @Override
    public void onRoundCompleted() {
        if (roundsRemaining > 0) {
            roundsRemaining--;
        }
    }

    @Override
    public boolean isExpired() {
        return roundsRemaining == 0;
    }
}
