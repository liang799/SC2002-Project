package sc2002.turnbased.engine;

import java.util.function.Supplier;

import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.Warrior;
import sc2002.turnbased.domain.Wizard;

public enum PlayerType {
    WARRIOR("Warrior", "Shield Bash", Warrior::new),
    WIZARD("Wizard", "Arcane Blast", Wizard::new);

    private final String displayName;
    private final String specialSkillName;
    private final Supplier<PlayerCharacter> playerFactory;

    PlayerType(String displayName, String specialSkillName, Supplier<PlayerCharacter> playerFactory) {
        this.displayName = displayName;
        this.specialSkillName = specialSkillName;
        this.playerFactory = playerFactory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpecialSkillName() {
        return specialSkillName;
    }

    public PlayerCharacter createPlayer() {
        return playerFactory.get();
    }
}
