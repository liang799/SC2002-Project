package sc2002.turnbased.domain;

import java.util.List;

public class SmokeBombStatusEffect implements StatusEffect {
    private int protectedEnemyAttacksRemaining;

    public SmokeBombStatusEffect(int protectedEnemyAttacksRemaining) {
        this.protectedEnemyAttacksRemaining = protectedEnemyAttacksRemaining;
    }

    @Override
    public String getName() {
        return "SMOKE BOMB";
    }

    @Override
    public TurnEffectResolution onTurnOpportunity() {
        return new TurnEffectResolution(false, null, List.of());
    }

    @Override
    public int adjustIncomingDamage(Combatant owner, Combatant attacker, int damage, List<String> notes) {
        if (protectedEnemyAttacksRemaining <= 0 || attacker == owner) {
            return damage;
        }

        protectedEnemyAttacksRemaining--;
        notes.add("Smoke Bomb active");
        if (protectedEnemyAttacksRemaining == 0) {
            notes.add("Smoke Bomb effect expires");
        }
        return 0;
    }

    @Override
    public boolean isExpired() {
        return protectedEnemyAttacksRemaining == 0;
    }
}
