package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class DefaultBattleOutcomeReporterTest {
    @Test
    void reportOutcome_WhenVictory_EmitsVictoryNarration() {
        PlayerCharacter player = TestDependencies.warrior();
        BattleOutcomeReporter reporter = new DefaultBattleOutcomeReporter(player);
        List<BattleEvent> emittedEvents = new ArrayList<>();

        reporter.reportOutcome(4, List.of(), List.of(), emittedEvents::add);

        List<String> narrationLines = narrationTexts(emittedEvents);
        assertTrue(narrationLines.contains("Victory:"));
        assertTrue(narrationLines.contains("Total Rounds: 4"));
        assertTrue(narrationLines.stream().anyMatch(line -> line.startsWith("Remaining HP: ")));
    }

    @Test
    void reportOutcome_WhenDefeat_EmitsDefeatNarration() {
        PlayerCharacter player = TestDependencies.warrior();
        player.receiveDamage(player.getCurrentHp());
        EnemyCombatant enemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Wolf")
            .withHp(40)
            .build();

        BattleOutcomeReporter reporter = new DefaultBattleOutcomeReporter(player);
        List<BattleEvent> emittedEvents = new ArrayList<>();

        reporter.reportOutcome(3, List.of(enemy), List.of(), emittedEvents::add);

        List<String> narrationLines = narrationTexts(emittedEvents);
        assertTrue(narrationLines.contains("Defeat:"));
        assertTrue(narrationLines.contains("Enemies remaining: 1"));
        assertTrue(narrationLines.contains("Total Rounds Survived: 3"));
    }

    @Test
    void reportOutcome_WhenOnlyReserveEnemiesRemainOnDefeat_UsesTotalRemainingEnemyCount() {
        PlayerCharacter player = TestDependencies.warrior();
        player.receiveDamage(player.getCurrentHp());
        EnemyCombatant reserveEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Reserve Wolf")
            .withHp(40)
            .build();

        BattleOutcomeReporter reporter = new DefaultBattleOutcomeReporter(player);
        List<BattleEvent> emittedEvents = new ArrayList<>();

        reporter.reportOutcome(3, List.of(), List.of(reserveEnemy), emittedEvents::add);

        List<String> narrationLines = narrationTexts(emittedEvents);
        assertTrue(narrationLines.contains("Defeat:"));
        assertTrue(narrationLines.contains("Enemies remaining: 1"));
        assertTrue(narrationLines.contains("Total Rounds Survived: 3"));
    }

    private static List<String> narrationTexts(List<BattleEvent> events) {
        List<String> lines = new ArrayList<>();
        for (BattleEvent event : events) {
            if (event instanceof NarrationEvent narrationEvent) {
                lines.add(narrationEvent.getText());
            }
        }
        return lines;
    }
}