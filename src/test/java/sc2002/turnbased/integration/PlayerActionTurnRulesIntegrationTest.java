package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.EnemyCount;
import sc2002.turnbased.engine.EnemyType;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.ScriptedDecisionProvider;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.support.BattleTestSupport;
import sc2002.turnbased.support.BattleTestSupport.RoundCapture;
import sc2002.turnbased.support.TestDependencies;

@Tag("integration")
class PlayerActionTurnRulesIntegrationTest {
    @Test
    void runRounds_WhenPlayerUsesAllFourActionTypes_ExecutesExactlyOnePlayerActionPerTurn() {
        // arrange
        BattleSetup battleSetup = TestDependencies.battleSetupFactory().createCustom(
            new CustomGameConfiguration(
                PlayerType.WARRIOR,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB),
                List.of(WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, 1),
                    EnemyCount.of(EnemyType.WOLF, 0)
                ))
            )
        );
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin A")))
            .addDecision(2, PlayerDecision.untargeted(new DefendAction()))
            .addDecision(3, PlayerDecision.untargeted(new UsePotionAction()))
            .addDecision(4, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Goblin A")));
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());

        // act
        Map<Integer, RoundCapture> rounds = BattleTestSupport.captureRounds(battleEngine.runRounds(4, decisions));

        // assert
        assertRoundContainsOnlyPlayerAction(rounds.get(1), "BasicAttack");
        assertRoundContainsOnlyPlayerAction(rounds.get(2), "Defend");
        assertRoundContainsOnlyPlayerAction(rounds.get(3), "Item");
        assertRoundContainsOnlyPlayerAction(rounds.get(4), "SpecialSkill");
    }

    private static void assertRoundContainsOnlyPlayerAction(RoundCapture roundCapture, String expectedActionType) {
        assertNotNull(roundCapture, "Missing round capture");

        List<String> playerActionTypes = roundCapture.events().stream()
            .map(PlayerActionTurnRulesIntegrationTest::toPlayerActionType)
            .flatMap(Optional::stream)
            .toList();

        assertEquals(List.of(expectedActionType), playerActionTypes);
    }

    private static Optional<String> toPlayerActionType(BattleEvent event) {
        if (event instanceof ActionEvent actionEvent && actionEvent.getActorName().equals("Warrior")) {
            return switch (actionEvent.getActionName()) {
                case "BasicAttack" -> Optional.of("BasicAttack");
                case "Shield Bash", "Arcane Blast" -> Optional.of("SpecialSkill");
                default -> Optional.empty();
            };
        }

        if (event instanceof NarrationEvent narrationEvent) {
            String text = narrationEvent.getText();
            if (text.startsWith("Warrior -> Defend")) {
                return Optional.of("Defend");
            }
            if (text.startsWith("Warrior -> Item ->")) {
                return Optional.of("Item");
            }
            if (text.startsWith("Warrior -> Arcane Blast ->")) {
                return Optional.of("SpecialSkill");
            }
        }

        return Optional.empty();
    }

    private static Combatant enemyNamed(BattleSetup battleSetup, String enemyName) {
        return battleSetup.getInitialEnemies().stream()
            .filter(enemy -> enemy.getName().equals(enemyName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Enemy not found in setup: " + enemyName));
    }
}
