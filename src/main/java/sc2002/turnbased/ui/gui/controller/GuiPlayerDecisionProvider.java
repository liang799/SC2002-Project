package sc2002.turnbased.ui.gui.controller;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;

/**
 * Bridges the blocking battle engine decision API to the interactive Swing arena.
 */
public class GuiPlayerDecisionProvider implements PlayerDecisionProvider {
    private final BattleController controller;

    GuiPlayerDecisionProvider(BattleController controller) {
        this.controller = Objects.requireNonNull(controller, "controller");
    }

    @Override
    public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        return controller.awaitPlayerDecision(roundNumber, player, livingEnemies);
    }
}
