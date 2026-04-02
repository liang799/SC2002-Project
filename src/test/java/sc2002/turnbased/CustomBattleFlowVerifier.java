package sc2002.turnbased;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.actions.TargetingMode;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;

public class CustomBattleFlowVerifier {
    public static void main(String[] args) {
        VerificationReport report = runValidation();
        if (report.mismatches().isEmpty()) {
            System.out.println("Custom battle flow validation PASSED");
            System.out.println("All 13 rounds matched expected results");
            if (!report.notes().isEmpty()) {
                for (String note : report.notes()) {
                    System.out.println("Note: " + note);
                }
            }
            return;
        }

        System.out.println("Custom battle flow validation FAILED");
        for (String mismatch : report.mismatches()) {
            System.out.println("- " + mismatch);
        }
        if (!report.notes().isEmpty()) {
            for (String note : report.notes()) {
                System.out.println("Note: " + note);
            }
        }
        throw new AssertionError("Custom battle flow validation failed");
    }

    private static VerificationReport runValidation() {
        BattleSetup battleSetup = new BattleSetupFactory().create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB)
            )
        );
        CustomFlowDecisionProvider decisionProvider = new CustomFlowDecisionProvider();
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        List<BattleEvent> events = battleEngine.runRounds(13, decisionProvider);

        Map<Integer, RoundCapture> rounds = captureRounds(events);
        List<String> mismatches = new ArrayList<>();
        List<String> notes = new ArrayList<>();

        assertRound(mismatches, rounds.get(1), 220, 3, 1, 1, Set.of(), enemyStates(
            enemyState("Goblin", 55, true, Set.of()),
            enemyState("Wolf", 5, true, Set.of("STUNNED"))
        ));
        assertRound(mismatches, rounds.get(2), 220, 2, 1, 0, Set.of(), enemyStates(
            enemyState("Goblin", 55, true, Set.of()),
            enemyState("Wolf", 5, true, Set.of("STUNNED"))
        ));
        assertRound(mismatches, rounds.get(3), 220, 1, 1, 0, Set.of(), enemyStates(
            enemyState("Goblin", 55, true, Set.of()),
            enemyState("Wolf", 0, false, Set.of())
        ));
        assertRound(mismatches, rounds.get(4), 215, 0, 1, 0, Set.of("DEFENDING"), enemyStates(
            enemyState("Goblin", 55, true, Set.of()),
            enemyState("Wolf", 0, false, Set.of())
        ));
        assertRound(mismatches, rounds.get(5), 215, 3, 1, 0, Set.of("DEFENDING"), enemyStates(
            enemyState("Goblin", 30, true, Set.of("STUNNED")),
            enemyState("Wolf", 0, false, Set.of())
        ));
        assertRound(mismatches, rounds.get(6), 215, 2, 1, 0, Set.of(), enemyStates(
            enemyState("Goblin", 5, true, Set.of()),
            enemyState("Wolf", 0, false, Set.of())
        ));
        assertRound(mismatches, rounds.get(7), 245, 1, 0, 0, Set.of(), enemyStates(
            enemyState("Goblin", 5, true, Set.of()),
            enemyState("Wolf", 0, false, Set.of())
        ));
        assertRound(mismatches, rounds.get(8), 245, 0, 0, 0, Set.of(), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 40, true, Set.of()),
            enemyState("Wolf B", 40, true, Set.of())
        ));
        assertRound(mismatches, rounds.get(9), 195, 0, 0, 0, Set.of("DEFENDING"), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 40, true, Set.of()),
            enemyState("Wolf B", 40, true, Set.of())
        ));
        assertRound(mismatches, rounds.get(10), 165, 3, 0, 0, Set.of("DEFENDING"), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 5, true, Set.of("STUNNED")),
            enemyState("Wolf B", 40, true, Set.of())
        ));
        assertRound(mismatches, rounds.get(11), 140, 2, 0, 0, Set.of(), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 0, false, Set.of()),
            enemyState("Wolf B", 40, true, Set.of())
        ));
        assertRound(mismatches, rounds.get(12), 115, 1, 0, 0, Set.of(), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 0, false, Set.of()),
            enemyState("Wolf B", 5, true, Set.of())
        ));
        assertRound(mismatches, rounds.get(13), 90, 0, 0, 0, Set.of(), enemyStates(
            enemyState("Goblin", 0, false, Set.of()),
            enemyState("Wolf", 0, false, Set.of()),
            enemyState("Wolf A", 0, false, Set.of()),
            enemyState("Wolf B", 0, false, Set.of())
        ));

        assertAction(mismatches, rounds.get(2), "Goblin", "Warrior", 0, 20, Set.of("Smoke Bomb active"), "Round 2 smoke bomb protection mismatch");
        assertAction(mismatches, rounds.get(3), "Goblin", "Warrior", 0, 20, Set.of("Smoke Bomb active", "Smoke Bomb effect expires"), "Round 3 smoke bomb expiry mismatch");
        assertAction(mismatches, rounds.get(4), "Goblin", "Warrior", 5, 30, Set.of(), "Round 4 defend damage mismatch");
        assertAction(mismatches, rounds.get(10), "Wolf A", "Warrior", 15, 30, Set.of(), "Round 10 Wolf A defend damage mismatch");
        assertAction(mismatches, rounds.get(10), "Wolf B", "Warrior", 15, 30, Set.of(), "Round 10 Wolf B defend damage mismatch");

        assertSkipped(mismatches, rounds.get(2), "Wolf", "STUNNED", Set.of(), "Round 2 wolf stun skip mismatch");
        assertSkipped(mismatches, rounds.get(3), "Wolf", "STUNNED", Set.of("Stun expires"), "Round 3 wolf stun expiry mismatch");
        assertSkipped(mismatches, rounds.get(5), "Goblin", "STUNNED", Set.of(), "Round 5 goblin stun skip mismatch");
        assertSkipped(mismatches, rounds.get(6), "Goblin", "STUNNED", Set.of("Stun expires"), "Round 6 goblin stun expiry mismatch");

        assertNarrationContains(mismatches, rounds.get(8), "Backup Spawn triggered: Wolf A, Wolf B", "Round 8 backup spawn should occur after initial wave is defeated");
        assertVictoryNarration(mismatches, events);

        if (!decisionProvider.getCooldownBlockedRounds().equals(Set.of(12))) {
            mismatches.add("Cooldown fallback mismatch: expected blocked special-skill attempt only in round 12, actual blocked rounds "
                + decisionProvider.getCooldownBlockedRounds());
        } else {
            notes.add("Round 12 special-skill attempt was blocked by the provider while Warrior cooldown was still active before turn start, then it fell back to BasicAttack and the normal turn-start decrement produced the expected round-end cooldown of 1.");
        }

        if (!decisionProvider.getFallbackDescriptions().isEmpty()) {
            notes.addAll(decisionProvider.getFallbackDescriptions());
        }

        return new VerificationReport(mismatches, notes);
    }

    private static Map<Integer, RoundCapture> captureRounds(List<BattleEvent> events) {
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
            rounds.put(entry.getKey(), new RoundCapture(entry.getValue(), summariesByRound.get(entry.getKey())));
        }
        return rounds;
    }

    private static void assertRound(
        List<String> mismatches,
        RoundCapture roundCapture,
        int expectedWarriorHp,
        int expectedCooldown,
        int expectedPotionCount,
        int expectedSmokeBombCount,
        Set<String> expectedPlayerStatuses,
        Map<String, ExpectedEnemyState> expectedEnemies
    ) {
        if (roundCapture == null || roundCapture.summary() == null) {
            mismatches.add("Missing round summary for expected round");
            return;
        }

        RoundSummaryEvent summary = roundCapture.summary();
        CombatantSummary playerSummary = summary.getPlayerSummary();

        if (playerSummary.getCurrentHp() != expectedWarriorHp) {
            mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected Warrior HP " + expectedWarriorHp
                + ", actual " + playerSummary.getCurrentHp());
        }
        if (summary.getSpecialSkillCooldown() != expectedCooldown) {
            mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected cooldown " + expectedCooldown
                + ", actual " + summary.getSpecialSkillCooldown());
        }
        if (summary.getInventorySnapshot().getOrDefault(ItemType.POTION, 0) != expectedPotionCount) {
            mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected Potion count " + expectedPotionCount
                + ", actual " + summary.getInventorySnapshot().getOrDefault(ItemType.POTION, 0));
        }
        if (summary.getInventorySnapshot().getOrDefault(ItemType.SMOKE_BOMB, 0) != expectedSmokeBombCount) {
            mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected Smoke Bomb count " + expectedSmokeBombCount
                + ", actual " + summary.getInventorySnapshot().getOrDefault(ItemType.SMOKE_BOMB, 0));
        }
        if (!playerSummary.getActiveStatuses().containsAll(expectedPlayerStatuses)) {
            mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected Warrior statuses to include "
                + expectedPlayerStatuses + ", actual " + playerSummary.getActiveStatuses());
        }

        for (ExpectedEnemyState expectedEnemy : expectedEnemies.values()) {
            CombatantSummary actual = findEnemy(summary, expectedEnemy.name());
            if (actual == null) {
                mismatches.add("Round " + summary.getRoundNumber() + " mismatch: enemy " + expectedEnemy.name() + " missing from summary");
                continue;
            }
            if (actual.getCurrentHp() != expectedEnemy.hp()) {
                mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected " + expectedEnemy.name()
                    + " HP " + expectedEnemy.hp() + ", actual " + actual.getCurrentHp());
            }
            if (actual.isAlive() != expectedEnemy.alive()) {
                mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected " + expectedEnemy.name()
                    + " alive=" + expectedEnemy.alive() + ", actual " + actual.isAlive());
            }
            if (!actual.getActiveStatuses().containsAll(expectedEnemy.statuses())) {
                mismatches.add("Round " + summary.getRoundNumber() + " mismatch: expected " + expectedEnemy.name()
                    + " statuses to include " + expectedEnemy.statuses() + ", actual " + actual.getActiveStatuses());
            }
        }
    }

    private static void assertAction(
        List<String> mismatches,
        RoundCapture roundCapture,
        String actor,
        String target,
        int expectedDamage,
        int expectedDefense,
        Set<String> expectedNotes,
        String prefix
    ) {
        ActionEvent actionEvent = roundCapture.events().stream()
            .filter(ActionEvent.class::isInstance)
            .map(ActionEvent.class::cast)
            .filter(event -> event.getActorName().equals(actor) && event.getTargetName().equals(target))
            .findFirst()
            .orElse(null);

        if (actionEvent == null) {
            mismatches.add(prefix + ": matching action event not found");
            return;
        }
        if (actionEvent.getDamage() != expectedDamage) {
            mismatches.add(prefix + ": expected damage " + expectedDamage + ", actual " + actionEvent.getDamage());
        }
        if (actionEvent.getTargetDefense() != expectedDefense) {
            mismatches.add(prefix + ": expected target defense " + expectedDefense + ", actual " + actionEvent.getTargetDefense());
        }
        if (!actionEvent.getNotes().containsAll(expectedNotes)) {
            mismatches.add(prefix + ": expected notes to include " + expectedNotes + ", actual " + actionEvent.getNotes());
        }
    }

    private static void assertSkipped(
        List<String> mismatches,
        RoundCapture roundCapture,
        String combatantName,
        String expectedReason,
        Set<String> expectedNotes,
        String prefix
    ) {
        SkippedTurnEvent skippedTurnEvent = roundCapture.events().stream()
            .filter(SkippedTurnEvent.class::isInstance)
            .map(SkippedTurnEvent.class::cast)
            .filter(event -> event.getCombatantName().equals(combatantName))
            .findFirst()
            .orElse(null);

        if (skippedTurnEvent == null) {
            mismatches.add(prefix + ": skipped-turn event not found");
            return;
        }
        if (!skippedTurnEvent.getReason().equals(expectedReason)) {
            mismatches.add(prefix + ": expected reason " + expectedReason + ", actual " + skippedTurnEvent.getReason());
        }
        if (!skippedTurnEvent.getNotes().containsAll(expectedNotes)) {
            mismatches.add(prefix + ": expected notes to include " + expectedNotes + ", actual " + skippedTurnEvent.getNotes());
        }
    }

    private static void assertNarrationContains(List<String> mismatches, RoundCapture roundCapture, String expectedText, String prefix) {
        boolean found = roundCapture.events().stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch(expectedText::equals);
        if (!found) {
            mismatches.add(prefix + ": expected narration \"" + expectedText + "\" not found");
        }
    }

    private static void assertVictoryNarration(List<String> mismatches, List<BattleEvent> events) {
        boolean victoryFound = events.stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch("Victory:"::equals);
        if (!victoryFound) {
            mismatches.add("Final result mismatch: expected victory narration, but it was not emitted");
        }
    }

    private static CombatantSummary findEnemy(RoundSummaryEvent summary, String enemyName) {
        return summary.getEnemySummaries().stream()
            .filter(enemy -> enemy.getName().equals(enemyName))
            .findFirst()
            .orElse(null);
    }

    private static Map<String, ExpectedEnemyState> enemyStates(ExpectedEnemyState... states) {
        Map<String, ExpectedEnemyState> enemyStates = new LinkedHashMap<>();
        for (ExpectedEnemyState state : states) {
            enemyStates.put(state.name(), state);
        }
        return enemyStates;
    }

    private static ExpectedEnemyState enemyState(String name, int hp, boolean alive, Set<String> statuses) {
        return new ExpectedEnemyState(name, hp, alive, statuses);
    }

    private record RoundCapture(List<BattleEvent> events, RoundSummaryEvent summary) {
    }

    private record ExpectedEnemyState(String name, int hp, boolean alive, Set<String> statuses) {
    }

    private record VerificationReport(List<String> mismatches, List<String> notes) {
    }

    private static final class CustomFlowDecisionProvider implements PlayerDecisionProvider {
        private final Map<Integer, PlannedTurn> planByRound = buildPlan();
        private final Set<Integer> cooldownBlockedRounds = new java.util.LinkedHashSet<>();
        private final List<String> fallbackDescriptions = new ArrayList<>();

        @Override
        public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
            PlannedTurn plannedTurn = planByRound.get(roundNumber);
            if (plannedTurn == null) {
                throw new IllegalStateException("No planned turn configured for round " + roundNumber);
            }

            if (plannedTurn.primaryAction() instanceof UseSpecialSkillAction && player.getSpecialSkillCooldown() > 0) {
                cooldownBlockedRounds.add(roundNumber);
                if (plannedTurn.fallbackAction() == null) {
                    throw new IllegalStateException("Special skill was unavailable in round " + roundNumber + " and no fallback action was configured");
                }
                fallbackDescriptions.add("Round " + roundNumber + " fallback used: attempted SpecialSkill with cooldown "
                    + player.getSpecialSkillCooldown() + ", switched to " + plannedTurn.fallbackAction().getName() + ".");
                return toDecision(plannedTurn.fallbackAction(), plannedTurn.fallbackTarget(), player, livingEnemies);
            }

            return toDecision(plannedTurn.primaryAction(), plannedTurn.primaryTarget(), player, livingEnemies);
        }

        public Set<Integer> getCooldownBlockedRounds() {
            return cooldownBlockedRounds;
        }

        public List<String> getFallbackDescriptions() {
            return fallbackDescriptions;
        }

        private PlayerDecision toDecision(BattleAction action, String targetName, PlayerCharacter player, List<Combatant> livingEnemies) {
            if (action.targetingMode(player) == TargetingMode.NONE) {
                return PlayerDecision.untargeted(action);
            }

            boolean targetExists = livingEnemies.stream().anyMatch(enemy -> enemy.getName().equals(targetName));
            if (!targetExists) {
                throw new IllegalStateException("Target " + targetName + " was not available for action " + action.getName());
            }
            return PlayerDecision.targeted(action, targetName);
        }

        private Map<Integer, PlannedTurn> buildPlan() {
            Map<Integer, PlannedTurn> plan = new LinkedHashMap<>();
            plan.put(1, new PlannedTurn(new UseSpecialSkillAction(), "Wolf", null, null));
            plan.put(2, new PlannedTurn(new UseSmokeBombAction(), null, null, null));
            plan.put(3, new PlannedTurn(new BasicAttackAction(), "Wolf", null, null));
            plan.put(4, new PlannedTurn(new DefendAction(), null, null, null));
            plan.put(5, new PlannedTurn(new UseSpecialSkillAction(), "Goblin", null, null));
            plan.put(6, new PlannedTurn(new BasicAttackAction(), "Goblin", null, null));
            plan.put(7, new PlannedTurn(new UsePotionAction(), null, null, null));
            plan.put(8, new PlannedTurn(new BasicAttackAction(), "Goblin", null, null));
            plan.put(9, new PlannedTurn(new DefendAction(), null, null, null));
            plan.put(10, new PlannedTurn(new UseSpecialSkillAction(), "Wolf A", null, null));
            plan.put(11, new PlannedTurn(new BasicAttackAction(), "Wolf A", null, null));
            plan.put(12, new PlannedTurn(new UseSpecialSkillAction(), "Wolf B", new BasicAttackAction(), "Wolf B"));
            plan.put(13, new PlannedTurn(new BasicAttackAction(), "Wolf B", null, null));
            return plan;
        }
    }

    private record PlannedTurn(BattleAction primaryAction, String primaryTarget, BattleAction fallbackAction, String fallbackTarget) {
    }
}
