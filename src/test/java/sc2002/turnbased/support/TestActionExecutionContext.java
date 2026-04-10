package sc2002.turnbased.support;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;

public final class TestActionExecutionContext implements ActionExecutionContext {
    private final List<Combatant> livingEnemies;

    public TestActionExecutionContext(List<Combatant> livingEnemies) {
        this.livingEnemies = List.copyOf(Objects.requireNonNull(livingEnemies, "livingEnemies"));
    }

    @Override
    public List<Combatant> getLivingEnemies() {
        return livingEnemies;
    }

    @Override
    public List<Combatant> getLivingEnemiesInTurnOrder() {
        return livingEnemies;
    }
}
