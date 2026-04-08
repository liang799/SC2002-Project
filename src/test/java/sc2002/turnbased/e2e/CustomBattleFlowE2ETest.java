package sc2002.turnbased.e2e;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.domain.status.event.StatusEffectEvent;
import sc2002.turnbased.domain.status.event.StatusEffectExpiredEvent;
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
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.SkippedTurnEvent;
import sc2002.turnbased.support.BattleTestSupport;
import sc2002.turnbased.support.BattleTestSupport.RoundCapture;
import sc2002.turnbased.support.TestDependencies;

@Tag("e2e")
class CustomBattleFlowE2ETest {
    @Test
    @DisplayName("Medium warrior battle flow matches the expected end-to-end transcript")
    void mediumWarriorBattleFlowMatchesExpectedTranscript() {
        BattleSetup battleSetup = TestDependencies.battleSetupFactory().create(
            new GameConfiguration(
                PlayerType.WARRIOR,
                DifficultyLevel.MEDIUM,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB)
            )
        );
        CustomFlowDecisionProvider decisionProvider = new CustomFlowDecisionProvider();
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        List<BattleEvent> events = battleEngine.runRounds(13, decisionProvider);

        Map<Integer, RoundCapture> rounds = BattleTestSupport.captureRounds(events);

        assertRound(rounds.get(1), 220, 3, 1, 1, Set.of(),
            enemy("Goblin", 55, true, Set.of()),
            enemy("Wolf", 5, true, Set.of("STUNNED"))
        );
        assertRound(rounds.get(2), 220, 2, 1, 0, Set.of(),
            enemy("Goblin", 55, true, Set.of()),
            enemy("Wolf", 5, true, Set.of("STUNNED"))
        );
        assertRound(rounds.get(3), 220, 1, 1, 0, Set.of(),
            enemy("Goblin", 55, true, Set.of()),
            enemy("Wolf", 0, false, Set.of())
        );
        assertRound(rounds.get(4), 215, 0, 1, 0, Set.of("DEFENDING"),
            enemy("Goblin", 55, true, Set.of()),
            enemy("Wolf", 0, false, Set.of())
        );
        assertRound(rounds.get(5), 215, 3, 1, 0, Set.of("DEFENDING"),
            enemy("Goblin", 30, true, Set.of("STUNNED")),
            enemy("Wolf", 0, false, Set.of())
        );
        assertRound(rounds.get(6), 215, 2, 1, 0, Set.of(),
            enemy("Goblin", 5, true, Set.of()),
            enemy("Wolf", 0, false, Set.of())
        );
        assertRound(rounds.get(7), 245, 1, 0, 0, Set.of(),
            enemy("Goblin", 5, true, Set.of()),
            enemy("Wolf", 0, false, Set.of())
        );
        assertRound(rounds.get(8), 245, 0, 0, 0, Set.of(),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 40, true, Set.of()),
            enemy("Wolf B", 40, true, Set.of())
        );
        assertRound(rounds.get(9), 195, 0, 0, 0, Set.of("DEFENDING"),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 40, true, Set.of()),
            enemy("Wolf B", 40, true, Set.of())
        );
        assertRound(rounds.get(10), 165, 3, 0, 0, Set.of("DEFENDING"),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 5, true, Set.of("STUNNED")),
            enemy("Wolf B", 40, true, Set.of())
        );
        assertRound(rounds.get(11), 140, 2, 0, 0, Set.of(),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 0, false, Set.of()),
            enemy("Wolf B", 40, true, Set.of())
        );
        assertRound(rounds.get(12), 115, 1, 0, 0, Set.of(),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 0, false, Set.of()),
            enemy("Wolf B", 5, true, Set.of())
        );
        assertRound(rounds.get(13), 90, 0, 0, 0, Set.of(),
            enemy("Goblin", 0, false, Set.of()),
            enemy("Wolf", 0, false, Set.of()),
            enemy("Wolf A", 0, false, Set.of()),
            enemy("Wolf B", 0, false, Set.of())
        );

        assertAction(rounds.get(2), "Goblin", "Warrior", 0, 20,
            List.of(new SmokeBombActivatedEvent("Warrior", "Goblin", 1)));
        assertAction(rounds.get(3), "Goblin", "Warrior", 0, 20,
            List.of(
                new SmokeBombActivatedEvent("Warrior", "Goblin", 0),
                new StatusEffectExpiredEvent("Warrior", StatusEffectKind.SMOKE_BOMB)
            ));
        assertAction(rounds.get(4), "Goblin", "Warrior", 5, 30, List.of());
        assertAction(rounds.get(10), "Wolf A", "Warrior", 15, 30, List.of());
        assertAction(rounds.get(10), "Wolf B", "Warrior", 15, 30, List.of());

        assertSkipped(rounds.get(2), "Wolf", "STUNNED", List.of());
        assertSkipped(rounds.get(3), "Wolf", "STUNNED",
            List.of(new StatusEffectExpiredEvent("Wolf", StatusEffectKind.STUN)));
        assertSkipped(rounds.get(5), "Goblin", "STUNNED", List.of());
        assertSkipped(rounds.get(6), "Goblin", "STUNNED",
            List.of(new StatusEffectExpiredEvent("Goblin", StatusEffectKind.STUN)));

        assertNarrationContains(rounds.get(8), "Backup Spawn triggered: Wolf A, Wolf B");
        assertVictoryNarration(events);

        assertEquals(Set.of(12), decisionProvider.getCooldownBlockedRounds());
        assertEquals(
            List.of("Round 12 fallback used: attempted SpecialSkill with cooldown 2, switched to BasicAttack."),
            decisionProvider.getFallbackDescriptions()
        );
    }

    private static void assertRound(
        RoundCapture roundCapture,
        int expectedWarriorHp,
        int expectedCooldown,
        int expectedPotionCount,
        int expectedSmokeBombCount,
        Set<String> expectedPlayerStatuses,
        ExpectedEnemyState... expectedEnemies
    ) {
        assertNotNull(roundCapture, "Missing expected round capture");
        RoundSummaryEvent summary = roundCapture.summary();
        assertNotNull(summary, "Missing round summary");
        CombatantSummary playerSummary = summary.getPlayerSummary();

        assertAll(
            () -> assertEquals(expectedWarriorHp, playerSummary.getCurrentHp(), "Unexpected Warrior HP in round " + summary.getRoundNumber()),
            () -> assertEquals(expectedCooldown, summary.getSpecialSkillCooldown(), "Unexpected cooldown in round " + summary.getRoundNumber()),
            () -> assertEquals(expectedPotionCount, summary.getInventorySnapshot().getOrDefault(ItemType.POTION, 0), "Unexpected Potion count in round " + summary.getRoundNumber()),
            () -> assertEquals(expectedSmokeBombCount, summary.getInventorySnapshot().getOrDefault(ItemType.SMOKE_BOMB, 0), "Unexpected Smoke Bomb count in round " + summary.getRoundNumber()),
            () -> assertTrue(playerSummary.getActiveStatuses().containsAll(expectedPlayerStatuses), "Unexpected Warrior statuses in round " + summary.getRoundNumber())
        );

        for (ExpectedEnemyState expectedEnemy : expectedEnemies) {
            CombatantSummary actual = BattleTestSupport.findEnemy(summary, expectedEnemy.name());
            assertAll(
                () -> assertEquals(expectedEnemy.hp(), actual.getCurrentHp(), "Unexpected HP for " + expectedEnemy.name() + " in round " + summary.getRoundNumber()),
                () -> assertEquals(expectedEnemy.alive(), actual.isAlive(), "Unexpected alive state for " + expectedEnemy.name() + " in round " + summary.getRoundNumber()),
                () -> assertTrue(actual.getActiveStatuses().containsAll(expectedEnemy.statuses()), "Unexpected statuses for " + expectedEnemy.name() + " in round " + summary.getRoundNumber())
            );
        }
    }

    private static void assertAction(
        RoundCapture roundCapture,
        String actor,
        String target,
        int expectedDamage,
        int expectedDefense,
        List<StatusEffectEvent> expectedStatusEffectEvents
    ) {
        assertNotNull(roundCapture, "Missing expected round capture");

        ActionEvent actionEvent = roundCapture.events().stream()
            .filter(ActionEvent.class::isInstance)
            .map(ActionEvent.class::cast)
            .filter(event -> event.getActorName().equals(actor) && event.getTargetName().equals(target))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Matching action event not found for " + actor + " -> " + target));

        assertAll(
            () -> assertEquals(expectedDamage, actionEvent.getDamage(), "Unexpected damage for " + actor + " -> " + target),
            () -> assertEquals(expectedDefense, actionEvent.getTargetDefense(), "Unexpected target defense for " + actor + " -> " + target),
            () -> assertEquals(
                expectedStatusEffectEvents,
                actionEvent.getStatusEffectEvents(),
                "Unexpected status effect events for " + actor + " -> " + target
            )
        );
    }

    private static void assertSkipped(
        RoundCapture roundCapture,
        String combatantName,
        String expectedReason,
        List<StatusEffectEvent> expectedStatusEffectEvents
    ) {
        assertNotNull(roundCapture, "Missing expected round capture");

        SkippedTurnEvent skippedTurnEvent = roundCapture.events().stream()
            .filter(SkippedTurnEvent.class::isInstance)
            .map(SkippedTurnEvent.class::cast)
            .filter(event -> event.getCombatantName().equals(combatantName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Skipped-turn event not found for " + combatantName));

        assertAll(
            () -> assertEquals(expectedReason, skippedTurnEvent.getReason(), "Unexpected skip reason for " + combatantName),
            () -> assertEquals(
                expectedStatusEffectEvents,
                skippedTurnEvent.getStatusEffectEvents(),
                "Unexpected status effect events for " + combatantName
            )
        );
    }

    private static void assertNarrationContains(RoundCapture roundCapture, String expectedText) {
        assertNotNull(roundCapture, "Missing expected round capture");
        boolean found = roundCapture.events().stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch(expectedText::equals);
        assertTrue(found, "Expected narration not found: " + expectedText);
    }

    private static void assertVictoryNarration(List<BattleEvent> events) {
        boolean victoryFound = events.stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch("Victory:"::equals);
        assertTrue(victoryFound, "Expected victory narration to be emitted");
    }

    private static ExpectedEnemyState enemy(String name, int hp, boolean alive, Set<String> statuses) {
        return new ExpectedEnemyState(name, hp, alive, statuses);
    }

    private record ExpectedEnemyState(String name, int hp, boolean alive, Set<String> statuses) {
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
