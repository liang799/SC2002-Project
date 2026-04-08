package sc2002.turnbased.domain.status;

import java.util.List;

import sc2002.turnbased.domain.Combatant;

public class SmokeBombStatusEffect implements StatusEffect, IncomingDamageModifierEffect {
    private int protectedEnemyAttacksRemaining;

    public SmokeBombStatusEffect(int protectedEnemyAttacksRemaining) {
        this.protectedEnemyAttacksRemaining = protectedEnemyAttacksRemaining;
    }

    @Override
    public String name() {
        return "SMOKE BOMB";
    }

    @Override
    public DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        if (protectedEnemyAttacksRemaining <= 0 || attacker == owner) {
            return DamageAdjustment.unchanged(damage);
        }

        protectedEnemyAttacksRemaining--;
        List<String> notes = new java.util.ArrayList<>();
        notes.add("Smoke Bomb active");
        if (protectedEnemyAttacksRemaining == 0) {
            notes.add("Smoke Bomb effect expires");
        }
        return new DamageAdjustment(0, notes);
    }

    @Override
    public boolean isExpired() {
        return protectedEnemyAttacksRemaining == 0;
    }
}
