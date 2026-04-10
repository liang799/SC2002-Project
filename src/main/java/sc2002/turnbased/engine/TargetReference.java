package sc2002.turnbased.engine;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;

public record TargetReference(TargetType type, CombatantId combatantId) {
    public TargetReference {
        Objects.requireNonNull(type, "type");
        if (type == TargetType.ENEMY && combatantId == null) {
            throw new IllegalArgumentException("Enemy targets must have a combatant id");
        }
        if (type == TargetType.NONE && combatantId != null) {
            throw new IllegalArgumentException("No-target references must not carry a combatant id");
        }
    }

    public static TargetReference none() {
        return new TargetReference(TargetType.NONE, null);
    }

    public static TargetReference enemy(CombatantId combatantId) {
        return new TargetReference(TargetType.ENEMY, combatantId);
    }

    public static TargetReference enemy(Combatant combatant) {
        return enemy(Objects.requireNonNull(combatant, "combatant").combatantId());
    }

    public Combatant resolveFrom(List<Combatant> livingEnemies) {
        if (type == TargetType.NONE) {
            return null;
        }
        return livingEnemies.stream()
            .filter(enemy -> enemy.combatantId().equals(combatantId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown enemy target: " + combatantId));
    }

    public enum TargetType {
        NONE,
        ENEMY
    }
}
