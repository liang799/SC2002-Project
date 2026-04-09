package sc2002.turnbased.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record AttackResolution(
    int attackUsed,
    int targetDefense,
    int hpBefore,
    int hpAfter,
    int damage,
    boolean targetEliminated,
    List<String> statusEffectNotes
) {
    public AttackResolution {
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public AttackResolution appendStatusEffectNotes(List<String> additionalNotes) {
        List<String> combinedNotes = new ArrayList<>(statusEffectNotes);
        combinedNotes.addAll(List.copyOf(Objects.requireNonNull(additionalNotes, "additionalNotes")));
        return new AttackResolution(attackUsed, targetDefense, hpBefore, hpAfter, damage, targetEliminated, combinedNotes);
    }
}
