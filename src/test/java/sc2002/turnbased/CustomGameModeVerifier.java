package sc2002.turnbased;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.engine.WaveSpec;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundSummaryEvent;

public class CustomGameModeVerifier {

    public static void main(String[] args) {
        List<String> failures = new ArrayList<>();

        runSectionA(failures);
        runSectionB(failures);
        runSectionC(failures);
        runSectionD(failures);
        runSectionE(failures);

        if (failures.isEmpty()) {
            System.out.println("CustomGameModeVerifier PASSED — all checks OK");
        } else {
            System.out.println("CustomGameModeVerifier FAILED — " + failures.size() + " issue(s):");
            for (String failure : failures) {
                System.out.println("  FAIL: " + failure);
            }
            throw new AssertionError("Custom game mode verification failed");
        }
    }

    private static void runSectionA(List<String> failures) {
        System.out.println("[A] WaveSpec validation...");
        checkNoThrow(failures, "A1 WaveSpec(1,0) should be valid",
            () -> new WaveSpec(1, 0));

        checkNoThrow(failures, "A2 WaveSpec(0,1) should be valid",
            () -> new WaveSpec(0, 1));

        checkNoThrow(failures, "A3 WaveSpec(3,1) should be valid",
            () -> new WaveSpec(3, 1));

        checkNoThrow(failures, "A4 WaveSpec(1,3) should be valid",
            () -> new WaveSpec(1, 3));

        checkNoThrow(failures, "A5 WaveSpec(0,3) should be valid",
            () -> new WaveSpec(0, 3));

        checkNoThrow(failures, "A6 WaveSpec(2,2) should be valid",
            () -> new WaveSpec(2, 2));

        checkThrows(failures, "A7 WaveSpec(0,0) must throw (no enemies)",
            () -> new WaveSpec(0, 0));

        checkThrows(failures, "A8 WaveSpec(3,2) must throw (total 5 > 4)",
            () -> new WaveSpec(3, 2));

        checkThrows(failures, "A9 WaveSpec(4,0) must throw (goblinCount > 3)",
            () -> new WaveSpec(4, 0));

        checkThrows(failures, "A10 WaveSpec(0,4) must throw (wolfCount > 3)",
            () -> new WaveSpec(0, 4));

        checkThrows(failures, "A11 WaveSpec(-1,1) must throw (negative goblinCount)",
            () -> new WaveSpec(-1, 1));

        checkThrows(failures, "A12 WaveSpec(1,-1) must throw (negative wolfCount)",
            () -> new WaveSpec(1, -1));

        WaveSpec spec = new WaveSpec(2, 1);
        assertEquals(failures, "A13 WaveSpec(2,1).totalEnemies() should be 3", 3, spec.totalEnemies());

        System.out.println("  [A] done");
    }

    private static void runSectionB(List<String> failures) {
        System.out.println("[B] CustomGameConfiguration validation...");

        List<ItemType> twoItems = List.of(ItemType.POTION, ItemType.SMOKE_BOMB);

        checkNoThrow(failures, "B1 1-wave config should be valid",
            () -> new CustomGameConfiguration(
                PlayerType.WARRIOR, twoItems, List.of(new WaveSpec(2, 1))));

        checkNoThrow(failures, "B2 2-wave config should be valid",
            () -> new CustomGameConfiguration(
                PlayerType.WIZARD, twoItems,
                List.of(new WaveSpec(1, 1), new WaveSpec(0, 2))));

        checkThrows(failures, "B3 1-item config must throw",
            () -> new CustomGameConfiguration(
                PlayerType.WARRIOR, List.of(ItemType.POTION), List.of(new WaveSpec(1, 0))));

        checkThrows(failures, "B4 0-wave config must throw",
            () -> new CustomGameConfiguration(
                PlayerType.WARRIOR, twoItems, List.of()));

        checkThrows(failures, "B5 3-wave config must throw",
            () -> new CustomGameConfiguration(
                PlayerType.WARRIOR, twoItems,
                List.of(new WaveSpec(1, 0), new WaveSpec(1, 0), new WaveSpec(1, 0))));

        checkThrows(failures, "B6 null playerType must throw",
            () -> new CustomGameConfiguration(
                null, twoItems, List.of(new WaveSpec(1, 0))));

        System.out.println("  [B] done");
    }


    private static void runSectionC(List<String> failures) {
        System.out.println("[C] BattleSetupFactory.createCustom enemy population...");

        BattleSetupFactory factory = new BattleSetupFactory();
        List<ItemType> items = List.of(ItemType.POTION, ItemType.POWER_STONE);

        BattleSetup setup1 = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WARRIOR, items, List.of(new WaveSpec(1, 0))));
        assertEquals(failures, "C1 initial enemies should be 1", 1, setup1.getInitialEnemies().size());
        assertEquals(failures, "C1 backup enemies should be 0", 0, setup1.getBackupEnemies().size());


        BattleSetup setup2 = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WIZARD, items, List.of(new WaveSpec(2, 2))));
        assertEquals(failures, "C2 initial enemies should be 4", 4, setup2.getInitialEnemies().size());
        assertEquals(failures, "C2 backup enemies should be 0", 0, setup2.getBackupEnemies().size());


        BattleSetup setup3 = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WARRIOR, items,
            List.of(new WaveSpec(2, 0), new WaveSpec(0, 2))));
        assertEquals(failures, "C3 wave 1 should have 2 initial enemies", 2, setup3.getInitialEnemies().size());
        assertEquals(failures, "C3 wave 2 should have 2 backup enemies", 2, setup3.getBackupEnemies().size());


        BattleSetup setup4 = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WARRIOR, items,
            List.of(new WaveSpec(3, 0), new WaveSpec(3, 0))));
        List<String> allNames = new ArrayList<>();
        for (Combatant c : setup4.getInitialEnemies()) {
            allNames.add(c.getName());
        }
        for (Combatant c : setup4.getBackupEnemies()) {
            allNames.add(c.getName());
        }
        long distinctCount = allNames.stream().distinct().count();
        assertEquals(failures, "C4 all 6 enemy names should be distinct", 6, (int) distinctCount);


        BattleSetup wizardSetup = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WIZARD, items, List.of(new WaveSpec(1, 0))));
        assertEquals(failures, "C5 custom Wizard ATK should be 50", 50, wizardSetup.getPlayer().getAttack());

        BattleSetup warriorSetup = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WARRIOR, items, List.of(new WaveSpec(1, 0))));
        assertEquals(failures, "C5 custom Warrior ATK should be 40", 40, warriorSetup.getPlayer().getAttack());


        assertEquals(failures, "C6 Potion count should be 1", 1, setup1.getInventory().countOf(ItemType.POTION));
        assertEquals(failures, "C6 Power Stone count should be 1", 1, setup1.getInventory().countOf(ItemType.POWER_STONE));

        System.out.println("  [C] done");
    }
    private static void runSectionD(List<String> failures) {
        System.out.println("[D] 1-wave battle runs to victory...");

        BattleSetupFactory factory = new BattleSetupFactory();

        BattleSetup setup = factory.createCustom(new CustomGameConfiguration(
                PlayerType.WARRIOR,
                List.of(ItemType.POTION, ItemType.SMOKE_BOMB),
                List.of(new WaveSpec(1, 0))
        ));

        List<BattleEvent> events = new BattleEngine(setup, new SpeedTurnOrderStrategy())
                .runUntilBattleEnds(new AlwaysAttackFirstEnemyProvider());

        RoundSummaryEvent lastSummary = events.stream()
                .filter(RoundSummaryEvent.class::isInstance)
                .map(RoundSummaryEvent.class::cast)
                .reduce((first, second) -> second)
                .orElse(null);

        if (lastSummary == null) {
            failures.add("D1 no RoundSummaryEvent found — battle may not have run");
        } else {

            if (lastSummary.getPlayerSummary().getCurrentHp() <= 0) {
                failures.add("D1 player HP should be > 0 after defeating 1 goblin as Warrior");
            }

            boolean allDead = lastSummary.getEnemySummaries().stream()
                    .noneMatch(e -> e.getCurrentHp() > 0);
            if (!allDead) {
                failures.add("D2 all enemies should be eliminated at end of 1-wave battle");
            }
        }

        System.out.println("  [D] done");
    }
    private static void runSectionE(List<String> failures) {
        System.out.println("[E] 2-wave battle triggers backup spawn...");

        BattleSetupFactory factory = new BattleSetupFactory();

        BattleSetup setup = factory.createCustom(new CustomGameConfiguration(
            PlayerType.WARRIOR,
            List.of(ItemType.POTION, ItemType.SMOKE_BOMB),
            List.of(new WaveSpec(1, 0), new WaveSpec(0, 1))
        ));

        List<BattleEvent> events = new BattleEngine(setup, new SpeedTurnOrderStrategy())
            .runUntilBattleEnds(new AlwaysAttackFirstEnemyProvider());


        boolean backupSpawned = events.stream()
            .filter(NarrationEvent.class::isInstance)
            .map(NarrationEvent.class::cast)
            .map(NarrationEvent::getText)
            .anyMatch(text -> text.startsWith("Backup Spawn triggered"));

        if (!backupSpawned) {
            failures.add("E1 expected 'Backup Spawn triggered' narration — wave 2 enemies never entered");
        }

        RoundSummaryEvent lastSummary = events.stream()
            .filter(RoundSummaryEvent.class::isInstance)
            .map(RoundSummaryEvent.class::cast)
            .reduce((first, second) -> second)
            .orElse(null);

        if (lastSummary == null) {
            failures.add("E2 no RoundSummaryEvent found — battle may not have run");
        } else {
            if (lastSummary.getPlayerSummary().getCurrentHp() <= 0) {
                failures.add("E2 player should survive after defeating both waves");
            }
            boolean allDead = lastSummary.getEnemySummaries().stream()
                .noneMatch(e -> e.getCurrentHp() > 0);
            if (!allDead) {
                failures.add("E2 all enemies should be eliminated after both waves");
            }
        }

        System.out.println("  [E] done");
    }

    private static void checkNoThrow(List<String> failures, String label, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            failures.add(label + " — unexpected exception: " + e.getMessage());
        }
    }

    private static void checkThrows(List<String> failures, String label, Runnable action) {
        try {
            action.run();
            failures.add(label + " — expected an exception but none was thrown");
        } catch (IllegalArgumentException | NullPointerException ignored) {
            // expected
        }
    }

    private static void assertEquals(List<String> failures, String label, int expected, int actual) {
        if (expected != actual) {
            failures.add(label + " — expected " + expected + ", got " + actual);
        }
    }

    private static final class AlwaysAttackFirstEnemyProvider implements PlayerDecisionProvider {
        @Override
        public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
            return PlayerDecision.targeted(new BasicAttackAction(), livingEnemies.get(0).getName());
        }
    }
}