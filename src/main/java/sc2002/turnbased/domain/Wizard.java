package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ArcaneBlastAction;

public class Wizard extends PlayerCharacter {
    private static final HitPoints WIZARD_HIT_POINTS = HitPoints.full(200);
    private static final CombatStats WIZARD_STATS = CombatStats.of(
        new Stat(50),
        new Stat(10),
        new Stat(20)
    );

    public Wizard() {
        super("Wizard", WIZARD_HIT_POINTS, WIZARD_STATS, new SpecialSkill(new ArcaneBlastAction(), 3));
    }
}
