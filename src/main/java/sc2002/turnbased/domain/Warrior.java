package sc2002.turnbased.domain;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.ShieldBashAction;

public class Warrior extends PlayerCharacter {
    private static final CombatStats WARRIOR_STATS = new CombatStats(260, 40, 20, 30);

    public Warrior() {
        super("Warrior", WARRIOR_STATS);
    }

    @Override
    public BattleAction createSpecialSkillAction(boolean startsCooldown) {
        return new ShieldBashAction(startsCooldown);
    }
}
