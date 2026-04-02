package sc2002.turnbased.engine;

import sc2002.turnbased.report.BattleEvent;

@FunctionalInterface
public interface BattleEventListener {
    BattleEventListener NO_OP = event -> {
    };

    void onEvent(BattleEvent event);
}
