package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ArcaneBlastAction;

public class Wizard extends PlayerCharacter {
    private static final CombatStats WIZARD_STATS = CombatStats.of(
        HitPoints.full(200),
        new Stat(50),
        new Stat(10),
        new Stat(20)
    );

    public Wizard() {
        super("Wizard", WIZARD_STATS, new SpecialSkill(new ArcaneBlastAction(), 3));
    }
}
