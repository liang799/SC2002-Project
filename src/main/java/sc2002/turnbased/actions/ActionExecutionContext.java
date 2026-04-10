package sc2002.turnbased.actions;

import java.util.List;

import sc2002.turnbased.domain.Combatant;

public interface ActionExecutionContext {
    List<Combatant> getLivingEnemies();

    List<Combatant> getLivingEnemiesInTurnOrder();
}
