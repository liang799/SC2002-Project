package sc2002.turnbased.ui.gui.playback;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

import javax.swing.Timer;

import sc2002.turnbased.report.BattleEvent;

public final class BattlePlaybackController {
    private final Queue<BattleEvent> events = new ArrayDeque<>();
    private final BattleDialogueFormatter dialogueFormatter;
    private final Consumer<BattleEvent> eventPlayer;
    private final Runnable drainedCallback;
    private Timer playbackTimer;
    private boolean active;

    public BattlePlaybackController(
        BattleDialogueFormatter dialogueFormatter,
        Consumer<BattleEvent> eventPlayer,
        Runnable drainedCallback
    ) {
        this.dialogueFormatter = Objects.requireNonNull(dialogueFormatter, "dialogueFormatter");
        this.eventPlayer = Objects.requireNonNull(eventPlayer, "eventPlayer");
        this.drainedCallback = Objects.requireNonNull(drainedCallback, "drainedCallback");
    }

    public void enqueue(BattleEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
        playNextIfIdle();
    }

    public void playNextIfIdle() {
        if (active) {
            return;
        }

        BattleEvent event = events.poll();
        if (event == null) {
            drainedCallback.run();
            return;
        }

        active = true;
        eventPlayer.accept(event);
        playbackTimer = new Timer(dialogueFormatter.playbackDelayMillis(event), e -> {
            playbackTimer.stop();
            playbackTimer = null;
            active = false;
            playNextIfIdle();
        });
        playbackTimer.setRepeats(false);
        playbackTimer.start();
    }

    public void reset() {
        if (playbackTimer != null) {
            playbackTimer.stop();
            playbackTimer = null;
        }
        events.clear();
        active = false;
    }
}
