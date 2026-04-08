package sc2002.turnbased.domain;

import sc2002.turnbased.actions.ShieldBashAction;

public class Warrior extends PlayerCharacter {
    private static final HitPoints WARRIOR_HIT_POINTS = HitPoints.full(260);
    private static final CombatStats WARRIOR_STATS = CombatStats.builder()
        .attack(40)
        .defense(20)
        .speed(30)
        .build();

    public Warrior() {
        super("Warrior", WARRIOR_HIT_POINTS, WARRIOR_STATS, new SpecialSkill(new ShieldBashAction(), 3));
    }
}
