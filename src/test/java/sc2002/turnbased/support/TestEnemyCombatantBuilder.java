package sc2002.turnbased.support;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.HitPoints;
import sc2002.turnbased.domain.status.StatusEffectRegistryFactory;

public final class TestEnemyCombatantBuilder {
    private final BattleAction attackAction;
    private final StatusEffectRegistryFactory statusEffectRegistryFactory;
    private String name = "Goblin";
    private int currentHp = 100;
    private int maxHp = 100;
    private int attack = 40;
    private int defense = 20;
    private int speed = 30;

    private TestEnemyCombatantBuilder(BattleAction attackAction, StatusEffectRegistryFactory statusEffectRegistryFactory) {
        this.attackAction = Objects.requireNonNull(attackAction, "attackAction");
        this.statusEffectRegistryFactory = Objects.requireNonNull(statusEffectRegistryFactory, "statusEffectRegistryFactory");
    }

    public static TestEnemyCombatantBuilder anEnemyCombatant(BattleAction attackAction) {
        return anEnemyCombatant(attackAction, TestDependencies::registry);
    }

    public static TestEnemyCombatantBuilder anEnemyCombatant(
        BattleAction attackAction,
        StatusEffectRegistryFactory statusEffectRegistryFactory
    ) {
        return new TestEnemyCombatantBuilder(attackAction, statusEffectRegistryFactory);
    }

    public TestEnemyCombatantBuilder named(String name) {
        this.name = name;
        return this;
    }

    public TestEnemyCombatantBuilder withCurrentHp(int currentHp) {
        this.currentHp = currentHp;
        return this;
    }

    public TestEnemyCombatantBuilder withMaxHp(int maxHp) {
        this.maxHp = maxHp;
        return this;
    }

    public TestEnemyCombatantBuilder withHp(int hp) {
        currentHp = hp;
        maxHp = hp;
        return this;
    }

    public TestEnemyCombatantBuilder withAttack(int attack) {
        this.attack = attack;
        return this;
    }

    public TestEnemyCombatantBuilder withDefense(int defense) {
        this.defense = defense;
        return this;
    }

    public TestEnemyCombatantBuilder withSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public EnemyCombatant build() {
        return new EnemyCombatant(
            name,
            new HitPoints(currentHp, maxHp),
            CombatStats.builder()
                .attack(attack)
                .defense(defense)
                .speed(speed)
                .build(),
            statusEffectRegistryFactory.create(),
            attackAction
        );
    }
}
