package sc2002.turnbased.ui.gui.view;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

final class ArenaSceneLoop {
    private static final Logger LOGGER = Logger.getLogger(ArenaSceneLoop.class.getName());

    private final Runnable tickCallback;
    private Timer tickTimer;

    ArenaSceneLoop(Runnable tickCallback) {
        this.tickCallback = Objects.requireNonNull(tickCallback, "tickCallback");
    }

    void start() {
        if (tickTimer != null) {
            return;
        }
        tickTimer = new Timer(16, event -> {
            try {
                tickCallback.run();
            } catch (Throwable throwable) {
                LOGGER.log(Level.SEVERE, "Arena scene tick failed; stopping animation loop", throwable);
                stop();
            }
        });
        tickTimer.start();
    }

    void stop() {
        if (tickTimer != null) {
            tickTimer.stop();
            tickTimer = null;
        }
    }
}
