package sc2002.turnbased.engine;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.bootstrap.CombatantFactories;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.DefaultStatusEffectRegistryFactory;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public final class HardLevelSetup {
    private HardLevelSetup() {
    }

    public static BattleSetup create(PlayerCharacter player, Inventory inventory) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(inventory, "inventory");

        List<ItemType> selectedItems = new java.util.ArrayList<>();
        for (ItemType itemType : ItemType.values()) {
            for (int count = 0; count < inventory.countOf(itemType); count++) {
                selectedItems.add(itemType);
            }
        }
        return new BattleSetupFactory(CombatantFactories.createDefault(
            new DefaultStatusEffectRegistryFactory(StatusEffectRegistry::new),
            new BasicAttackAction(),
            new ShieldBashAction(),
            new ArcaneBlastAction()
        )).create(
            new GameConfiguration(resolvePlayerType(player), DifficultyLevel.HARD, selectedItems)
        );
    }

    private static PlayerType resolvePlayerType(PlayerCharacter player) {
        return java.util.Arrays.stream(PlayerType.values())
            .filter(playerType -> playerType.getDisplayName().equals(player.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported player class: " + player.getName()));
    }
}
