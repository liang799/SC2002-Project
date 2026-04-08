package sc2002.turnbased.domain;

import java.util.Objects;

import sc2002.turnbased.actions.BattleAction;

public class DefaultSpecialSkillFactory implements SpecialSkillFactory {
    private final BattleAction action;
    private final int cooldownTurns;

    public DefaultSpecialSkillFactory(BattleAction action, int cooldownTurns) {
        this.action = Objects.requireNonNull(action, "action");
        if (cooldownTurns < 0) {
            throw new IllegalArgumentException("cooldownTurns cannot be negative");
        }
        this.cooldownTurns = cooldownTurns;
    }

    @Override
    public SpecialSkill create() {
        return new SpecialSkill(action, cooldownTurns);
    }
}
