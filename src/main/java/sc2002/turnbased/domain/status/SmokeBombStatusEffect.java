package sc2002.turnbased.domain.status;

import java.util.List;

import sc2002.turnbased.domain.Combatant;

public class SmokeBombStatusEffect implements StatusEffect {
    private int chargesRemaining;

    public SmokeBombStatusEffect(int protectedEnemyAttacksRemaining) {
        if (protectedEnemyAttacksRemaining < 0) {
            throw new IllegalArgumentException("protectedEnemyAttacksRemaining must not be negative");
        }
        this.chargesRemaining = protectedEnemyAttacksRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.SMOKE_BOMB;
    }

    @Override
    public String description() {
        return "SMOKE BOMB";
    }

    @Override
    public List<StatusEffectOutcome> onApply(Combatant owner) {
        return List.of(StatusEffectChange.applied(kind(), 0, chargesRemaining));
    }

    @Override
    public DamageAdjustment modifyIncomingDamage(
        Combatant owner,
        Combatant attacker,
        int damage
    ) {
        if (chargesRemaining == 0 || attacker == owner) {
            return DamageAdjustment.unchanged(damage);
        }

        chargesRemaining--;
        return new DamageAdjustment(0, List.of(new DamageModifier(kind(), DamageModifierType.BLOCKED)));
    }

    @Override
    public List<StatusEffectOutcome> onExpire(Combatant owner) {
        return List.of(StatusEffectChange.expired(kind()));
    }

    @Override
    public boolean isExpired() {
        return chargesRemaining == 0;
    }
}
