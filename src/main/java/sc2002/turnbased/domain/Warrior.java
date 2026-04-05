package sc2002.turnbased.domain;

import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.ShieldBashAction;

public class Warrior extends PlayerCharacter {
    private static final CombatStats WARRIOR_STATS = new CombatStats(
        HitPoints.full(260),
        new Stat(40),
        new Stat(20),
        new Stat(30)
    );

    public Warrior() {
        super("Warrior", WARRIOR_STATS);
    }

    @Override
    public BattleAction createSpecialSkillAction(boolean startsCooldown) {
        return new ShieldBashAction(startsCooldown);
    }
}
