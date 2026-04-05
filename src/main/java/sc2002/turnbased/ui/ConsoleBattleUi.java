package sc2002.turnbased.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Inventory;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.Warrior;
import sc2002.turnbased.domain.Wizard;
import sc2002.turnbased.domain.Wolf;
import sc2002.turnbased.engine.DifficultyLevel;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.PlayerType;
import sc2002.turnbased.engine.WaveSpec;

public class ConsoleBattleUi {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleBattleUi(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    public void showLoadingScreen() {
        out.println("=== SC2002 Turn-Based Combat Arena ===");
        out.println();
        out.println("Playable Classes:");
        showPlayerPreview(PlayerType.WARRIOR, new Warrior());
        showPlayerPreview(PlayerType.WIZARD, new Wizard());
        out.println();
        out.println("Usable Items:");
        for (ItemType itemType : ItemType.values()) {
            out.println("- " + itemType.getDisplayName() + ": " + itemType.getDescription());
        }
        out.println();
        out.println("Enemy Types:");
        showEnemyPreview(new Goblin("Goblin"));
        showEnemyPreview(new Wolf("Wolf"));
        out.println();
        out.println("Difficulty Levels and Enemy Counts:");
        for (DifficultyLevel difficultyLevel : DifficultyLevel.values()) {
            out.println("- " + difficultyLevel.getDisplayName()
                + ": Initial Spawn " + difficultyLevel.getInitialSpawnDescription()
                + " (" + difficultyLevel.getInitialEnemyCount() + " enemies)"
                + ", Backup Spawn " + difficultyLevel.getBackupSpawnDescription()
                + " (" + difficultyLevel.getBackupEnemyCount() + " enemies)"
                + ", Total " + difficultyLevel.getTotalEnemyCount() + " enemies");
        }
        out.println();
        out.println("You will choose 1 player class, 2 starting items, and 1 difficulty level.");
        out.println("Duplicate items are allowed.");
        out.println();
    }

    public PlayerType promptForPlayerType() {
        List<String> options = new ArrayList<>();
        for (PlayerType playerType : PlayerType.values()) {
            options.add(playerType.getDisplayName());
        }
        return PlayerType.values()[promptForSelection("Choose a player class", options)];
    }

    public DifficultyLevel promptForDifficultyOrCustom() {
        List<String> options = new ArrayList<>();
        for (DifficultyLevel difficultyLevel : DifficultyLevel.values()) {
            options.add(difficultyLevel.getDisplayName());
        }
        options.add("Custom Mode (build your own waves)");
        int choice = promptForSelection("Choose a difficulty level", options);
        if (choice == DifficultyLevel.values().length) {
            return null; // signals custom mode
        }
        return DifficultyLevel.values()[choice];
    }

    @Deprecated
    public DifficultyLevel promptForDifficultyLevel() {
        List<String> options = new ArrayList<>();
        for (DifficultyLevel difficultyLevel : DifficultyLevel.values()) {
            options.add(difficultyLevel.getDisplayName());
        }
        return DifficultyLevel.values()[promptForSelection("Choose a difficulty level", options)];
    }

    public int promptForWaveCount() {
        return promptForSelection("How many waves?", List.of("1 wave", "2 waves")) + 1;
    }

    public WaveSpec promptForWaveSpec(int waveNumber) {
        out.println("Configure Wave " + waveNumber + " (max 4 enemies total, max 3 of each type):");
        while (true) {
            int goblinCount = promptForCount("  Goblins (HP:55 ATK:35 DEF:15 SPD:25)", 0, 3);
            int wolfCount   = promptForCount("  Wolves  (HP:40 ATK:45 DEF:5  SPD:35)", 0, 3);
            if (goblinCount + wolfCount == 0) {
                out.println("  A wave needs at least 1 enemy. Please try again.");
                continue;
            }
            if (goblinCount + wolfCount > 4) {
                out.println("  That's " + (goblinCount + wolfCount) + " enemies - maximum is 4. Please try again.");
                continue;
            }
            return new WaveSpec(goblinCount, wolfCount);
        }
    }

    private int promptForCount(String label, int min, int max) {
        while (true) {
            out.print(label + " [" + min + "-" + max + "]: ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            out.println("  Please enter a number between " + min + " and " + max + ".");
        }
    }

    public List<ItemType> promptForItems(int itemCount) {
        out.println("Select your starting items. You may choose duplicates.");
        List<ItemType> selectedItems = new ArrayList<>();
        for (int selectionIndex = 1; selectionIndex <= itemCount; selectionIndex++) {
            List<String> options = new ArrayList<>();
            for (ItemType itemType : ItemType.values()) {
                options.add(itemType.getDisplayName() + " - " + itemType.getDescription());
            }
            int choice = promptForSelection("Choose item " + selectionIndex + " of " + itemCount, options);
            selectedItems.add(ItemType.values()[choice]);
        }
        return selectedItems;
    }

    public void showConfigurationSummary(GameConfiguration configuration) {
        out.println();
        out.println("=== Selected Configuration ===");
        out.println("Player Class: " + configuration.playerType().getDisplayName()
            + " | Special Skill: " + configuration.playerType().getSpecialSkillName());
        out.println("Difficulty: " + configuration.difficultyLevel().getDisplayName());
        out.println("Initial Enemies: " + configuration.difficultyLevel().getInitialSpawnDescription());
        out.println("Backup Spawn: " + configuration.difficultyLevel().getBackupSpawnDescription());
        out.println("Starting Items: " + formatSelectedItems(configuration.selectedItems()));
        out.println();
    }

    public void showPlayerTurn(int roundNumber, PlayerCharacter player, List<Combatant> livingEnemies, Inventory inventory) {
        out.println();
        out.println("Round " + roundNumber + " - " + player.getName() + "'s Turn");
        out.println("Player: HP " + player.getCurrentHp() + "/" + player.getMaxHp()
            + " | ATK " + player.getAttack()
            + " | DEF " + player.getDefense()
            + " | SPD " + player.getSpeed()
            + " | Cooldown " + player.getSpecialSkillCooldown());
        out.println("Items: " + formatInventory(inventory));
        out.println("Enemies:");
        for (Combatant enemy : livingEnemies) {
            String statusSuffix = enemy.getActiveStatusNames().isEmpty()
                ? ""
                : " [" + String.join(", ", enemy.getActiveStatusNames()) + "]";
            out.println("- " + enemy.getName() + ": HP " + enemy.getCurrentHp() + "/" + enemy.getMaxHp() + statusSuffix);
        }
    }

    public int promptForSelection(String prompt, List<String> options) {
        while (true) {
            out.println(prompt + ":");
            for (int index = 0; index < options.size(); index++) {
                out.println((index + 1) + ". " + options.get(index));
            }
            out.print("> ");

            String input = scanner.nextLine().trim();
            try {
                int selectedIndex = Integer.parseInt(input) - 1;
                if (selectedIndex >= 0 && selectedIndex < options.size()) {
                    return selectedIndex;
                }
            } catch (NumberFormatException ignored) {
            }
            out.println("Invalid selection. Please try again.");
        }
    }

    public void showMessage(String message) {
        out.println(message);
    }

    public void showBattleTranscript(List<String> lines) {
        out.println();
        out.println("=== Battle Transcript ===");
        for (String line : lines) {
            out.println(line);
        }
        out.println();
    }

    public void showBattleLines(List<String> lines) {
        for (String line : lines) {
            out.println(line);
        }
    }

    public PostGameChoice promptPostGameChoice() {
        int selection = promptForSelection(
            "What would you like to do next",
            List.of("Replay with same settings", "Start a new game", "Exit")
        );
        return PostGameChoice.values()[selection];
    }

    private void showPlayerPreview(PlayerType playerType, Combatant combatant) {
        out.println("- " + combatant.getName()
            + ": HP " + combatant.getMaxHp()
            + ", ATK " + combatant.getBaseAttack()
            + ", DEF " + combatant.getDefense()
            + ", SPD " + combatant.getSpeed()
            + ", Special Skill " + playerType.getSpecialSkillName());
    }

    private void showEnemyPreview(Combatant combatant) {
        out.println("- " + combatant.getName()
            + ": HP " + combatant.getMaxHp()
            + ", ATK " + combatant.getBaseAttack()
            + ", DEF " + combatant.getDefense()
            + ", SPD " + combatant.getSpeed());
    }

    private String formatInventory(Inventory inventory) {
        List<String> parts = new ArrayList<>();
        for (ItemType itemType : ItemType.values()) {
            if (inventory.countOf(itemType) > 0) {
                parts.add(itemType.getDisplayName() + " x" + inventory.countOf(itemType));
            }
        }
        if (parts.isEmpty()) {
            return "None";
        }
        return String.join(", ", parts);
    }

    private String formatSelectedItems(List<ItemType> selectedItems) {
        List<String> itemNames = new ArrayList<>();
        for (ItemType selectedItem : selectedItems) {
            itemNames.add(selectedItem.getDisplayName());
        }
        return String.join(", ", itemNames);
    }
}
