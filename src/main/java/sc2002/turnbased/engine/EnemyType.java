package sc2002.turnbased.engine;

import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.HitPoints;

public enum EnemyType implements EnemyFactory {
    GOBLIN(
        "Goblin",
        "Goblins",
        3,
        "Goblins (HP:55 ATK:35 DEF:15 SPD:25)",
        HitPoints.full(55),
        CombatStats.builder()
            .attack(35)
            .defense(15)
            .speed(25)
            .build()
    ),
    WOLF(
        "Wolf",
        "Wolves",
        3,
        "Wolves  (HP:40 ATK:45 DEF:5  SPD:35)",
        HitPoints.full(40),
        CombatStats.builder()
            .attack(45)
            .defense(5)
            .speed(35)
            .build()
    );

    private final String displayName;
    private final String pluralDisplayName;
    private final int maxPerWave;
    private final String configurationPrompt;
    private final HitPoints baseHitPoints;
    private final CombatStats baseStats;

    EnemyType(
        String displayName,
        String pluralDisplayName,
        int maxPerWave,
        String configurationPrompt,
        HitPoints baseHitPoints,
        CombatStats baseStats
    ) {
        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;
        this.maxPerWave = maxPerWave;
        this.configurationPrompt = configurationPrompt;
        this.baseHitPoints = baseHitPoints;
        this.baseStats = baseStats;
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

    public HitPoints getBaseHitPoints() {
        return baseHitPoints;
    }

    public CombatStats getBaseStats() {
        return baseStats;
    }

    @Override
    public EnemyCombatant create(String name, CombatantFactory combatantFactory) {
        return combatantFactory.createEnemy(this, name);
    }
}
