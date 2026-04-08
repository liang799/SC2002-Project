package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ShieldBashAction;

public class Warrior extends PlayerCharacter {
    private static final HitPoints WARRIOR_HIT_POINTS = HitPoints.full(260);
    private static final CombatStats WARRIOR_STATS = CombatStats.of(
        new Stat(40),
        new Stat(20),
        new Stat(30)
    );

    public Warrior() {
        super("Warrior", WARRIOR_HIT_POINTS, WARRIOR_STATS, new SpecialSkill(new ShieldBashAction(), 3));
    }
}
