package sc2002.turnbased.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.ScenarioScript;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.ui.BattleConsoleFormatter;

public final class BattleTestSupport {
    private BattleTestSupport() {
    }

    public static ScenarioRun runScenario(ScenarioScript scenario) {
        BattleEngine battleEngine = new BattleEngine(scenario.getBattleSetup(), new SpeedTurnOrderStrategy());
        List<BattleEvent> events = battleEngine.runRounds(scenario.getRoundCount(), scenario.getDecisionProvider());
        return new ScenarioRun(events, summariesFrom(events), String.join("\n", new BattleConsoleFormatter().format(events)));
    }

    public static Map<Integer, RoundCapture> captureRounds(List<BattleEvent> events) {
        Map<Integer, List<BattleEvent>> eventsByRound = new LinkedHashMap<>();
        Map<Integer, RoundSummaryEvent> summariesByRound = new LinkedHashMap<>();
        int currentRound = 0;

        for (BattleEvent event : events) {
            if (event instanceof RoundStartEvent roundStartEvent) {
                currentRound = roundStartEvent.getRoundNumber();
                eventsByRound.putIfAbsent(currentRound, new ArrayList<>());
                continue;
            }
            if (currentRound > 0) {
                eventsByRound.computeIfAbsent(currentRound, ignored -> new ArrayList<>()).add(event);
            }
            if (event instanceof RoundSummaryEvent roundSummaryEvent) {
                summariesByRound.put(roundSummaryEvent.getRoundNumber(), roundSummaryEvent);
            }
        }

        Map<Integer, RoundCapture> rounds = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<BattleEvent>> entry : eventsByRound.entrySet()) {
            rounds.put(entry.getKey(), new RoundCapture(List.copyOf(entry.getValue()), summariesByRound.get(entry.getKey())));
        }
        return rounds;
    }

    public static CombatantSummary findEnemy(RoundSummaryEvent summary, String enemyName) {
        return summary.getEnemySummaries().stream()
            .filter(enemy -> enemy.getName().equals(enemyName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Enemy not found in summary: " + enemyName));
    }

    private static List<RoundSummaryEvent> summariesFrom(List<BattleEvent> events) {
        return events.stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .toList();
    }

    public record ScenarioRun(List<BattleEvent> events, List<RoundSummaryEvent> summaries, String formattedOutput) {
        public ScenarioRun {
            events = List.copyOf(events);
            summaries = List.copyOf(summaries);
        }
    }

    public record RoundCapture(List<BattleEvent> events, RoundSummaryEvent summary) {
    }
}
