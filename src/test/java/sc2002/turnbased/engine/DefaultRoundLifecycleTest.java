package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.DefendStatusEffect;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class DefaultRoundLifecycleTest {
    @Test
    void createRoundSummary_WhenCalled_IncludesPlayerAndAllSpawnedEnemies() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant aliveEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Alive Goblin")
            .withHp(30)
            .build();
        EnemyCombatant deadEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Dead Goblin")
            .withCurrentHp(0)
            .withMaxHp(30)
            .build();

        List<Combatant> spawnedEnemies = List.of(aliveEnemy, deadEnemy);
        RoundLifecycle roundLifecycle = new DefaultRoundLifecycle(player, spawnedEnemies);

        RoundSummaryEvent summaryEvent = roundLifecycle.createRoundSummary(2);

        assertEquals(2, summaryEvent.getRoundNumber());
        assertEquals(player.getName(), summaryEvent.getPlayerSummary().getName());
        assertEquals(2, summaryEvent.getEnemySummaries().size());

        CombatantSummary deadEnemySummary = summaryEvent.getEnemySummaries().stream()
            .filter(enemy -> enemy.getName().equals("Dead Goblin"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Expected dead enemy summary"));
        assertFalse(deadEnemySummary.isAlive());
    }

    @Test
    void completeRound_WhenStatusEffectExpires_EmitsStatusEffectReport() {
        PlayerCharacter player = TestDependencies.warrior();
        player.addStatusEffect(new DefendStatusEffect(1));

        RoundLifecycle roundLifecycle = new DefaultRoundLifecycle(player, List.of());
        List<BattleEvent> emittedEvents = new ArrayList<>();

        roundLifecycle.completeRound(emittedEvents::add);

        assertEquals(1, emittedEvents.size());
        StatusEffectReportEvent reportEvent = assertInstanceOf(StatusEffectReportEvent.class, emittedEvents.get(0));
        assertTrue(reportEvent.statusEffectNotes().contains("Defend expired"));
    }
}