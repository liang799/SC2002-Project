package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.TargetingMode;
import sc2002.turnbased.domain.status.StatusEffectRegistry;
import sc2002.turnbased.report.BattleEvent;

public class PlayerCharacter extends Combatant {
    private final SpecialSkill specialSkill;

    public PlayerCharacter(
        String name,
        HitPoints baseHitPoints,
        CombatStats baseStats,
        StatusEffectRegistry statusEffectRegistry,
        SpecialSkill specialSkill
    ) {
        super(name, baseHitPoints, baseStats, statusEffectRegistry);
        this.specialSkill = Objects.requireNonNull(specialSkill, "specialSkill");
    }

    public TargetingMode specialSkillTargetingMode() {
        return specialSkill.targetingMode(this);
    }

    public int getSpecialSkillCooldown() {
        return specialSkill.cooldownRemaining();
    }

    public boolean canUseSpecialSkill() {
        return specialSkill.isAvailable();
    }

    public List<BattleEvent> useSpecialSkill(ActionExecutionContext context, Combatant target) {
        return specialSkill.use(context, this, target);
    }

    public List<BattleEvent> useSpecialSkillWithoutCooldown(ActionExecutionContext context, Combatant target) {
        return specialSkill.useWithoutTriggeringCooldown(context, this, target);
    }

    public String getSpecialSkillName() {
        return specialSkill.actionName();
    }

    public void advanceRoundState() {
        specialSkill.advanceCooldown();
    }
}
