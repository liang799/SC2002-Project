package sc2002.turnbased.e2e;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import static sc2002.turnbased.support.BattleRoundAssertions.assertCapturedRound;
import sc2002.turnbased.support.BattleTestSupport;
import sc2002.turnbased.support.BattleTestSupport.RoundCapture;
import static sc2002.turnbased.support.ExpectedCombatantState.enemy;
import sc2002.turnbased.support.TestDependencies;

@Tag("e2e")
class CustomBattleFlowE2ETest {
    @Test
    @DisplayName("Given a compact custom wizard battle flow, when rounds are run, then key gameplay mechanics are covered in 8 rounds")
    void givenCompactCustomWizardBattleFlow_WhenRoundsAreRun_ThenKeyGameplayMechanicsAreCoveredIn8Rounds() {
        BattleSetup battleSetup = TestDependencies.battleSetupFactory().createCustom(
            new CustomGameConfiguration(
                PlayerType.WIZARD,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB),
                List.of(
                    WaveSpec.of(EnemyCount.of(EnemyType.GOBLIN, 1)),
                    WaveSpec.of(EnemyCount.of(EnemyType.GOBLIN, 2))
                )
            )
        );
        CustomFlowDecisionProvider decisionProvider = new CustomFlowDecisionProvider();
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        List<BattleEvent> events = battleEngine.runRounds(8, decisionProvider);

        Map<Integer, RoundCapture> rounds = BattleTestSupport.captureRounds(events);

        assertCapturedRound(rounds.get(2), 150, 2, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 1), Set.of(),
            enemy("Goblin A", 0),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertCapturedRound(rounds.get(3), 100, 1, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0), Set.of(),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertCapturedRound(rounds.get(4), 100, 0, Map.of(ItemType.POTION, 1, ItemType.SMOKE_BOMB, 0), Set.of("DEFENDING"),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertCapturedRound(rounds.get(5), 170, 0, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0), Set.of(),
            enemy("Goblin B", 55),
            enemy("Goblin C", 55)
        );
        assertCapturedRound(rounds.get(6), 120, 3, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0), Set.of(),
            enemy("Goblin B", 20),
            enemy("Goblin C", 20)
        );
        assertCapturedRound(rounds.get(8), 45, 1, Map.of(ItemType.POTION, 0, ItemType.SMOKE_BOMB, 0), Set.of(),
            enemy("Goblin B", 0),
            enemy("Goblin C", 0)
        );

        assertAction(rounds.get(3), "Goblin B", "Wizard", 25, 10, List.of());
        assertAction(rounds.get(3), "Goblin C", "Wizard", 25, 10, List.of());
        assertAction(rounds.get(4), "Goblin B", "Wizard", 0, 10, List.of("Smoke Bomb blocked the attack"));
        assertAction(rounds.get(4), "Goblin C", "Wizard", 0, 10,
            List.of(
                "Smoke Bomb blocked the attack",
                "Smoke Bomb expired"
            ));
        assertAction(rounds.get(5), "Goblin B", "Wizard", 15, 20, List.of());

        assertNarrationContains(rounds.get(2), "Backup Spawn triggered: Goblin B, Goblin C");
        assertNarrationContains(rounds.get(5), "Wizard -> Item -> Potion used: HP: 70 -> 170 (+100)");
        assertVictoryNarration(events);

        assertEquals(Set.of(2), decisionProvider.getCooldownBlockedRounds());
        assertEquals(
            List.of("Round 2 fallback used: attempted SpecialSkill with cooldown 3, switched to BasicAttack."),
            decisionProvider.getFallbackDescriptions()
        );
    }

    private static void assertAction(
        RoundCapture roundCapture,
        String actor,
        String target,
        int expectedDamage,
        int expectedDefense,
        List<String> expectedStatusEffectNotes
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
                expectedStatusEffectNotes,
                actionEvent.getStatusEffectNotes(),
                "Unexpected status effect notes for " + actor + " -> " + target
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

            if (plannedTurn.primaryAction() instanceof UseSpecialSkillAction && !player.canUseSpecialSkill()) {
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

            Combatant target = livingEnemies.stream()
                .filter(enemy -> enemy.getName().equals(targetName))
                .findFirst()
                .orElse(null);
            if (target == null) {
                throw new IllegalStateException("Target " + targetName + " was not available for action " + action.getName());
            }
            return PlayerDecision.targeted(action, target);
        }

        private Map<Integer, PlannedTurn> buildPlan() {
            Map<Integer, PlannedTurn> plan = new LinkedHashMap<>();
            plan.put(1, new PlannedTurn(new UseSpecialSkillAction(), null, null, null));
            plan.put(2, new PlannedTurn(new UseSpecialSkillAction(), "Goblin A", new BasicAttackAction(), "Goblin A"));
            plan.put(3, new PlannedTurn(new UseSmokeBombAction(), null, null, null));
            plan.put(4, new PlannedTurn(new DefendAction(), null, null, null));
            plan.put(5, new PlannedTurn(new UsePotionAction(), null, null, null));
            plan.put(6, new PlannedTurn(new UseSpecialSkillAction(), null, null, null));
            plan.put(7, new PlannedTurn(new BasicAttackAction(), "Goblin B", null, null));
            plan.put(8, new PlannedTurn(new BasicAttackAction(), "Goblin C", null, null));
            return plan;
        }
    }

    private record PlannedTurn(BattleAction primaryAction, String primaryTarget, BattleAction fallbackAction, String fallbackTarget) {
    }
}
