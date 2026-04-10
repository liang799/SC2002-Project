package sc2002.turnbased.ui;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.actions.TargetingMode;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UsePowerStoneSkillAction;
import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.engine.PlayerDecisionProvider;

public class CliPlayerDecisionProvider implements PlayerDecisionProvider {
    private final ConsoleBattleUi ui;
    private final Inventory inventory;

    public CliPlayerDecisionProvider(ConsoleBattleUi ui, Inventory inventory) {
        this.ui = ui;
        this.inventory = inventory;
    }

    @Override
    public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        while (true) {
            ui.showPlayerTurn(roundNumber, player, livingEnemies, inventory);

            List<String> options = new ArrayList<>();
            options.add("BasicAttack");
            options.add("Defend");
            options.add("Item");
            if (player.canUseSpecialSkill()) {
                options.add("SpecialSkill");
            } else {
                options.add("SpecialSkill (Cooldown " + player.getSpecialSkillCooldown() + ")");
            }

            int actionSelection = ui.promptForSelection("Choose an action", options);
            switch (actionSelection) {
                case 0:
                    return createDecision(new BasicAttackAction(), player, livingEnemies);
                case 1:
                    return PlayerDecision.untargeted(new DefendAction());
                case 2:
                    PlayerDecision itemDecision = promptForItemDecision(player, livingEnemies);
                    if (itemDecision != null) {
                        return itemDecision;
                    }
                    break;
                case 3:
                    if (!player.canUseSpecialSkill()) {
                        ui.showMessage("SpecialSkill is still on cooldown.");
                        break;
                    }
                    return createDecision(new UseSpecialSkillAction(), player, livingEnemies);
                default:
                    break;
            }
        }
    }

    private PlayerDecision promptForItemDecision(PlayerCharacter player, List<Combatant> livingEnemies) {
        List<ItemType> availableItems = new ArrayList<>();
        for (ItemType itemType : ItemType.values()) {
            if (inventory.countOf(itemType) > 0) {
                availableItems.add(itemType);
            }
        }
        if (availableItems.isEmpty()) {
            ui.showMessage("No items remaining.");
            return null;
        }

        List<String> options = new ArrayList<>();
        for (ItemType itemType : availableItems) {
            options.add(itemType.getDisplayName() + " x" + inventory.countOf(itemType));
        }

        ItemType selectedItem = availableItems.get(ui.promptForSelection("Choose an item", options));
        return switch (selectedItem) {
            case POTION -> createDecision(new UsePotionAction(), player, livingEnemies);
            case POWER_STONE -> createDecision(new UsePowerStoneSkillAction(), player, livingEnemies);
            case SMOKE_BOMB -> createDecision(new UseSmokeBombAction(), player, livingEnemies);
        };
    }

    private PlayerDecision createDecision(BattleAction action, PlayerCharacter player, List<Combatant> livingEnemies) {
        if (action.targetingMode(player) == TargetingMode.NONE) {
            return PlayerDecision.untargeted(action);
        }

        List<String> enemyOptions = livingEnemies.stream()
            .map(enemy -> enemy.getName() + " (HP " + enemy.getCurrentHp() + ")")
            .toList();
        int targetSelection = ui.promptForSelection("Choose a target", enemyOptions);
        return PlayerDecision.targeted(action, livingEnemies.get(targetSelection));
    }
}
