package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BattleAction;

public class Wizard extends PlayerCharacter {
    private static final CombatStats WIZARD_STATS = new CombatStats(200, 50, 10, 20);

    public Wizard() {
        super("Wizard", WIZARD_STATS);
    }

    @Override
    public BattleAction createSpecialSkillAction(boolean startsCooldown) {
        return new ArcaneBlastAction(startsCooldown);
    }
}
