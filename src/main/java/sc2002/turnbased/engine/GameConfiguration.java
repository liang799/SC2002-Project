package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.ItemType;

public record GameConfiguration(PlayerType playerType, DifficultyLevel difficultyLevel, List<ItemType> selectedItems) {
    public GameConfiguration {
        Objects.requireNonNull(playerType, "playerType");
        Objects.requireNonNull(difficultyLevel, "difficultyLevel");
        Objects.requireNonNull(selectedItems, "selectedItems");
        selectedItems = List.copyOf(new ArrayList<>(selectedItems));
        if (selectedItems.size() != 2) {
            throw new IllegalArgumentException("Exactly 2 starting items must be selected");
        }
    }
}
