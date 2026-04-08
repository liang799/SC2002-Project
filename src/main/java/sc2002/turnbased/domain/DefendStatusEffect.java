package sc2002.turnbased.domain;

public class DefendStatusEffect implements StatusEffect, StatModifierEffect {
    private int roundsRemaining;

    public DefendStatusEffect(int roundsRemaining) {
        this.roundsRemaining = roundsRemaining;
    }

    @Override
    public String name() {
        return "DEFENDING";
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
