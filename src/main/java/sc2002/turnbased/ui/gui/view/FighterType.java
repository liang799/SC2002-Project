package sc2002.turnbased.ui.gui.view;

import java.util.Locale;
import java.util.Objects;

public enum FighterType {
    PLAYER,
    GOBLIN,
    WOLF,
    UNKNOWN;

    public static FighterType fromName(String name, boolean player) {
        Objects.requireNonNull(name, "name");
        if (player) {
            return PLAYER;
        }
        String normalized = name.toLowerCase(Locale.ROOT);
        if (normalized.contains("wolf")) {
            return WOLF;
        }
        if (normalized.contains("goblin")) {
            return GOBLIN;
        }
        return UNKNOWN;
    }
}
