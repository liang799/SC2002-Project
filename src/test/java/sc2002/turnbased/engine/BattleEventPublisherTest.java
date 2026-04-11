package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

@Tag("unit")
class BattleEventPublisherTest {
    @Test
    void emit_WhenCalled_StoresEventAndNotifiesListener() {
        BattleEventPublisher battleEventPublisher = new BattleEventPublisher();
        List<BattleEvent> observedByListener = new ArrayList<>();
        NarrationEvent event = new NarrationEvent("Test Event");

        battleEventPublisher.emit(event, observedByListener::add);

        assertEquals(List.of(event), observedByListener);
        assertEquals(List.of(event), battleEventPublisher.snapshot());
    }

    @Test
    void snapshot_WhenMutated_ThrowsUnsupportedOperationException() {
        BattleEventPublisher battleEventPublisher = new BattleEventPublisher();
        battleEventPublisher.emit(new NarrationEvent("Immutable"), BattleEventListener.NO_OP);

        List<BattleEvent> snapshot = battleEventPublisher.snapshot();

        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> snapshot.add(new NarrationEvent("Fail"))
        );
        assertEquals(UnsupportedOperationException.class, exception.getClass());
    }
}