package sc2002.turnbased.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.TargetReference;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class CliPlayerDecisionProviderTest {
    @Test
    void decide_WhenPromptingForPlayerAction_PresentsFourActionTypesAndReturnsSingleSelectedAction() {
        // arrange
        RecordingConsoleBattleUi ui = new RecordingConsoleBattleUi(1);
        CliPlayerDecisionProvider provider = new CliPlayerDecisionProvider(ui);
        PlayerCharacter warrior = TestDependencies.warrior();
        List<Combatant> livingEnemies = List.of(TestDependencies.goblin("Goblin"));

        // act
        PlayerDecision decision = provider.decide(1, warrior, livingEnemies);

        // assert
        assertEquals(1, ui.showPlayerTurnCalls());
        assertEquals(List.of("Choose an action"), ui.prompts());
        assertEquals(
            List.of(List.of("BasicAttack", "Defend", "Item", "SpecialSkill")),
            ui.optionsShown()
        );
        assertInstanceOf(DefendAction.class, decision.action());
        assertEquals(TargetReference.none(), decision.targetReference());
    }

    private static final class RecordingConsoleBattleUi extends ConsoleBattleUi {
        private final Queue<Integer> selections = new ArrayDeque<>();
        private final List<String> prompts = new ArrayList<>();
        private final List<List<String>> optionsShown = new ArrayList<>();
        private int showPlayerTurnCalls;

        private RecordingConsoleBattleUi(int... selections) {
            super(
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new PrintStream(OutputStream.nullOutputStream()),
                TestDependencies.combatantFactory()
            );
            for (int selection : selections) {
                this.selections.add(selection);
            }
        }

        @Override
        public void showPlayerTurn(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
            showPlayerTurnCalls++;
        }

        @Override
        public int promptForSelection(String prompt, List<String> options) {
            prompts.add(prompt);
            optionsShown.add(List.copyOf(options));
            Integer selection = selections.poll();
            if (selection == null) {
                throw new AssertionError("No scripted selection configured for prompt: " + prompt);
            }
            return selection;
        }

        private int showPlayerTurnCalls() {
            return showPlayerTurnCalls;
        }

        private List<String> prompts() {
            return List.copyOf(prompts);
        }

        private List<List<String>> optionsShown() {
            return List.copyOf(optionsShown);
        }
    }
}
