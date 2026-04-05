package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

public abstract class Combatant {
    private final String name;
    private final CombatStats baseStats;
    private HitPoints hitPoints;
    private final CombatStatModifierRegistry statModifiers = new CombatStatModifierRegistry();
    private final StatusEffectRegistry statusEffectRegistry = new StatusEffectRegistry();

    protected Combatant(String name, CombatStats baseStats) {
        this.name = Objects.requireNonNull(name, "name");
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
        this.hitPoints = baseStats.hitPoints();
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
        return baseStats.valueOf(StatType.ATTACK);
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
        int resolved = baseStats.valueOf(statType)
            + statModifiers.modifierFor(statType)
            + statusEffectRegistry.modifierFor(statType);
        return Math.max(0, resolved);
    }

    public void receiveDamage(int damage) {
        hitPoints = hitPoints.takeDamage(damage);
    }

    public void heal(int amount) {
        hitPoints = hitPoints.heal(amount);
    }

    public void adjustStat(StatType statType, int amount) {
        statModifiers.adjust(statType, amount);
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
}
