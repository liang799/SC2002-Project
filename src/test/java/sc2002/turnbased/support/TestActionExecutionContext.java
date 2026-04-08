package sc2002.turnbased.support;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;

public final class TestActionExecutionContext implements ActionExecutionContext {
    private final List<Combatant> livingEnemies;
    private final Inventory inventory;

    public TestActionExecutionContext(List<Combatant> livingEnemies) {
        this(livingEnemies, new Inventory());
    }

    public TestActionExecutionContext(List<Combatant> livingEnemies, Inventory inventory) {
        this.livingEnemies = List.copyOf(Objects.requireNonNull(livingEnemies, "livingEnemies"));
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    @Override
    public List<Combatant> getLivingEnemies() {
        return livingEnemies;
    }

    @Override
    public List<Combatant> getLivingEnemiesInTurnOrder() {
        return livingEnemies;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
