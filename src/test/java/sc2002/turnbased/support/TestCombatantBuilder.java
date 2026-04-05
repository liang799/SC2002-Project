package sc2002.turnbased.support;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.HitPoints;
import sc2002.turnbased.domain.Stat;

public final class TestCombatantBuilder {
    private String name = "Test Combatant";
    private int currentHp = 100;
    private int maxHp = 100;
    private int attack = 40;
    private int defense = 20;
    private int speed = 30;

    private TestCombatantBuilder() {
    }

    public static TestCombatantBuilder aCombatant() {
        return new TestCombatantBuilder();
    }

    public TestCombatantBuilder named(String name) {
        this.name = name;
        return this;
    }

    public TestCombatantBuilder withCurrentHp(int currentHp) {
        this.currentHp = currentHp;
        return this;
    }

    public TestCombatantBuilder withMaxHp(int maxHp) {
        this.maxHp = maxHp;
        return this;
    }

    public TestCombatantBuilder withAttack(int attack) {
        this.attack = attack;
        return this;
    }

    public TestCombatantBuilder withDefense(int defense) {
        this.defense = defense;
        return this;
    }

    public TestCombatantBuilder withSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public Combatant build() {
        return new TestCombatant(
            name,
            CombatStats.of(
                new HitPoints(currentHp, maxHp),
                new Stat(attack),
                new Stat(defense),
                new Stat(speed)
            )
        );
    }

    private static final class TestCombatant extends Combatant {
        private TestCombatant(String name, CombatStats baseStats) {
            super(name, baseStats);
        }
    }
}
