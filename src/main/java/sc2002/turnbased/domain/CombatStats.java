package sc2002.turnbased.domain;

public record CombatStats(HitPoints hitPoints, Stat attack, Stat defense, Stat speed) {
    public int valueOf(StatType statType) {
        return switch (statType) {
            case ATTACK -> attack.value();
            case DEFENSE -> defense.value();
            case SPEED -> speed.value();
        };
    }

    public CombatStats withHitPoints(HitPoints updatedHitPoints) {
        return new CombatStats(updatedHitPoints, attack, defense, speed);
    }

    public CombatStats withAttack(Stat updatedAttack) {
        return new CombatStats(hitPoints, updatedAttack, defense, speed);
    }
}
