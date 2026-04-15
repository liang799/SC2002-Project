package sc2002.turnbased.ui.gui.playback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.report.NarrationEvent;

class BattlePlaybackControllerTest {
    @Test
    void clearsActiveFlagWhenEventPlayerThrows() throws Exception {
        AtomicInteger dispatchCount = new AtomicInteger();
        BattlePlaybackController controller = new BattlePlaybackController(
            new BattleDialogueFormatter(),
            event -> {
                dispatchCount.incrementAndGet();
                throw new IllegalStateException("boom");
            },
            () -> {
            }
        );

        assertThrows(IllegalStateException.class, () -> controller.enqueue(new NarrationEvent("first")));
        assertThrows(IllegalStateException.class, () -> controller.enqueue(new NarrationEvent("second")));

        assertEquals(2, dispatchCount.get());
    }
}
