package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Combatant {
    private final String name;
    private final int maxHp;
    private final int baseAttack;
    private final int defense;
    private final int speed;
    private int attack;
    private int currentHp;
    private int specialSkillCooldown;
    private final List<StatusEffect> statusEffects = new ArrayList<>();

    protected Combatant(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.baseAttack = attack;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.currentHp = maxHp;
        this.specialSkillCooldown = 0;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
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

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
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
}
