package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.event.ArcanePowerAppliedEvent;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestCombatantBuilder;
import sc2002.turnbased.support.TestDependencies;

@Tag("unit")
class ArcaneBlastActionTest {
    @Test
    void execute_whenEnemiesAreEliminated_addsAttackBuffThroughStatusEffects() {
        PlayerCharacter wizard = TestDependencies.wizard();
        Combatant goblin = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .withCurrentHp(30)
            .withMaxHp(30)
            .withDefense(0)
            .build();
        Combatant wolf = TestCombatantBuilder.aCombatant()
            .named("Wolf")
            .withCurrentHp(30)
            .withMaxHp(30)
            .withDefense(0)
            .build();
        ArcaneBlastAction action = new ArcaneBlastAction();

        List<BattleEvent> events = action.execute(new TestActionExecutionContext(List.of(goblin, wolf)), wizard, null);

        assertEquals(3, events.size());
        assertEquals(70, wizard.getAttack());
        assertEquals(50, wizard.getBaseAttack());
        assertEquals(List.of("ARCANE POWER +20"), wizard.getActiveStatusNames());
        assertEquals(true, ((ActionEvent) events.get(1)).isTargetEliminated());
        assertEquals(List.of(new ArcanePowerAppliedEvent("Wizard", 10)), ((ActionEvent) events.get(1)).getStatusEffectEvents());
        assertEquals(true, ((ActionEvent) events.get(2)).isTargetEliminated());
        assertEquals(List.of(new ArcanePowerAppliedEvent("Wizard", 20)), ((ActionEvent) events.get(2)).getStatusEffectEvents());
    }

    private record TestActionExecutionContext(List<Combatant> livingEnemiesInTurnOrder) implements ActionExecutionContext {
        @Override
        public List<Combatant> getLivingEnemies() {
            return livingEnemiesInTurnOrder();
        }

        @Override
        public List<Combatant> getLivingEnemiesInTurnOrder() {
            return livingEnemiesInTurnOrder();
        }

        @Override
        public Inventory getInventory() {
            return new Inventory();
        }
    }
}
