package sc2002.turnbased.domain;

public interface IncomingDamageModifierEffect {
    DamageAdjustment adjustIncomingDamage(Combatant owner, Combatant attacker, int damage);
}
