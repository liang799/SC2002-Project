package sc2002.turnbased.domain;

import sc2002.turnbased.domain.status.StatusEffectRegistry;

public class Wizard extends PlayerCharacter {
    private static final HitPoints WIZARD_HIT_POINTS = HitPoints.full(200);
    private static final CombatStats WIZARD_STATS = CombatStats.builder()
        .attack(50)
        .defense(10)
        .speed(20)
        .build();

    public Wizard(StatusEffectRegistry statusEffectRegistry, SpecialSkill specialSkill) {
        super("Wizard", WIZARD_HIT_POINTS, WIZARD_STATS, statusEffectRegistry, specialSkill);
    }
}
