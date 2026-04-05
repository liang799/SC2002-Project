package sc2002.turnbased.domain;

import java.util.Objects;

public abstract class PlayerCharacter extends Combatant {
    private final SpecialSkill specialSkill;

    protected PlayerCharacter(String name, CombatStats baseStats, SpecialSkill specialSkill) {
        super(name, baseStats);
        this.specialSkill = Objects.requireNonNull(specialSkill, "specialSkill");
    }

    public SpecialSkill getSpecialSkill() {
        return specialSkill;
    }

    public int getSpecialSkillCooldown() {
        return specialSkill.cooldownRemaining();
    }

    public boolean canUseSpecialSkill() {
        return specialSkill.isAvailable();
    }
}
