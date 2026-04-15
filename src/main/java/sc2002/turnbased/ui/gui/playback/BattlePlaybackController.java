package sc2002.turnbased.ui.gui.playback;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

import javax.swing.Timer;

import sc2002.turnbased.report.BattleEvent;

/**
 * EDT-only battle event playback queue.
 * <p>
 * This class uses {@link Timer}, whose callbacks run on the Swing Event Dispatch Thread. Call
 * {@link #enqueue(BattleEvent)}, {@link #playNextIfIdle()}, and {@link #reset()} from the EDT;
 * use {@code SwingUtilities.invokeLater(...)} or {@code SwingUtilities.invokeAndWait(...)} when
 * calling from another thread.
 */
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
        try {
            eventPlayer.accept(event);
            playbackTimer = new Timer(dialogueFormatter.playbackDelayMillis(event), e -> {
                stopPlaybackTimer();
                active = false;
                playNextIfIdle();
            });
            playbackTimer.setRepeats(false);
            playbackTimer.start();
        } catch (Throwable throwable) {
            stopPlaybackTimer();
            active = false;
            throw rethrow(throwable);
        }
    }

    public void reset() {
        stopPlaybackTimer();
        events.clear();
        active = false;
    }

    private void stopPlaybackTimer() {
        if (playbackTimer != null) {
            playbackTimer.stop();
            playbackTimer = null;
        }
    }

    private static RuntimeException rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        if (throwable instanceof Error error) {
            throw error;
        }
        return new IllegalStateException(throwable);
    }
}
