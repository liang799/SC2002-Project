package sc2002.turnbased.domain;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.actions.ActionExecutionContext;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.TargetingMode;
import sc2002.turnbased.report.BattleEvent;

public class SpecialSkill {
    private final BattleAction action;
    private final int cooldownTurns;
    private int cooldownRemaining;

    public SpecialSkill(BattleAction action, int cooldownTurns) {
        this.action = Objects.requireNonNull(action, "action");
        if (cooldownTurns < 0) {
            throw new IllegalArgumentException("cooldownTurns cannot be negative");
        }
        this.cooldownTurns = cooldownTurns;
        this.cooldownRemaining = 0;
    }

    public String actionName() {
        return action.getName();
    }

    public TargetingMode targetingMode(Combatant actor) {
        return action.targetingMode(Objects.requireNonNull(actor, "actor"));
    }

    public boolean isAvailable() {
        return cooldownRemaining == 0;
    }

    public int cooldownRemaining() {
        return cooldownRemaining;
    }

    public List<BattleEvent> use(ActionExecutionContext context, Combatant actor, Combatant target) {
        if (!isAvailable()) {
            throw new IllegalStateException("Special skill is on cooldown");
        }
        List<BattleEvent> events = useWithoutTriggeringCooldown(context, actor, target);
        triggerCooldown();
        return events;
    }

    public List<BattleEvent> useWithoutTriggeringCooldown(ActionExecutionContext context, Combatant actor, Combatant target) {
        return action.execute(
            Objects.requireNonNull(context, "context"),
            Objects.requireNonNull(actor, "actor"),
            target
        );
    }

    public void advanceCooldown() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    public void triggerCooldown() {
        cooldownRemaining = cooldownTurns;
    }
}
