package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.StatusEffect;
import sc2002.turnbased.domain.status.StatusEffectChange;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.StatusEffectOutcome;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class BattleEngineTest {
    @Test
    void runRounds_WhenActorTakesNormalTurn_DoesNotDrainPendingStatusOutcomes() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Fast Goblin")
            .withHp(30)
            .withAttack(5)
            .withDefense(0)
            .withSpeed(player.getSpeed() + 10)
            .build();
        enemy.addStatusEffect(expiringOnTurnCheckEffect());

        BattleEngine battleEngine = new BattleEngine(
            new BattleSetup(player, List.of(enemy), List.of(), new Inventory()),
            new SpeedTurnOrderStrategy()
        );

        List<BattleEvent> events = battleEngine.runRounds(
            1,
            new ScriptedDecisionProvider()
                .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy.getName()))
        );

        ActionEvent playerAttackEvent = events.stream()
            .filter(ActionEvent.class::isInstance)
            .map(ActionEvent.class::cast)
            .filter(event -> event.getActorName().equals(player.getName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Expected a player attack event"));

        assertEquals(List.of("Defend expired"), playerAttackEvent.getStatusEffectNotes());
    }

    private static StatusEffect expiringOnTurnCheckEffect() {
        return new StatusEffect() {
            private boolean expired;

            @Override
            public StatusEffectKind kind() {
                return StatusEffectKind.DEFEND;
            }

            @Override
            public String description() {
                return "TURN CHECK";
            }

            @Override
            public Optional<String> getTurnBlockReason(Combatant owner) {
                expired = true;
                return Optional.empty();
            }

            @Override
            public List<StatusEffectOutcome> onExpire(Combatant owner) {
                return List.of(StatusEffectChange.expired(kind()));
            }

            @Override
            public boolean isExpired() {
                return expired;
            }
        };
    }
}
