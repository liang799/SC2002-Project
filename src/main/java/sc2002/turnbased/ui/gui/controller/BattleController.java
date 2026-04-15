package sc2002.turnbased.ui.gui.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.BattleEngine;
import sc2002.turnbased.engine.BattleEventListener;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.SpeedTurnOrderStrategy;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.ui.BattleConsoleFormatter;
import sc2002.turnbased.ui.gui.command.PlayerCommandResolver;
import sc2002.turnbased.ui.gui.model.BattleSessionModel;
import sc2002.turnbased.ui.gui.model.PlayerTurnRequest;
import sc2002.turnbased.ui.gui.model.ResolvedPlayerCommand;
import sc2002.turnbased.ui.gui.playback.BattleDialogueFormatter;
import sc2002.turnbased.ui.gui.playback.BattlePlaybackController;
import sc2002.turnbased.ui.gui.setup.BattleLaunchRequest;
import sc2002.turnbased.ui.gui.setup.PostGameConfig;
import sc2002.turnbased.ui.gui.util.SwingThread;
import sc2002.turnbased.ui.gui.view.BattleCommandPanel;
import sc2002.turnbased.ui.gui.view.BattleView;
import sc2002.turnbased.ui.gui.view.PostGameChoice;

public final class BattleController {
    private static final Logger LOGGER = Logger.getLogger(BattleController.class.getName());

    private final BattleView view;
    private final BattleSessionModel model;
    private final BattlePlaybackController playbackController;
    private final PlayerCommandResolver commandResolver;
    private final BattleSetupFactory setupFactory;
    private final BattleConsoleFormatter consoleFormatter;
    private final BattleDialogueFormatter dialogueFormatter;
    private final ExecutorService battleExecutor;

    public BattleController(BattleView view, BattleSetupFactory setupFactory) {
        this(
            view,
            setupFactory,
            new BattleSessionModel(),
            new PlayerCommandResolver(),
            new BattleConsoleFormatter(),
            new BattleDialogueFormatter(),
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "battle-engine");
                t.setDaemon(true);
                return t;
            })
        );
    }

    BattleController(
        BattleView view,
        BattleSetupFactory setupFactory,
        BattleSessionModel model,
        PlayerCommandResolver commandResolver,
        BattleConsoleFormatter consoleFormatter,
        BattleDialogueFormatter dialogueFormatter,
        ExecutorService battleExecutor
    ) {
        this.view = view;
        this.setupFactory = setupFactory;
        this.model = model;
        this.commandResolver = commandResolver;
        this.consoleFormatter = consoleFormatter;
        this.dialogueFormatter = dialogueFormatter;
        this.battleExecutor = battleExecutor;
        this.playbackController = new BattlePlaybackController(
            dialogueFormatter,
            this::playBattleEvent,
            this::onPlaybackDrained
        );
    }

    public void startBattle(BattleLaunchRequest request) {
        if (!beginBattle(request.intro())) {
            return;
        }
        battleExecutor.submit(() -> runBattle(request));
    }

    PlayerDecision awaitPlayerDecision(
        int roundNumber,
        PlayerCharacter player,
        List<Combatant> livingEnemies
    ) {
        BlockingQueue<PlayerDecision> responseQueue = new ArrayBlockingQueue<>(1);
        PlayerTurnRequest nextTurn = new PlayerTurnRequest(
            roundNumber,
            player,
            List.copyOf(livingEnemies),
            responseQueue
        );
        SwingThread.runAndWait(() -> {
            model.queuePlayerTurn(nextTurn);
            playbackController.playNextIfIdle();
        });

        try {
            return responseQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PlayerDecision.untargeted(new DefendAction());
        }
    }

    public void handleCommand(BattleCommandPanel.Command command) {
        if (command == BattleCommandPanel.Command.PREVIOUS_TARGET) {
            view.selectNextEnemy(-1);
            return;
        }
        if (command == BattleCommandPanel.Command.NEXT_TARGET) {
            view.selectNextEnemy(1);
            return;
        }

        Optional<PlayerTurnRequest> activeTurn = model.activePlayerTurn();
        if (activeTurn.isEmpty()) {
            return;
        }

        CombatantId selectedTarget = view.selectedEnemyId();
        Optional<ResolvedPlayerCommand> resolved = commandResolver.resolve(command, activeTurn.get(), selectedTarget);
        if (resolved.isEmpty()) {
            view.showUnavailableCommand();
            return;
        }

        ResolvedPlayerCommand playerCommand = resolved.get();
        if (activeTurn.get().responseQueue().offer(playerCommand.decision())) {
            model.clearActivePlayerTurn();
            view.showCommandResolving(playerCommand.actionName());
            view.appendLog(">> " + playerCommand.actionName() + playerCommand.targetLabel());
        } else {
            view.appendLog("Warning: could not queue command | command=" + command
                + " | resolved=" + playerCommand
                + " | action=" + playerCommand.actionName()
                + " | target=" + playerCommand.targetLabel());
        }
    }

    public void shutdown() {
        battleExecutor.shutdownNow();
        try {
            battleExecutor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean beginBattle(String intro) {
        if (!model.beginBattle()) {
            view.appendLog("A battle is already running.");
            return false;
        }
        resetBattlePlayback();
        view.clearBattleLog();
        for (String line : intro.split("\\R")) {
            if (!line.isBlank()) {
                view.appendLog(line);
            }
        }
        view.setSetupControlsEnabled(false);
        view.showBattleLoading();
        return true;
    }

    private void runBattle(BattleLaunchRequest request) {
        try {
            BattleSetup setup = request.createSetup(setupFactory);
            SwingThread.runAndWait(() -> view.showBattleLoaded(setup));

            BattleEngine engine = new BattleEngine(setup, new SpeedTurnOrderStrategy());
            GuiPlayerDecisionProvider decisions = new GuiPlayerDecisionProvider(this);
            BattleEventListener listener = event -> SwingUtilities.invokeLater(() -> playbackController.enqueue(event));
            engine.runUntilBattleEnds(decisions, listener);
            SwingUtilities.invokeLater(() -> queuePostGame(request.replayConfiguration()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Battle execution failed", ex);
            SwingUtilities.invokeLater(() -> showBattleError(ex));
        }
    }

    private void showBattleError(Exception ex) {
        resetBattlePlayback();
        model.stopBattle();
        view.appendLog("Error: " + ex.getMessage());
        view.setSetupControlsEnabled(true);
        view.showBattleError(ex.getMessage());
    }

    private void playBattleEvent(BattleEvent event) {
        view.showBattleEvent(
            event,
            dialogueFormatter.format(event),
            consoleFormatter.format(List.of(event))
        );
    }

    private void onPlaybackDrained() {
        Optional<PlayerTurnRequest> nextTurn = model.takeQueuedPlayerTurn();
        if (nextTurn.isPresent()) {
            view.showPlayerTurn(nextTurn.get());
            return;
        }

        Optional<PostGameConfig> postGameConfig = model.takeQueuedPostGameConfig();
        postGameConfig.ifPresent(this::finishBattle);
    }

    private void queuePostGame(PostGameConfig lastConfigForPostGame) {
        model.queuePostGame(lastConfigForPostGame);
        playbackController.playNextIfIdle();
    }

    private void finishBattle(PostGameConfig lastConfig) {
        view.appendLog("Battle complete.");
        view.showBattleComplete();
        model.finishBattle();
        view.setSetupControlsEnabled(true);
        handlePostGameChoice(lastConfig);
    }

    private void handlePostGameChoice(PostGameConfig lastConfig) {
        PostGameChoice choice = view.askPostGameChoice();
        if (choice == PostGameChoice.REPLAY) {
            startBattle(BattleLaunchRequest.replay(lastConfig));
        } else if (choice == PostGameChoice.NEW_SETUP) {
            view.showNewSetupPrompt();
        } else {
            view.exitGame();
        }
    }

    private void resetBattlePlayback() {
        playbackController.reset();
        model.clearQueuedPlaybackState();
    }
}
