package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.Warrior;

public final class EasyLevelSetup {
    private EasyLevelSetup() {
    }

    public static BattleSetup createWarriorPotionSmokeBombSetup() {
        Inventory inventory = new Inventory();
        inventory.add(ItemType.POTION, 1);
        inventory.add(ItemType.SMOKE_BOMB, 1);

        return new BattleSetup(
            new Warrior(),
            List.of(new Goblin("Goblin A"), new Goblin("Goblin B"), new Goblin("Goblin C")),
            List.of(),
            inventory
        );
    }
}
