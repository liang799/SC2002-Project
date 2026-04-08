package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;

import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;

public class BattleSetupFactory {
    private static final char[] LABELS = {'A', 'B', 'C', 'D', 'E', 'F'};
    private final CombatantFactory combatantFactory;

    public BattleSetupFactory(CombatantFactory combatantFactory) {
        this.combatantFactory = Objects.requireNonNull(combatantFactory, "combatantFactory");
    }

    public BattleSetup create(GameConfiguration configuration) {
        PlayerCharacter player = configuration.playerType().createPlayer(combatantFactory);
        Inventory inventory = createInventory(configuration.selectedItems());

        return switch (configuration.difficultyLevel()) {
            case EASY -> new BattleSetup(
                player,
                List.of(
                    EnemyType.GOBLIN.create("Goblin A", combatantFactory),
                    EnemyType.GOBLIN.create("Goblin B", combatantFactory),
                    EnemyType.GOBLIN.create("Goblin C", combatantFactory)
                ),
                List.of(),
                inventory
            );
            case MEDIUM -> new BattleSetup(
                player,
                List.of(
                    EnemyType.GOBLIN.create("Goblin", combatantFactory),
                    EnemyType.WOLF.create("Wolf", combatantFactory)
                ),
                List.of(
                    EnemyType.WOLF.create("Wolf A", combatantFactory),
                    EnemyType.WOLF.create("Wolf B", combatantFactory)
                ),
                inventory
            );
            case HARD -> new BattleSetup(
                player,
                List.of(
                    EnemyType.GOBLIN.create("Goblin A", combatantFactory),
                    EnemyType.GOBLIN.create("Goblin B", combatantFactory)
                ),
                List.of(
                    EnemyType.GOBLIN.create("Goblin C", combatantFactory),
                    EnemyType.WOLF.create("Wolf A", combatantFactory),
                    EnemyType.WOLF.create("Wolf B", combatantFactory)
                ),
                inventory
            );
        };
    }

    public BattleSetup createCustom(CustomGameConfiguration config) {
        PlayerCharacter player = config.playerType().createPlayer(combatantFactory);
        Inventory inventory = createInventory(config.selectedItems());
        return buildSetup(player, inventory, config.waves());
    }

    private BattleSetup buildSetup(PlayerCharacter player, Inventory inventory, List<WaveSpec> waves) {
        Objects.requireNonNull(waves, "waves");
        if (waves.isEmpty() || waves.size() > 2) {
            throw new IllegalArgumentException("Battle setup requires 1 or 2 waves, got: " + waves.size());
        }

        Map<EnemyFactory, Integer> nextLabelByFactory = new java.util.LinkedHashMap<>();
        List<Combatant> wave1 = buildWave(waves.get(0), nextLabelByFactory);
        List<Combatant> wave2 = waves.size() > 1
            ? buildWave(waves.get(1), nextLabelByFactory)
            : List.of();
        return new BattleSetup(player, wave1, wave2, inventory);
    }

    private List<Combatant> buildWave(WaveSpec spec, Map<EnemyFactory, Integer> nextLabelByFactory) {
        List<Combatant> enemies = new ArrayList<>();
        for (EnemyCount enemyCount : spec.enemyCounts()) {
            for (int i = 0; i < enemyCount.count(); i++) {
                enemies.add(createEnemy(enemyCount.enemyFactory(), nextLabelByFactory));
            }
        }
        return enemies;
    }

    private Combatant createEnemy(EnemyFactory enemyFactory, Map<EnemyFactory, Integer> nextLabelByFactory) {
        int nextIndex = nextLabelByFactory.getOrDefault(enemyFactory, 0);
        nextLabelByFactory.put(enemyFactory, nextIndex + 1);
        return enemyFactory.create(formatEnemyName(enemyFactory, nextIndex), combatantFactory);
    }

    private String formatEnemyName(EnemyFactory enemyFactory, int enemyIndex) {
        if (enemyIndex < LABELS.length) {
            return enemyFactory.getDisplayName() + " " + LABELS[enemyIndex];
        }
        return enemyFactory.getDisplayName() + " " + (enemyIndex + 1);
    }

    private Inventory createInventory(List<ItemType> selectedItems) {
        Inventory inventory = new Inventory();
        for (ItemType itemType : selectedItems) {
            inventory.add(itemType, 1);
        }
        return inventory;
    }
}
