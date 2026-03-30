package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import sc2002.turnbased.domain.Combatant;

public class SpeedTurnOrderStrategy implements TurnOrderStrategy {
    @Override
    public List<Combatant> determineOrder(List<Combatant> combatants) {
        List<Combatant> ordered = new ArrayList<>(combatants);
        ordered.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());
        return ordered;
    }
}
