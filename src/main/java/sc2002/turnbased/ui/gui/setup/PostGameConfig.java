package sc2002.turnbased.ui.gui.setup;

import java.util.Objects;

import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.GameConfiguration;

public sealed interface PostGameConfig permits PostGameConfig.Preset, PostGameConfig.Custom {
    static PostGameConfig preset(GameConfiguration configuration) {
        return new Preset(configuration);
    }

    static PostGameConfig custom(CustomGameConfiguration configuration) {
        return new Custom(configuration);
    }

    record Preset(GameConfiguration configuration) implements PostGameConfig {
        public Preset {
            Objects.requireNonNull(configuration, "configuration");
        }
    }

    record Custom(CustomGameConfiguration configuration) implements PostGameConfig {
        public Custom {
            Objects.requireNonNull(configuration, "configuration");
        }
    }
}
