package sc2002.turnbased.ui.gui.setup;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.engine.BattleSetup;
import sc2002.turnbased.engine.BattleSetupFactory;
import sc2002.turnbased.engine.CustomGameConfiguration;
import sc2002.turnbased.engine.GameConfiguration;
import sc2002.turnbased.engine.WaveSpec;

public final class BattleLaunchRequest {
    private final Function<BattleSetupFactory, BattleSetup> setupCreator;
    private final Object replayConfiguration;
    private final String intro;

    private BattleLaunchRequest(
        Function<BattleSetupFactory, BattleSetup> setupCreator,
        Object replayConfiguration,
        String intro
    ) {
        this.setupCreator = Objects.requireNonNull(setupCreator, "setupCreator");
        this.replayConfiguration = Objects.requireNonNull(replayConfiguration, "replayConfiguration");
        this.intro = Objects.requireNonNull(intro, "intro");
    }

    public static BattleLaunchRequest preset(GameConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        String intro = "=== Selected: " + configuration.playerType().getDisplayName()
            + " | " + configuration.difficultyLevel().getDisplayName()
            + " | Items: " + describeItems(configuration.selectedItems())
            + " ===";
        return new BattleLaunchRequest(factory -> factory.create(configuration), configuration, intro);
    }

    public static BattleLaunchRequest custom(CustomGameConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        return new BattleLaunchRequest(
            factory -> factory.createCustom(configuration),
            configuration,
            describeCustomConfiguration(configuration)
        );
    }

    public static BattleLaunchRequest replay(Object configuration) {
        if (configuration instanceof GameConfiguration gameConfiguration) {
            return new BattleLaunchRequest(
                factory -> factory.create(gameConfiguration),
                gameConfiguration,
                "=== Replaying same settings ==="
            );
        }
        if (configuration instanceof CustomGameConfiguration customConfiguration) {
            return custom(customConfiguration);
        }
        throw new IllegalArgumentException("Unsupported replay configuration: " + configuration);
    }

    public BattleSetup createSetup(BattleSetupFactory setupFactory) {
        return setupCreator.apply(Objects.requireNonNull(setupFactory, "setupFactory"));
    }

    public Object replayConfiguration() {
        return replayConfiguration;
    }

    public String intro() {
        return intro;
    }

    private static String describeCustomConfiguration(CustomGameConfiguration config) {
        StringBuilder description = new StringBuilder();
        description.append("=== Custom Mode Configuration ===\n");
        description.append("Player Class: ").append(config.playerType().getDisplayName()).append("\n");
        description.append("Items: ").append(describeItems(config.selectedItems())).append("\n");
        for (int i = 0; i < config.waves().size(); i++) {
            WaveSpec wave = config.waves().get(i);
            description.append("Wave ").append(i + 1).append(": ")
                .append(wave.describe()).append(" - ")
                .append(wave.totalEnemies()).append(" ")
                .append(wave.totalEnemies() == 1 ? "enemy" : "enemies")
                .append(" total\n");
        }
        return description.toString();
    }

    private static String describeItems(List<ItemType> selectedItems) {
        if (selectedItems.isEmpty()) {
            return "none";
        }
        return selectedItems.stream()
            .map(ItemType::getDisplayName)
            .collect(Collectors.joining(", "));
    }
}
