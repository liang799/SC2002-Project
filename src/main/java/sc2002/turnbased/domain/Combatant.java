package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

public abstract class Combatant {
    private final String name;
    private final CombatStats baseStats;
    private HitPoints hitPoints;
    private CombatStatsModifier statModifier = CombatStatsModifier.identity();
    private final StatusEffectRegistry statusEffectRegistry = new StatusEffectRegistry();

    protected Combatant(String name, HitPoints baseHitPoints, CombatStats baseStats) {
        this.name = Objects.requireNonNull(name, "name");
        this.hitPoints = Objects.requireNonNull(baseHitPoints, "baseHitPoints");
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return hitPoints.max();
    }

    public int getCurrentHp() {
        return hitPoints.current();
    }

    public HitPoints getHitPoints() {
        return hitPoints;
    }

    public int getAttack() {
        return getStat(StatType.ATTACK);
    }

    public int getBaseAttack() {
        return baseStats.attack().value();
    }

    public int getDefense() {
        return getStat(StatType.DEFENSE);
    }

    public int getSpeed() {
        return getStat(StatType.SPEED);
    }

    public boolean isAlive() {
        return !getHitPoints().isDead();
    }

    public int getStat(StatType statType) {
        return effectiveStats().valueOf(statType);
    }

    public void receiveDamage(int damage) {
        hitPoints = hitPoints.takeDamage(damage);
    }

    public void heal(int amount) {
        hitPoints = hitPoints.heal(amount);
    }

    public void modifyStats(CombatStatsModifier modifier) {
        statModifier = statModifier.andThen(Objects.requireNonNull(modifier, "modifier"));
    }

    public void addStatusEffect(StatusEffect statusEffect) {
        statusEffectRegistry.add(statusEffect);
    }

    public StatusEffectRegistry statusEffects() {
        return statusEffectRegistry;
    }

    public List<String> getActiveStatusNames() {
        return statusEffectRegistry.activeStatusNames(isAlive());
    }

    private CombatStats effectiveStats() {
        CombatStats effectiveStats = baseStats.apply(statModifier);
        return statusEffectRegistry.apply(effectiveStats);
    }
}
