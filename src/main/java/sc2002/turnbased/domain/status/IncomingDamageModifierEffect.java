package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.Combatant;

public interface IncomingDamageModifierEffect {
    DamageAdjustment adjustIncomingDamage(
        Combatant owner,
        Combatant attacker,
        int damage,
        StatusEffectEventPublisher eventPublisher
    );
}
