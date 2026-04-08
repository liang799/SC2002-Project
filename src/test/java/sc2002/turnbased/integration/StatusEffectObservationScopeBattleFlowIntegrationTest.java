package sc2002.turnbased.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.StatusEffectObservationScope;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.domain.status.event.SmokeBombAppliedEvent;
import sc2002.turnbased.domain.status.event.StatusEffectExpiredEvent;
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
import sc2002.turnbased.support.TestDependencies;

@Tag("integration")
class StatusEffectObservationScopeBattleFlowIntegrationTest {
    @Test
    @DisplayName("StatusEffectObservationScope and BattleEngine publish Smoke Bomb activation events")
    void openObservation_WhenSmokeBombActivates_PublishesActivationEvents() {
        // Arrange
        BattleSetup battleSetup = TestDependencies.battleSetupFactory().createCustom(
            new CustomGameConfiguration(
                PlayerType.WARRIOR,
                List.of(ItemType.SMOKE_BOMB, ItemType.POTION),
                List.of(WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, 1),
                    EnemyCount.of(EnemyType.WOLF, 0)
                ))
            )
        );
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSmokeBombAction()));

        try (StatusEffectObservationScope observation = battleSetup.getPlayer().statusEffects().openObservation()) {
            // Act
            battleEngine.runRounds(1, decisions);

            // Assert
            assertAll(
                () -> assertEquals(
                    List.of(
                        new SmokeBombAppliedEvent("Warrior", 2),
                        new SmokeBombActivatedEvent("Warrior", "Goblin A", 1)
                    ),
                    observation.observedEvents()
                ),
                () -> assertEquals(260, battleSetup.getPlayer().getCurrentHp()),
                () -> assertEquals(List.of("SMOKE BOMB"), battleSetup.getPlayer().getActiveStatusNames())
            );
        }
    }

    @Test
    @DisplayName("StatusEffectObservationScope and BattleEngine publish Smoke Bomb lifecycle events")
    void openObservation_WhenSmokeBombLifecycleCompletes_PublishesLifecycleEvents() {
        // Arrange
        BattleSetup battleSetup = TestDependencies.battleSetupFactory().createCustom(
            new CustomGameConfiguration(
                PlayerType.WARRIOR,
                List.of(ItemType.SMOKE_BOMB, ItemType.POTION),
                List.of(WaveSpec.of(
                    EnemyCount.of(EnemyType.GOBLIN, 2),
                    EnemyCount.of(EnemyType.WOLF, 0)
                ))
            )
        );
        BattleEngine battleEngine = new BattleEngine(battleSetup, new SpeedTurnOrderStrategy());
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSmokeBombAction()));

        try (StatusEffectObservationScope observation = battleSetup.getPlayer().statusEffects().openObservation()) {
            // Act
            battleEngine.runRounds(1, decisions);

            // Assert
            assertAll(
                () -> assertEquals(
                    List.of(
                        new SmokeBombAppliedEvent("Warrior", 2),
                        new SmokeBombActivatedEvent("Warrior", "Goblin A", 1),
                        new SmokeBombActivatedEvent("Warrior", "Goblin B", 0),
                        new StatusEffectExpiredEvent("Warrior", StatusEffectKind.SMOKE_BOMB)
                    ),
                    observation.observedEvents()
                ),
                () -> assertEquals(260, battleSetup.getPlayer().getCurrentHp()),
                () -> assertEquals(List.of(), battleSetup.getPlayer().getActiveStatusNames())
            );
        }
    }
}
