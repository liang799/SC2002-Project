package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.StatType;
import sc2002.turnbased.domain.status.event.ArcanePowerAppliedEvent;

public class ArcanePowerStatusEffect implements StatusEffect, StatModifierEffect, MergeableStatusEffect {
    private final int attackBonus;

    public ArcanePowerStatusEffect(int attackBonus) {
        if (attackBonus <= 0) {
            throw new IllegalArgumentException("attackBonus must be positive");
        }
        this.attackBonus = attackBonus;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.ARCANE_POWER;
    }

    @Override
    public String name() {
        return "ARCANE POWER +" + attackBonus;
    }

    @Override
    public void onRegistered(String ownerName, StatusEffectEventPublisher eventPublisher) {
        eventPublisher.publish(new ArcanePowerAppliedEvent(ownerName, attackBonus));
    }

    @Override
    public CombatStats modifyStats(CombatStats stats) {
        return stats.addFlat(StatType.ATTACK, attackBonus);
    }

    @Override
    public boolean canMergeWith(StatusEffect other) {
        return other instanceof ArcanePowerStatusEffect;
    }

    @Override
    public StatusEffect merge(StatusEffect other) {
        if (!(other instanceof ArcanePowerStatusEffect arcanePowerStatusEffect)) {
            throw new IllegalArgumentException("Cannot merge ArcanePowerStatusEffect with " + other.getClass().getSimpleName());
        }

        return new ArcanePowerStatusEffect(attackBonus + arcanePowerStatusEffect.attackBonus);
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
