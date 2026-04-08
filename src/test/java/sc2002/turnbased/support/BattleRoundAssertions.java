package sc2002.turnbased.support;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.support.BattleTestSupport.RoundCapture;

public final class BattleRoundAssertions {
    private BattleRoundAssertions() {
    }

    public static void assertRoundSummary(
        RoundSummaryEvent summary,
        int expectedPlayerHp,
        int expectedCooldown,
        Map<ItemType, Integer> expectedInventory,
        ExpectedCombatantState... expectedEnemies
    ) {
        assertNotNull(summary, "Missing expected round summary");
        assertAll(
            () -> assertEquals(expectedPlayerHp, summary.getPlayerSummary().getCurrentHp(), "Unexpected player HP"),
            () -> assertEquals(expectedCooldown, summary.getSpecialSkillCooldown(), "Unexpected cooldown"),
            () -> assertInventory(summary, expectedInventory)
        );
        assertEnemies(summary, expectedEnemies);
    }

    public static void assertCapturedRound(
        RoundCapture roundCapture,
        int expectedPlayerHp,
        int expectedCooldown,
        Map<ItemType, Integer> expectedInventory,
        Set<String> expectedPlayerStatuses,
        ExpectedCombatantState... expectedEnemies
    ) {
        assertNotNull(roundCapture, "Missing expected round capture");

        RoundSummaryEvent summary = roundCapture.summary();
        assertNotNull(summary, "Missing round summary");

        CombatantSummary playerSummary = summary.getPlayerSummary();
        assertAll(
            () -> assertEquals(expectedPlayerHp, playerSummary.getCurrentHp(), "Unexpected player HP in round " + summary.getRoundNumber()),
            () -> assertEquals(expectedCooldown, summary.getSpecialSkillCooldown(), "Unexpected cooldown in round " + summary.getRoundNumber()),
            () -> assertInventory(summary, expectedInventory),
            () -> assertTrue(
                Set.copyOf(playerSummary.getActiveStatuses())
                    .containsAll(Objects.requireNonNull(expectedPlayerStatuses, "expectedPlayerStatuses")),
                "Unexpected player statuses in round " + summary.getRoundNumber()
            )
        );
        assertEnemies(summary, expectedEnemies);
    }

    private static void assertInventory(RoundSummaryEvent summary, Map<ItemType, Integer> expectedInventory) {
        Objects.requireNonNull(expectedInventory, "expectedInventory").forEach((itemType, expectedCount) ->
            assertEquals(
                expectedCount,
                summary.getInventorySnapshot().getOrDefault(itemType, 0),
                "Unexpected count for " + itemType + " in round " + summary.getRoundNumber()
            )
        );
    }

    private static void assertEnemies(RoundSummaryEvent summary, ExpectedCombatantState... expectedEnemies) {
        for (ExpectedCombatantState expectedEnemy : Objects.requireNonNull(expectedEnemies, "expectedEnemies")) {
            CombatantSummary enemySummary = BattleTestSupport.findEnemy(summary, expectedEnemy.name());
            assertAll(
                () -> assertEquals(
                    expectedEnemy.hp(),
                    enemySummary.getCurrentHp(),
                    "Unexpected HP for " + expectedEnemy.name() + " in round " + summary.getRoundNumber()
                ),
                () -> assertEquals(
                    expectedEnemy.alive(),
                    enemySummary.isAlive(),
                    "Unexpected alive state for " + expectedEnemy.name() + " in round " + summary.getRoundNumber()
                ),
                () -> assertTrue(
                    Set.copyOf(enemySummary.getActiveStatuses()).containsAll(expectedEnemy.statuses()),
                    "Unexpected statuses for " + expectedEnemy.name() + " in round " + summary.getRoundNumber()
                )
            );
        }
    }
}
