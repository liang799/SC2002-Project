package sc2002.turnbased.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.Wizard;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class ArcaneBlastActionTest {
    @Test
    void execute_whenEnemiesAreEliminated_addsAttackBuffThroughStatusEffects() {
        Wizard wizard = new Wizard();
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
        assertEquals(List.of("ELIMINATED", "Wizard ATK: 50 -> 60 (+10)"), ((ActionEvent) events.get(1)).getNotes());
        assertEquals(List.of("ELIMINATED", "Wizard ATK: 60 -> 70 (+10)"), ((ActionEvent) events.get(2)).getNotes());
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
