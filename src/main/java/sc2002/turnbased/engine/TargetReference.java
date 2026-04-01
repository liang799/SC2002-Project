package sc2002.turnbased.engine;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;

public record TargetReference(TargetType type, String combatantName) {
    public TargetReference {
        Objects.requireNonNull(type, "type");
        if (type == TargetType.ENEMY && (combatantName == null || combatantName.isBlank())) {
            throw new IllegalArgumentException("Enemy targets must have a combatant name");
        }
        if (type == TargetType.NONE && combatantName != null) {
            throw new IllegalArgumentException("No-target references must not carry a combatant name");
        }
    }

    public static TargetReference none() {
        return new TargetReference(TargetType.NONE, null);
    }

    public static TargetReference enemy(String combatantName) {
        return new TargetReference(TargetType.ENEMY, combatantName);
    }

    public Combatant resolveFrom(List<Combatant> livingEnemies) {
        if (type == TargetType.NONE) {
            return null;
        }
        return livingEnemies.stream()
            .filter(enemy -> enemy.getName().equals(combatantName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown enemy target: " + combatantName));
    }

    public enum TargetType {
        NONE,
        ENEMY
    }
}
