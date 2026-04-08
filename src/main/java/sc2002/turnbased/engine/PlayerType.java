package sc2002.turnbased.engine;

import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.HitPoints;
import sc2002.turnbased.domain.PlayerCharacter;

public enum PlayerType {
    WARRIOR(
        "Warrior",
        "Shield Bash",
        HitPoints.full(260),
        CombatStats.builder()
            .attack(40)
            .defense(20)
            .speed(30)
            .build()
    ),
    WIZARD(
        "Wizard",
        "Arcane Blast",
        HitPoints.full(200),
        CombatStats.builder()
            .attack(50)
            .defense(10)
            .speed(20)
            .build()
    );

    private final String displayName;
    private final String specialSkillName;
    private final HitPoints baseHitPoints;
    private final CombatStats baseStats;

    PlayerType(String displayName, String specialSkillName, HitPoints baseHitPoints, CombatStats baseStats) {
        this.displayName = displayName;
        this.specialSkillName = specialSkillName;
        this.baseHitPoints = baseHitPoints;
        this.baseStats = baseStats;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpecialSkillName() {
        return specialSkillName;
    }

    public HitPoints getBaseHitPoints() {
        return baseHitPoints;
    }

    public CombatStats getBaseStats() {
        return baseStats;
    }

    public PlayerCharacter createPlayer(CombatantFactory combatantFactory) {
        return combatantFactory.createPlayer(this);
    }
}
