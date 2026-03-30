package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.Combatant;

public interface TurnOrderStrategy {
    List<Combatant> determineOrder(List<Combatant> combatants);
}
