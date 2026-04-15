package sc2002.turnbased.ui.gui.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.ui.gui.model.PlayerTurnRequest;
import sc2002.turnbased.ui.gui.model.ResolvedPlayerCommand;
import sc2002.turnbased.ui.gui.view.BattleCommandPanel;
import sc2002.turnbased.support.TestDependencies;

class PlayerCommandResolverTest {
    private final PlayerCommandResolver resolver = new PlayerCommandResolver();

    @Test
    void resolvesBasicAttackAgainstSelectedLivingEnemy() {
        PlayerCharacter player = TestDependencies.warrior();
        Combatant goblin = TestDependencies.goblin("Goblin A");
        PlayerTurnRequest turn = turn(player, List.of(goblin));

        Optional<ResolvedPlayerCommand> resolved = resolver.resolve(
            BattleCommandPanel.Command.BASIC_ATTACK,
            turn,
            goblin.combatantId()
        );

        assertTrue(resolved.isPresent());
        assertInstanceOf(BasicAttackAction.class, resolved.get().decision().action());
        assertEquals(goblin, resolved.get().decision().targetReference().resolveFrom(List.of(goblin)));
        assertEquals("BasicAttack", resolved.get().actionName());
        assertEquals(" -> Goblin A", resolved.get().targetLabel());
    }

    @Test
    void rejectsTargetedActionWhenNoLivingTargetIsSelected() {
        PlayerCharacter player = TestDependencies.warrior();
        Combatant goblin = TestDependencies.goblin("Goblin A");
        PlayerTurnRequest turn = turn(player, List.of(goblin));

        Optional<ResolvedPlayerCommand> resolved = resolver.resolve(
            BattleCommandPanel.Command.BASIC_ATTACK,
            turn,
            null
        );

        assertTrue(resolved.isEmpty());
    }

    @Test
    void resolvesDefendWithoutTarget() {
        PlayerCharacter player = TestDependencies.warrior();
        PlayerTurnRequest turn = turn(player, List.of());

        Optional<ResolvedPlayerCommand> resolved = resolver.resolve(
            BattleCommandPanel.Command.DEFEND,
            turn,
            null
        );

        assertTrue(resolved.isPresent());
        assertInstanceOf(DefendAction.class, resolved.get().decision().action());
        assertEquals("Defend", resolved.get().actionName());
        assertEquals("", resolved.get().targetLabel());
    }

    @Test
    void rejectsItemCommandWhenItemIsUnavailable() {
        PlayerCharacter player = TestDependencies.warrior();
        PlayerTurnRequest turn = turn(player, List.of());

        Optional<ResolvedPlayerCommand> resolved = resolver.resolve(
            BattleCommandPanel.Command.POTION,
            turn,
            null
        );

        assertTrue(resolved.isEmpty());
    }

    @Test
    void resolvesPotionWhenInventoryContainsPotion() {
        PlayerCharacter player = TestDependencies.warrior();
        player.getInventory().add(ItemType.POTION, 1);
        PlayerTurnRequest turn = turn(player, List.of());

        Optional<ResolvedPlayerCommand> resolved = resolver.resolve(
            BattleCommandPanel.Command.POTION,
            turn,
            null
        );

        assertTrue(resolved.isPresent());
        assertInstanceOf(UsePotionAction.class, resolved.get().decision().action());
        assertEquals("Item", resolved.get().actionName());
    }

    private PlayerTurnRequest turn(PlayerCharacter player, List<Combatant> enemies) {
        return new PlayerTurnRequest(
            1,
            player,
            enemies,
            new ArrayBlockingQueue<PlayerDecision>(1)
        );
    }
}
