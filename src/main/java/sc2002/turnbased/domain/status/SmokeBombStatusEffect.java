package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.event.SmokeBombActivatedEvent;
import sc2002.turnbased.domain.status.event.SmokeBombAppliedEvent;

public class SmokeBombStatusEffect implements StatusEffect, IncomingDamageModifierEffect {
    private int protectedEnemyAttacksRemaining;

    public SmokeBombStatusEffect(int protectedEnemyAttacksRemaining) {
        this.protectedEnemyAttacksRemaining = protectedEnemyAttacksRemaining;
    }

    @Override
    public StatusEffectKind kind() {
        return StatusEffectKind.SMOKE_BOMB;
    }

    @Override
    public String name() {
        return "SMOKE BOMB";
    }

    @Override
    public void onRegistered(String ownerName, StatusEffectEventPublisher eventPublisher) {
        eventPublisher.publish(new SmokeBombAppliedEvent(ownerName, protectedEnemyAttacksRemaining));
    }

    @Override
    public DamageAdjustment adjustIncomingDamage(
        Combatant owner,
        Combatant attacker,
        int damage,
        StatusEffectEventPublisher eventPublisher
    ) {
        if (protectedEnemyAttacksRemaining <= 0 || attacker == owner) {
            return DamageAdjustment.unchanged(damage);
        }

        protectedEnemyAttacksRemaining--;
        eventPublisher.publish(new SmokeBombActivatedEvent(
            owner.getName(),
            attacker.getName(),
            protectedEnemyAttacksRemaining
        ));
        return new DamageAdjustment(0);
    }

    @Override
    public boolean isExpired() {
        return protectedEnemyAttacksRemaining == 0;
    }
}
