package sc2002.turnbased.engine;

import java.util.function.Function;

import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.Goblin;
import sc2002.turnbased.domain.Wolf;

public enum EnemyType implements EnemyFactory {
    GOBLIN("Goblin", "Goblins", 3, "Goblins (HP:55 ATK:35 DEF:15 SPD:25)", Goblin::new),
    WOLF("Wolf", "Wolves", 3, "Wolves  (HP:40 ATK:45 DEF:5  SPD:35)", Wolf::new);

    private final String displayName;
    private final String pluralDisplayName;
    private final int maxPerWave;
    private final String configurationPrompt;
    private final Function<String, EnemyCombatant> enemyFactory;

    EnemyType(
        String displayName,
        String pluralDisplayName,
        int maxPerWave,
        String configurationPrompt,
        Function<String, EnemyCombatant> enemyFactory
    ) {
        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;
        this.maxPerWave = maxPerWave;
        this.configurationPrompt = configurationPrompt;
        this.enemyFactory = enemyFactory;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getPluralDisplayName() {
        return pluralDisplayName;
    }

    @Override
    public int getMaxPerWave() {
        return maxPerWave;
    }

    public String getConfigurationPrompt() {
        return configurationPrompt;
    }

    @Override
    public EnemyCombatant create(String name) {
        return enemyFactory.apply(name);
    }
}
