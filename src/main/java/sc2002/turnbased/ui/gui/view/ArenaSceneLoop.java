package sc2002.turnbased.ui.gui.view;

import java.util.Objects;

import javax.swing.Timer;

final class ArenaSceneLoop {
    private final Runnable tickCallback;
    private Timer tickTimer;

    ArenaSceneLoop(Runnable tickCallback) {
        this.tickCallback = Objects.requireNonNull(tickCallback, "tickCallback");
    }

    void start() {
        if (tickTimer != null) {
            return;
        }
        tickTimer = new Timer(16, event -> tickCallback.run());
        tickTimer.start();
    }

    void stop() {
        if (tickTimer != null) {
            tickTimer.stop();
            tickTimer = null;
        }
    }
}
