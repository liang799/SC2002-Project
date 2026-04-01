package sc2002.turnbased.domain;

import sc2002.turnbased.actions.BattleAction;

public abstract class PlayerCharacter extends Combatant {
    protected PlayerCharacter(String name, CombatStats baseStats) {
        super(name, baseStats);
    }

    public abstract BattleAction createSpecialSkillAction(boolean startsCooldown);
}
