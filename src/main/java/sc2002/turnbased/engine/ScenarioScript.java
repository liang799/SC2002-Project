package sc2002.turnbased.engine;

public class ScenarioScript {
    private final String name;
    private final BattleSetup battleSetup;
    private final PlayerDecisionProvider decisionProvider;
    private final int roundCount;

    public ScenarioScript(String name, BattleSetup battleSetup, PlayerDecisionProvider decisionProvider, int roundCount) {
        this.name = name;
        this.battleSetup = battleSetup;
        this.decisionProvider = decisionProvider;
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
