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
import sc2002.turnbased.domain.status.DefendStatusEffect;
import sc2002.turnbased.domain.status.StatusEffect;
import sc2002.turnbased.domain.status.StatusEffectChange;
import sc2002.turnbased.domain.status.StatusEffectKind;
import sc2002.turnbased.domain.status.StatusEffectOutcome;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;
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
                .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy))
        );

        ActionEvent playerAttackEvent = events.stream()
            .filter(ActionEvent.class::isInstance)
            .map(ActionEvent.class::cast)
            .filter(event -> event.getActorName().equals(player.getName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Expected a player attack event"));

        assertEquals(List.of("Defend expired"), playerAttackEvent.getStatusEffectNotes());
    }

    @Test
    void runRounds_WhenStatusEffectsExpireAtRoundEnd_EmitsStatusReportEvent() {
        PlayerCharacter player = TestDependencies.warrior();
        player.addStatusEffect(new DefendStatusEffect(1));

        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Goblin")
            .withHp(100)
            .withAttack(0)
            .withDefense(0)
            .withSpeed(player.getSpeed() - 10)
            .build();

        BattleEngine battleEngine = new BattleEngine(
            new BattleSetup(player, List.of(enemy), List.of(), new Inventory()),
            new SpeedTurnOrderStrategy()
        );

        List<BattleEvent> events = battleEngine.runRounds(
            1,
            new ScriptedDecisionProvider()
                .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemy))
        );

        int roundSummaryIndex = firstIndexOf(events, RoundSummaryEvent.class);
        int statusReportIndex = firstIndexOf(events, StatusEffectReportEvent.class);
        StatusEffectReportEvent statusReportEvent = (StatusEffectReportEvent) events.get(statusReportIndex);

        assertEquals(List.of("Defend expired"), statusReportEvent.statusEffectNotes());
        assertEquals(true, statusReportIndex > roundSummaryIndex);
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

    private static int firstIndexOf(List<BattleEvent> events, Class<? extends BattleEvent> eventType) {
        for (int index = 0; index < events.size(); index++) {
            if (eventType.isInstance(events.get(index))) {
                return index;
            }
        }
        throw new AssertionError("Expected event not found: " + eventType.getSimpleName());
    }
}
