package sc2002.turnbased.ui;

import java.util.List;

import sc2002.turnbased.engine.AppendixAScenarios;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.ScenarioScript;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.BattleEvent;

public class EasyRoundsDemo {
    public static void main(String[] args) {
        BattleConsoleFormatter formatter = new BattleConsoleFormatter();
        for (ScenarioScript scenario : AppendixAScenarios.all()) {
            BattleEngine battleEngine = new BattleEngine(scenario.getBattleSetup(), new SpeedTurnOrderStrategy());
            List<BattleEvent> events = battleEngine.runRounds(scenario.getRoundCount(), scenario.getDecisionProvider());

            System.out.println("=== " + scenario.getName() + " ===");
            for (String line : formatter.format(events)) {
                System.out.println(line);
            }
            System.out.println();
        }
    }
}
