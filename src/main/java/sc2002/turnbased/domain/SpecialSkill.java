package sc2002.turnbased.domain;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;

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

    public BattleAction action() {
        return action;
    }

    public boolean isAvailable() {
        return cooldownRemaining == 0;
    }

    public int cooldownRemaining() {
        return cooldownRemaining;
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
