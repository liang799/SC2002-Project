package sc2002.turnbased.domain.status;

public interface StatusEffect {
    String name();

    default void onRoundCompleted() {
    }

    boolean isExpired();
}
