package sc2002.turnbased.ui.gui.playback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.NarrationEvent;
import sc2002.turnbased.report.RoundStartEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

class BattleDialogueFormatterTest {
    private final BattleDialogueFormatter formatter = new BattleDialogueFormatter();

    @Test
    void formatsActionEventAsReadableBattleDialogue() {
        ActionEvent event = new ActionEvent(
            CombatantId.generate(),
            "Warrior",
            "BasicAttack",
            CombatantId.generate(),
            "Goblin",
            25,
            0,
            40,
            15,
            25,
            true,
            List.of("STUN applied")
        );

        String text = formatter.format(event);

        assertEquals("Warrior used BasicAttack! Goblin took 25 damage. Goblin fainted! STUN applied", text);
    }

    @Test
    void formatsBlockedActionWithoutDamageText() {
        ActionEvent event = new ActionEvent(
            CombatantId.generate(),
            "Goblin",
            "BasicAttack",
            CombatantId.generate(),
            "Warrior",
            260,
            260,
            35,
            40,
            0,
            false,
            List.of()
        );

        String text = formatter.format(event);

        assertEquals("Goblin used BasicAttack! Warrior blocked the hit.", text);
    }

    @Test
    void cleansNarrationArrowSyntaxForDialogue() {
        String text = formatter.format(new NarrationEvent("Wizard -> Item -> Potion used"));

        assertEquals("Wizard Item Potion used", text);
    }

    @Test
    void formatsRoundAndStatusEvents() {
        assertEquals("Round 3 started!", formatter.format(new RoundStartEvent(3)));
        assertEquals(
            "Smoke Bomb active Arcane Power faded",
            formatter.format(new StatusEffectReportEvent(List.of("Smoke Bomb active", "Arcane Power faded")))
        );
    }

    @Test
    void playbackDelayIsBoundedForLongMessages() {
        ActionEvent event = new ActionEvent(
            CombatantId.generate(),
            "Wizard",
            "Arcane Blast",
            CombatantId.generate(),
            "Wolf",
            40,
            0,
            50,
            5,
            45,
            true,
            List.of("A very long status narration that should not make the battle wait forever")
        );

        int delayMillis = formatter.playbackDelayMillis(event);

        assertTrue(delayMillis >= 1_350);
        assertTrue(delayMillis <= 2_000);
    }
}
