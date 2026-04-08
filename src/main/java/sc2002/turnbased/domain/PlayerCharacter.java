package sc2002.turnbased.domain;

import java.util.Objects;

import sc2002.turnbased.domain.status.StatusEffectRegistry;

public abstract class PlayerCharacter extends Combatant {
    private final SpecialSkill specialSkill;

    protected PlayerCharacter(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        SpecialSkill specialSkill
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
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
