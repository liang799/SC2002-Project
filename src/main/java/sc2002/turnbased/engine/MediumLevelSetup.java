package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.Warrior;
import sc2002.turnbased.domain.Wizard;
import sc2002.turnbased.domain.Wolf;

public final class MediumLevelSetup {
    private MediumLevelSetup() {
    }

    public static BattleSetup createWarriorPowerStonePotionSetup() {
        Inventory inventory = new Inventory();
        inventory.add(ItemType.POWER_STONE, 1);
        inventory.add(ItemType.POTION, 1);

        return new BattleSetup(
            new Warrior(),
            List.of(new Goblin("Goblin"), new Wolf("Wolf")),
            List.of(new Wolf("Wolf A"), new Wolf("Wolf B")),
            inventory
        );
    }

    public static BattleSetup createWizardPowerStonePotionSetup() {
        Inventory inventory = new Inventory();
        inventory.add(ItemType.POWER_STONE, 1);
        inventory.add(ItemType.POTION, 1);

        PlayerCharacter wizard = new Wizard();
        return new BattleSetup(
            wizard,
            List.of(new Goblin("Goblin"), new Wolf("Wolf")),
            List.of(new Wolf("Wolf A"), new Wolf("Wolf B")),
            inventory
        );
    }
}
