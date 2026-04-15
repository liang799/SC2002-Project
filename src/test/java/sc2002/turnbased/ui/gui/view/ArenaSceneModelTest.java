package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.support.TestDependencies;

class ArenaSceneModelTest {
    private static final int ARENA_WIDTH = 940;
    private static final int ARENA_HEIGHT = 560;

    @Test
    void projectsBattleSetupAndCyclesTargets() {
        ArenaSceneModel model = new ArenaSceneModel();
        List<String> targetLabels = new ArrayList<>();
        model.setTargetSelectionListener(targetLabels::add);

        model.startBattle(setupWithTwoEnemies(), ARENA_WIDTH, ARENA_HEIGHT);

        assertTrue(model.battleActive());
        assertEquals(3, model.spritesByDrawOrder().size());
        assertTrue(model.selectedEnemyLabel().startsWith("Goblin A HP"));
        assertFalse(targetLabels.isEmpty());

        model.selectNextEnemy(1);

        assertTrue(model.selectedEnemyLabel().startsWith("Goblin B HP"));
    }

    @Test
    void actionEventUpdatesSpritesAndFallsBackToLivingTarget() {
        PlayerCharacter player = TestDependencies.warrior();
        Combatant goblinA = TestDependencies.goblin("Goblin A");
        Combatant goblinB = TestDependencies.goblin("Goblin B");
        ArenaSceneModel model = new ArenaSceneModel();
        model.startBattle(new BattleSetup(player, List.of(goblinA, goblinB), List.of()), ARENA_WIDTH, ARENA_HEIGHT);
        CombatantId targetId = model.selectedEnemyId();
        long eventTime = 10_000_000_000L;

        model.applyBattleEvent(new ActionEvent(
            player.combatantId(),
            player.getName(),
            "Slash",
            targetId,
            goblinA.getName(),
            goblinA.getCurrentHp(),
            0,
            player.getAttack(),
            0,
            goblinA.getCurrentHp(),
            true,
            List.of()
        ), ARENA_WIDTH, ARENA_HEIGHT, eventTime);

        FighterSpriteDto eliminated = spriteById(model, targetId);
        assertEquals(0, eliminated.hp);
        assertFalse(eliminated.alive);
        assertTrue(model.selectedEnemyLabel().startsWith("Goblin B HP"));
        assertEquals(2, model.floatingTexts().size());

        model.tick(eventTime + 1_100_000_000L, ARENA_WIDTH, ARENA_HEIGHT);

        assertTrue(model.floatingTexts().isEmpty());
    }

    @Test
    void tickMovesPlayerOnlyWhileBattleIsActive() {
        ArenaSceneModel model = new ArenaSceneModel();
        model.startBattle(setupWithTwoEnemies(), ARENA_WIDTH, ARENA_HEIGHT);
        FighterSpriteDto player = model.playerSprite();
        double startX = player.x;

        model.tick(1_000_000_000L, ARENA_WIDTH, ARENA_HEIGHT);
        model.setDirectionPressed("right", true);
        model.tick(1_100_000_000L, ARENA_WIDTH, ARENA_HEIGHT);

        assertTrue(player.x > startX);
        assertTrue(player.walkPhase > 0);

        model.applyBattleEvent(new NarrationEvent("Victory!"), ARENA_WIDTH, ARENA_HEIGHT, 2_000_000_000L);
        double stoppedX = player.x;
        model.tick(2_100_000_000L, ARENA_WIDTH, ARENA_HEIGHT);

        assertEquals(stoppedX, player.x);
    }

    @Test
    void selectNextEnemyIsSafeWhenNoEnemiesAreAlive() {
        BattleSetup setup = setupWithTwoEnemies();
        PlayerCharacter player = setup.getPlayer();
        Combatant goblinA = setup.getInitialEnemies().get(0);
        Combatant goblinB = setup.getInitialEnemies().get(1);
        ArenaSceneModel model = new ArenaSceneModel();
        model.startBattle(setup, ARENA_WIDTH, ARENA_HEIGHT);

        eliminate(model, player, goblinA, 10_000_000_000L);
        eliminate(model, player, goblinB, 11_000_000_000L);

        assertNull(model.selectedEnemyId());
        assertEquals("No target", model.selectedEnemyLabel());
        assertDoesNotThrow(() -> model.selectNextEnemy(1));
        assertNull(model.selectedEnemyId());
    }

    @Test
    void selectEnemyAtIgnoresOutOfBoundsPoints() {
        ArenaSceneModel model = new ArenaSceneModel();
        model.startBattle(setupWithTwoEnemies(), ARENA_WIDTH, ARENA_HEIGHT);
        CombatantId selected = model.selectedEnemyId();

        assertFalse(model.selectEnemyAt(new Point(-100, -100)));
        assertEquals(selected, model.selectedEnemyId());
        assertFalse(model.selectEnemyAt(new Point(ARENA_WIDTH + 100, ARENA_HEIGHT + 100)));
        assertEquals(selected, model.selectedEnemyId());
    }

    @Test
    void showSetupPreviewResetsBattleState() {
        ArenaSceneModel model = new ArenaSceneModel();
        model.showSetupPreview();
        model.startBattle(setupWithTwoEnemies(), ARENA_WIDTH, ARENA_HEIGHT);
        model.selectNextEnemy(1);
        model.setDirectionPressed("right", true);

        model.showSetupPreview();

        assertNull(model.selectedEnemyId());
        assertNull(model.currentSelectedEnemySprite());
        assertNull(model.playerSprite());
        assertTrue(model.spritesByDrawOrder().isEmpty());
        assertTrue(model.floatingTexts().isEmpty());
        assertFalse(model.battleActive());
        assertFalse(model.acceptingPlayerTurn());
        assertEquals(0, model.roundNumber());
        assertEquals("No target", model.selectedEnemyLabel());
    }

    @Test
    void playerUpdatePreservesZeroCoordinates() {
        BattleSetup setup = setupWithTwoEnemies();
        ArenaSceneModel model = new ArenaSceneModel();
        model.startBattle(setup, ARENA_WIDTH, ARENA_HEIGHT);
        FighterSpriteDto player = model.playerSprite();
        player.x = 0;
        player.y = 0;

        model.showPlayerTurn(1, setup.getPlayer(), setup.getInitialEnemies(), ARENA_WIDTH, ARENA_HEIGHT);

        assertEquals(0, player.x);
        assertEquals(0, player.y);
    }

    private static BattleSetup setupWithTwoEnemies() {
        return new BattleSetup(
            TestDependencies.warrior(),
            List.of(TestDependencies.goblin("Goblin A"), TestDependencies.goblin("Goblin B")),
            List.of()
        );
    }

    private static FighterSpriteDto spriteById(ArenaSceneModel model, CombatantId id) {
        return model.spritesByDrawOrder()
            .stream()
            .filter(sprite -> sprite.id.equals(id))
            .findFirst()
            .orElseThrow();
    }

    private static void eliminate(ArenaSceneModel model, PlayerCharacter player, Combatant target, long eventTime) {
        model.applyBattleEvent(new ActionEvent(
            player.combatantId(),
            player.getName(),
            "Slash",
            target.combatantId(),
            target.getName(),
            target.getCurrentHp(),
            0,
            player.getAttack(),
            0,
            target.getCurrentHp(),
            true,
            List.of()
        ), ARENA_WIDTH, ARENA_HEIGHT, eventTime);
    }
}
