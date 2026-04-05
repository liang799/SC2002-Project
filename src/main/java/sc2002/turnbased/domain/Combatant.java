package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class Combatant {
    private final String name;
    private final CombatStats baseStats;
    private CombatStats currentStats;
    private int specialSkillCooldown;
    private final List<StatusEffect> statusEffects = new ArrayList<>();

    protected Combatant(String name, CombatStats baseStats) {
        this.name = Objects.requireNonNull(name, "name");
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
        this.currentStats = baseStats;
        this.specialSkillCooldown = 0;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return currentStats.hitPoints().max();
    }

    public int getCurrentHp() {
        return currentStats.hitPoints().current();
    }

    public HitPoints getHitPoints() {
        return currentStats.hitPoints();
    }

    public int getAttack() {
        return currentStats.attack().value();
    }

    public int getBaseAttack() {
        return baseStats.attack().value();
    }

    public int getDefense() {
        return currentStats.defense().value() + activeDefenseModifier();
    }

    public int getSpeed() {
        return currentStats.speed().value();
    }

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }

    public boolean isAlive() {
        return !getHitPoints().isDead();
    }

    public void beginTurn() {
        if (specialSkillCooldown > 0) {
            specialSkillCooldown--;
        }
    }

    public void setSpecialSkillCooldown(int specialSkillCooldown) {
        this.specialSkillCooldown = specialSkillCooldown;
    }

    public void receiveDamage(int damage) {
        currentStats = currentStats.withHitPoints(currentStats.hitPoints().takeDamage(damage));
    }

    public int adjustIncomingDamage(Combatant attacker, int damage, List<String> notes) {
        Iterator<StatusEffect> iterator = statusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            damage = statusEffect.adjustIncomingDamage(this, attacker, damage, notes);
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }
        return damage;
    }

    public void heal(int amount) {
        currentStats = currentStats.withHitPoints(currentStats.hitPoints().heal(amount));
    }

    public void adjustAttack(int amount) {
        currentStats = currentStats.withAttack(currentStats.attack().adjustBy(amount));
    }

    public void addStatusEffect(StatusEffect statusEffect) {
        statusEffects.add(statusEffect);
    }

    public List<String> getActiveStatusNames() {
        if (!isAlive()) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        for (StatusEffect statusEffect : statusEffects) {
            if (!statusEffect.isExpired()) {
                names.add(statusEffect.getName());
            }
        }
        return names;
    }

    public TurnWindow openTurnWindow() {
        boolean blocked = false;
        String blockerLabel = null;
        List<String> notes = new ArrayList<>();

        Iterator<StatusEffect> iterator = statusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            TurnEffectResolution resolution = statusEffect.onTurnOpportunity();
            if (resolution.blocksAction() && blockerLabel == null) {
                blocked = true;
                blockerLabel = resolution.blockerLabel();
            }
            notes.addAll(resolution.notes());
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }

        return new TurnWindow(blocked, blockerLabel, notes);
    }

    public void completeRound() {
        Iterator<StatusEffect> iterator = statusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect statusEffect = iterator.next();
            statusEffect.onRoundCompleted();
            if (statusEffect.isExpired()) {
                iterator.remove();
            }
        }
    }

    private int activeDefenseModifier() {
        int defenseModifier = 0;
        for (StatusEffect statusEffect : statusEffects) {
            if (!statusEffect.isExpired()) {
                defenseModifier += statusEffect.getDefenseModifier();
            }
        }
        return defenseModifier;
    }
}
