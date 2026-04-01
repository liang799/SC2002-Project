package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class Combatant {
    private final String name;
    private final CombatStats baseStats;
    private int attack;
    private int currentHp;
    private int specialSkillCooldown;
    private final List<StatusEffect> statusEffects = new ArrayList<>();

    protected Combatant(String name, CombatStats baseStats) {
        this.name = Objects.requireNonNull(name, "name");
        this.baseStats = Objects.requireNonNull(baseStats, "baseStats");
        this.attack = baseStats.attack();
        this.currentHp = baseStats.maxHp();
        this.specialSkillCooldown = 0;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return baseStats.maxHp();
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getBaseAttack() {
        return baseStats.attack();
    }

    public int getDefense() {
        return baseStats.defense() + activeDefenseModifier();
    }

    public int getSpeed() {
        return baseStats.speed();
    }

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }

    public boolean isAlive() {
        return currentHp > 0;
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
        currentHp = Math.max(0, currentHp - damage);
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
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    public void adjustAttack(int amount) {
        attack += amount;
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
