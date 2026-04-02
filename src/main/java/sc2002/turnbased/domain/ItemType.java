package sc2002.turnbased.domain;

public enum ItemType {
    POTION("Potion", "Heal 100 HP, capped at max HP"),
    POWER_STONE("Power Stone", "Trigger your special skill once without starting or changing cooldown"),
    SMOKE_BOMB("Smoke Bomb", "Enemy attacks deal 0 damage for the current and next protected turn");

    private final String displayName;
    private final String description;

    ItemType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
