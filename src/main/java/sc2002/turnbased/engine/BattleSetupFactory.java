package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
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

    public BattleSetup createCustom(CustomGameConfiguration config) {
        PlayerCharacter player = config.playerType().createPlayer();
        Inventory inventory = createInventory(config.selectedItems());

        int[] goblinIdx = {0};
        int[] wolfIdx = {0};

        List<Combatant> wave1 = buildWave(config.waves().get(0), goblinIdx, wolfIdx);
        List<Combatant> wave2 = config.waves().size() > 1
            ? buildWave(config.waves().get(1), goblinIdx, wolfIdx)
            : List.of();

        return new BattleSetup(player, wave1, wave2, inventory);
    }

    private static final char[] LABELS = {'A', 'B', 'C', 'D', 'E', 'F'};

    private List<Combatant> buildWave(WaveSpec spec, int[] goblinIdx, int[] wolfIdx) {
        List<Combatant> enemies = new ArrayList<>();
        for (int i = 0; i < spec.goblinCount(); i++) {
            enemies.add(new Goblin("Goblin " + LABELS[goblinIdx[0]++]));
        }
        for (int i = 0; i < spec.wolfCount(); i++) {
            enemies.add(new Wolf("Wolf " + LABELS[wolfIdx[0]++]));
        }
        return enemies;
    }

    private Inventory createInventory(List<ItemType> selectedItems) {
        Inventory inventory = new Inventory();
        for (ItemType itemType : selectedItems) {
            inventory.add(itemType, 1);
        }
        return inventory;
    }
}
