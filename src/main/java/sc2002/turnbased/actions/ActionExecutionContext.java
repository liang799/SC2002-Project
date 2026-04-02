package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;

public interface ActionExecutionContext {
    List<Combatant> getLivingEnemies();

    List<Combatant> getLivingEnemiesInTurnOrder();

    Inventory getInventory();
}
