package sc2002.turnbased.ui.gui.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.engine.PlayerDecision;

class ResolvedPlayerCommandTest {
    @Test
    void rejectsNullComponents() {
        PlayerDecision decision = PlayerDecision.untargeted(new DefendAction());

        assertThrows(NullPointerException.class, () -> new ResolvedPlayerCommand(null, "Defend", ""));
        assertThrows(NullPointerException.class, () -> new ResolvedPlayerCommand(decision, null, ""));
        assertThrows(NullPointerException.class, () -> new ResolvedPlayerCommand(decision, "Defend", null));
    }
}
