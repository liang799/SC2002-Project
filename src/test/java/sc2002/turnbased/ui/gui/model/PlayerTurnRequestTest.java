package sc2002.turnbased.ui.gui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.support.TestDependencies;

class PlayerTurnRequestTest {
    @Test
    void snapshotsLivingEnemies() {
        PlayerCharacter player = TestDependencies.warrior();
        List<Combatant> enemies = new ArrayList<>();
        enemies.add(TestDependencies.goblin("Goblin A"));

        PlayerTurnRequest request = new PlayerTurnRequest(
            1,
            player,
            enemies,
            new ArrayBlockingQueue<PlayerDecision>(1)
        );
        enemies.add(TestDependencies.goblin("Goblin B"));

        assertEquals(1, request.livingEnemies().size());
        assertThrows(UnsupportedOperationException.class, () -> request.livingEnemies().clear());
    }

    @Test
    void rejectsNullInputs() {
        PlayerCharacter player = TestDependencies.warrior();
        List<Combatant> enemies = List.of(TestDependencies.goblin("Goblin A"));
        ArrayBlockingQueue<PlayerDecision> queue = new ArrayBlockingQueue<>(1);

        assertThrows(NullPointerException.class, () -> new PlayerTurnRequest(1, null, enemies, queue));
        assertThrows(NullPointerException.class, () -> new PlayerTurnRequest(1, player, null, queue));
        assertThrows(NullPointerException.class, () -> new PlayerTurnRequest(1, player, enemies, null));
    }
}
