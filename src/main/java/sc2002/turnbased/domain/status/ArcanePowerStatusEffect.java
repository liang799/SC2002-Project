package sc2002.turnbased.domain.status;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatStats;
import sc2002.turnbased.domain.StatType;

public class ArcanePowerStatusEffect implements StatusEffect {
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
    public String description() {
        return "ARCANE POWER +" + attackBonus;
    }

    @Override
    public List<StatusEffectOutcome> onApply(Combatant owner) {
        return List.of(StatusEffectChange.applied(kind(), attackBonus));
    }

    @Override
    public CombatStats modifyStats(CombatStats stats) {
        return stats.addFlat(StatType.ATTACK, attackBonus);
    }

    @Override
    public Optional<StatusEffect> mergeWith(StatusEffect other) {
        Objects.requireNonNull(other, "other");
        if (!(other instanceof ArcanePowerStatusEffect arcanePowerStatusEffect)) {
            return Optional.empty();
        }
        return Optional.of(new ArcanePowerStatusEffect(attackBonus + arcanePowerStatusEffect.attackBonus));
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
