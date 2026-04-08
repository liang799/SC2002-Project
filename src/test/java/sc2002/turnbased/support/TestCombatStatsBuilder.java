package sc2002.turnbased.support;

import sc2002.turnbased.domain.CombatStats;

public final class TestCombatStatsBuilder {
    private int attack = 40;
    private int defense = 15;
    private int speed = 20;

    private TestCombatStatsBuilder() {
    }

    public static TestCombatStatsBuilder combatStats() {
        return new TestCombatStatsBuilder();
    }

    public TestCombatStatsBuilder withAttack(int attack) {
        this.attack = attack;
        return this;
    }

    public TestCombatStatsBuilder withDefense(int defense) {
        this.defense = defense;
        return this;
    }

    public TestCombatStatsBuilder withSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public CombatStats build() {
        return CombatStats.builder()
            .attack(attack)
            .defense(defense)
            .speed(speed)
            .build();
    }
}
