package sc2002.turnbased.domain;

public enum ItemType {
    POTION("Potion"),
    POWER_STONE("Power Stone"),
    SMOKE_BOMB("Smoke Bomb");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
