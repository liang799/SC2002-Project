package sc2002.turnbased.domain;

import java.util.List;

public class DefendStatusEffect implements StatusEffect {
    private int roundsRemaining;

    public DefendStatusEffect(int roundsRemaining) {
        this.roundsRemaining = roundsRemaining;
    }

    @Override
    public String getName() {
        return "DEFENDING";
    }

    @Override
    public TurnEffectResolution onTurnOpportunity() {
        return new TurnEffectResolution(false, null, List.of());
    }

    @Override
    public int getDefenseModifier() {
        return roundsRemaining > 0 ? 10 : 0;
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
