package sc2002.turnbased.ui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

/**
 * Graphical player input: shows dialogs on the UI thread while the battle engine waits.
 * Same decision rules as {@link sc2002.turnbased.ui.CliPlayerDecisionProvider}.
 */
public class GuiPlayerDecisionProvider implements PlayerDecisionProvider {
    private final JFrame owner;
    private final Inventory inventory;

    public GuiPlayerDecisionProvider(JFrame owner, Inventory inventory) {
        this.owner = owner;
        this.inventory = inventory;
    }

    @Override
    public PlayerDecision decide(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        while (true) {
            PlayerDecision decision = promptActionLoop(roundNumber, player, livingEnemies);
            if (decision != null) {
                return decision;
            }
        }
    }

    private PlayerDecision promptActionLoop(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        AtomicReference<PlayerDecision> result = new AtomicReference<>();

        runOnEdt(() -> {
            String header = turnSummary(roundNumber, player, livingEnemies);
            String[] options = {
                "BasicAttack",
                "Defend",
                "Item",
                player.canUseSpecialSkill()
                    ? "SpecialSkill"
                    : "SpecialSkill (Cooldown " + player.getSpecialSkillCooldown() + ")"
            };
            int choice = JOptionPane.showOptionDialog(
                owner,
                header,
                "Your turn",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (choice == JOptionPane.CLOSED_OPTION) {
                result.set(null);
                return;
            }
            switch (choice) {
                case 0:
                    result.set(createDecision(new BasicAttackAction(), player, livingEnemies));
                    break;
                case 1:
                    result.set(PlayerDecision.untargeted(new DefendAction()));
                    break;
                case 2:
                    PlayerDecision item = promptItemDecision(player, livingEnemies);
                    result.set(item);
                    break;
                case 3:
                    if (!player.canUseSpecialSkill()) {
                        JOptionPane.showMessageDialog(owner, "SpecialSkill is still on cooldown.", "Invalid", JOptionPane.WARNING_MESSAGE);
                        result.set(null);
                    } else {
                        result.set(createDecision(new UseSpecialSkillAction(), player, livingEnemies));
                    }
                    break;
                default:
                    result.set(null);
            }
        });

        return result.get();
    }

    private PlayerDecision promptItemDecision(PlayerCharacter player, List<Combatant> livingEnemies) {
        AtomicReference<PlayerDecision> result = new AtomicReference<>();
        runOnEdt(() -> {
            List<ItemType> available = new ArrayList<>();
            for (ItemType itemType : ItemType.values()) {
                if (inventory.countOf(itemType) > 0) {
                    available.add(itemType);
                }
            }
            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(owner, "No items remaining.", "Items", JOptionPane.INFORMATION_MESSAGE);
                result.set(null);
                return;
            }
            String[] labels = available.stream()
                .map(t -> t.getDisplayName() + " x" + inventory.countOf(t))
                .toArray(String[]::new);
            int idx = JOptionPane.showOptionDialog(
                owner,
                "Choose an item",
                "Item",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                labels,
                labels[0]
            );
            if (idx == JOptionPane.CLOSED_OPTION) {
                result.set(null);
                return;
            }
            ItemType selected = available.get(idx);
            result.set(switch (selected) {
                case POTION -> createDecision(new UsePotionAction(), player, livingEnemies);
                case POWER_STONE -> createDecision(new UsePowerStoneSkillAction(), player, livingEnemies);
                case SMOKE_BOMB -> createDecision(new UseSmokeBombAction(), player, livingEnemies);
            });
        });
        return result.get();
    }

    private PlayerDecision createDecision(BattleAction action, PlayerCharacter player, List<Combatant> livingEnemies) {
        if (action.targetingMode(player) == TargetingMode.NONE) {
            return PlayerDecision.untargeted(action);
        }
        AtomicReference<PlayerDecision> result = new AtomicReference<>();
        runOnEdt(() -> {
            String[] labels = livingEnemies.stream()
                .map(e -> e.getName() + " (HP " + e.getCurrentHp() + ")")
                .toArray(String[]::new);
            int idx = JOptionPane.showOptionDialog(
                owner,
                "Choose a target",
                "Target",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                labels,
                labels[0]
            );
            if (idx == JOptionPane.CLOSED_OPTION) {
                result.set(null);
                return;
            }
            result.set(PlayerDecision.targeted(action, livingEnemies.get(idx).getName()));
        });
        return result.get();
    }

    private static String turnSummary(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies) {
        StringBuilder b = new StringBuilder();
        b.append("Round ").append(roundNumber).append(" — ").append(player.getName()).append("\n");
        b.append("HP ").append(player.getCurrentHp()).append("/").append(player.getMaxHp());
        b.append(" | ATK ").append(player.getAttack());
        b.append(" | DEF ").append(player.getDefense());
        b.append(" | SPD ").append(player.getSpeed());
        b.append(" | Cooldown ").append(player.getSpecialSkillCooldown()).append("\n\nEnemies:\n");
        for (Combatant e : livingEnemies) {
            b.append("- ").append(e.getName()).append(": HP ").append(e.getCurrentHp()).append("/").append(e.getMaxHp());
            List<String> activeStatuses = e.getActiveStatuses();
            if (!activeStatuses.isEmpty()) {
                b.append(" [").append(String.join(", ", activeStatuses)).append("]");
            }
            b.append("\n");
        }
        b.append("\nChoose an action:");
        return b.toString();
    }

    private static void runOnEdt(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
