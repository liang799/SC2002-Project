package sc2002.turnbased.domain.status;

public interface MergeableStatusEffect {
    boolean canMergeWith(StatusEffect other);

    StatusEffect merge(StatusEffect other);
}
