package sc2002.turnbased.engine;

import java.util.Objects;

public class ScenarioScript {
    private final String name;
    private final BattleSetup battleSetup;
    private final PlayerDecisionProvider decisionProvider;
    private final int roundCount;

    public ScenarioScript(String name, BattleSetup battleSetup, PlayerDecisionProvider decisionProvider, int roundCount) {
        this.name = Objects.requireNonNull(name, "name");
        this.battleSetup = Objects.requireNonNull(battleSetup, "battleSetup");
        this.decisionProvider = Objects.requireNonNull(decisionProvider, "decisionProvider");
        this.roundCount = roundCount;
    }

    public String getName() {
        return name;
    }

    public BattleSetup getBattleSetup() {
        return battleSetup;
    }

    public PlayerDecisionProvider getDecisionProvider() {
        return decisionProvider;
    }

    public int getRoundCount() {
        return roundCount;
    }
}
