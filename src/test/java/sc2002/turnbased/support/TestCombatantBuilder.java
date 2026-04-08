package sc2002.turnbased.support;

import java.util.Objects;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.HitPoints;
import sc2002.turnbased.domain.status.StatusEffectRegistry;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;

public final class TestCombatantBuilder {
    private final StatusEffectRegistryFactory statusEffectRegistryFactory;
    private String name = "Test Combatant";
    private int currentHp = 100;
    private int maxHp = 100;
    private int attack = 40;
    private int defense = 20;
    private int speed = 30;

    private TestCombatantBuilder(StatusEffectRegistryFactory statusEffectRegistryFactory) {
        this.statusEffectRegistryFactory = Objects.requireNonNull(statusEffectRegistryFactory, "statusEffectRegistryFactory");
    }

    public static TestCombatantBuilder aCombatant() {
        return aCombatant(TestDependencies::registry);
    }

    public static TestCombatantBuilder aCombatant(StatusEffectRegistryFactory statusEffectRegistryFactory) {
        return new TestCombatantBuilder(statusEffectRegistryFactory);
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

    public TestCombatantBuilder withHp(int hp) {
        currentHp = hp;
        maxHp = hp;
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
            new HitPoints(currentHp, maxHp),
            CombatStats.builder()
                .attack(attack)
                .defense(defense)
                .speed(speed)
                .build(),
            statusEffectRegistryFactory.create()
        );
    }

    private static final class TestCombatant extends Combatant {
        private TestCombatant(String name, HitPoints hitPoints, CombatStats baseStats, StatusEffectRegistry statusEffectRegistry) {
            super(name, hitPoints, baseStats, statusEffectRegistry);
        }
    }
}
