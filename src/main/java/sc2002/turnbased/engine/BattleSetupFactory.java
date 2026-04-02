package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.Wolf;

public class BattleSetupFactory {
    public BattleSetup create(GameConfiguration configuration) {
        PlayerCharacter player = configuration.playerType().createPlayer();
        Inventory inventory = createInventory(configuration.selectedItems());

        return switch (configuration.difficultyLevel()) {
            case EASY -> new BattleSetup(
                player,
                List.of(new Goblin("Goblin A"), new Goblin("Goblin B"), new Goblin("Goblin C")),
                List.of(),
                inventory
            );
            case MEDIUM -> new BattleSetup(
                player,
                List.of(new Goblin("Goblin"), new Wolf("Wolf")),
                List.of(new Wolf("Wolf A"), new Wolf("Wolf B")),
                inventory
            );
            case HARD -> new BattleSetup(
                player,
                List.of(new Goblin("Goblin A"), new Goblin("Goblin B")),
                List.of(new Goblin("Goblin C"), new Wolf("Wolf A"), new Wolf("Wolf B")),
                inventory
            );
        };
    }

    private Inventory createInventory(List<ItemType> selectedItems) {
        Inventory inventory = new Inventory();
        for (ItemType itemType : selectedItems) {
            inventory.add(itemType, 1);
        }
        return inventory;
    }
}
