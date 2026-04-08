package sc2002.turnbased.domain;

public interface StatusEffect {
    String name();

    default void onRoundCompleted() {
    }

    boolean isExpired();
}
