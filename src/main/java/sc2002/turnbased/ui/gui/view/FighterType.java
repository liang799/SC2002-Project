package sc2002.turnbased.ui.gui.view;

import java.util.Locale;
import java.util.Objects;

public enum FighterType {
    WARRIOR,
    WIZARD,
    GOBLIN,
    WOLF,
    UNKNOWN;

    public static FighterType fromName(String name, boolean player) {
        Objects.requireNonNull(name, "name");
        String normalized = name.toLowerCase(Locale.ROOT);
        if (player) {
            return normalized.contains("wizard") ? WIZARD : WARRIOR;
        }
        if (normalized.contains("wolf")) {
            return WOLF;
        }
        if (normalized.contains("goblin")) {
            return GOBLIN;
        }
        return UNKNOWN;
    }
}
