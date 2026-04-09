package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestActionExecutionContext;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class ArcaneBlastActionTest {
    @Test
    void execute_WhenEnemiesAreEliminated_AddsAttackBuffThroughStatusEffects() {
        PlayerCharacter wizard = TestDependencies.wizard();
        Combatant goblin = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withHp(30)
            .withDefense(0)
            .build();
        Combatant wolf = TestCombatantBuilder.aCombatant()
            .named("Wolf")
            .withHp(30)
            .withDefense(0)
            .build();
        ArcaneBlastAction action = new ArcaneBlastAction();

        List<BattleEvent> events = action.execute(new TestActionExecutionContext(List.of(goblin, wolf)), wizard, null);
        ActionEvent goblinEvent = assertInstanceOf(ActionEvent.class, events.get(1));
        ActionEvent wolfEvent = assertInstanceOf(ActionEvent.class, events.get(2));

        assertEquals(3, events.size());
        assertEquals(70, wizard.getAttack());
        assertEquals(50, wizard.getBaseAttack());
        assertEquals(List.of("ARCANE POWER +20"), wizard.getActiveStatuses());
        assertTrue(goblinEvent.isTargetEliminated());
        assertEquals(List.of("Wizard gains ARCANE POWER +10"), goblinEvent.getStatusEffectNotes());
        assertTrue(wolfEvent.isTargetEliminated());
        assertEquals(List.of("Wizard gains ARCANE POWER +20"), wolfEvent.getStatusEffectNotes());
    }
}
