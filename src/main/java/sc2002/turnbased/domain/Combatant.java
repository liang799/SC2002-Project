package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import sc2002.turnbased.domain.status.DamageAdjustment;
import sc2002.turnbased.domain.status.StatusEffect;
import sc2002.turnbased.domain.status.StatusEffectRegistry;

public abstract class Combatant {
    private final CombatantId combatantId;
    private final String name;
    private final CombatStats baseStats;
    private HitPoints hitPoints;
    private final StatusEffectRegistry statusEffectRegistry;

    protected Combatant(String name, HitPoints baseHitPoints, CombatStats baseStats, StatusEffectRegistry statusEffectRegistry) {
        this.name = Objects.requireNonNull(name, "name");
        this.combatantId = CombatantId.of(name);
        this.hitPoints = Objects.requireNonNull(baseHitPoints, "baseHitPoints");
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
        this.statusEffectRegistry = Objects.requireNonNull(statusEffectRegistry, "statusEffectRegistry");
    }

    public CombatantId combatantId() {
        return combatantId;
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

    public AttackResolution attack(Combatant target) {
        return attack(target, getAttack());
    }

    public AttackResolution attack(Combatant target, int attackPower) {
        Combatant attackTarget = Objects.requireNonNull(target, "target");
        if (attackPower < 0) {
            throw new IllegalArgumentException("attackPower must not be negative");
        }

        int attackUsed = attackPower;
        int targetDefense = attackTarget.getDefense();
        int baseDamage = Math.max(0, attackUsed - targetDefense);
        return attackTarget.resolveAttackFrom(this, attackUsed, targetDefense, baseDamage);
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

    public List<String> addStatusEffect(StatusEffect statusEffect) {
        Objects.requireNonNull(statusEffect, "statusEffect");
        return statusEffectRegistry.add(this, statusEffect);
    }

    public Optional<String> getTurnBlockReason() {
        return statusEffectRegistry.getTurnBlockReason(this);
    }

    public List<String> consumeStatusEffectNotes() {
        return statusEffectRegistry.consumeNotes();
    }

    public List<String> completeRound() {
        return statusEffectRegistry.completeRound(this);
    }

    public List<String> getActiveStatuses() {
        return statusEffectRegistry.activeStatuses(this);
    }

    private AttackResolution resolveAttackFrom(Combatant attacker, int attackUsed, int targetDefense, int baseDamage) {
        int hpBefore = getCurrentHp();
        DamageAdjustment damageAdjustment = statusEffectRegistry.adjustIncomingDamage(this, attacker, baseDamage);
        int damage = damageAdjustment.damage();
        receiveDamage(damage);

        return new AttackResolution(
            attackUsed,
            targetDefense,
            hpBefore,
            getCurrentHp(),
            damage,
            !isAlive(),
            damageAdjustment.notes()
        );
    }

    private CombatStats effectiveStats() {
        return statusEffectRegistry.apply(this, baseStats);
    }
}
