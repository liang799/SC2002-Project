package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.ItemType;


public record CustomGameConfiguration(
    PlayerType playerType,
    List<ItemType> selectedItems,
    List<WaveSpec> waves
) {
    public CustomGameConfiguration {
        Objects.requireNonNull(playerType, "playerType");
        Objects.requireNonNull(selectedItems, "selectedItems");
        Objects.requireNonNull(waves, "waves");
        selectedItems = List.copyOf(new ArrayList<>(selectedItems));
        waves = List.copyOf(new ArrayList<>(waves));
        if (selectedItems.size() != 2) {
            throw new IllegalArgumentException("Exactly 2 starting items must be selected");
        }
        if (waves.isEmpty() || waves.size() > 2) {
            throw new IllegalArgumentException("Custom mode requires 1 or 2 waves, got: " + waves.size());
        }
        for (WaveSpec wave : waves) {
            Objects.requireNonNull(wave, "wave entry cannot be null");
        }
    }
}