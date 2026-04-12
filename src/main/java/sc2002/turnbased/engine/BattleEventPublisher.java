package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.report.BattleEvent;

class BattleEventPublisher {
    private final List<BattleEvent> events = new ArrayList<>();

    void emit(BattleEvent battleEvent, BattleEventListener battleEventListener) {
        events.add(battleEvent);
        battleEventListener.onEvent(battleEvent);
    }

    List<BattleEvent> snapshot() {
        return List.copyOf(events);
    }
}