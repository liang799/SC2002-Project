package sc2002.turnbased.domain;

public interface MergeableStatusEffect {
    boolean canMergeWith(StatusEffect other);

    StatusEffect merge(StatusEffect other);
}
