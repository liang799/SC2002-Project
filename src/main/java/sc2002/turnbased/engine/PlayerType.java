package sc2002.turnbased.engine;

import sc2002.turnbased.domain.CombatantFactory;
import sc2002.turnbased.domain.PlayerCharacter;

public enum PlayerType {
    WARRIOR("Warrior", "Shield Bash"),
    WIZARD("Wizard", "Arcane Blast");

    private final String displayName;
    private final String specialSkillName;

    PlayerType(String displayName, String specialSkillName) {
        this.displayName = displayName;
        this.specialSkillName = specialSkillName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpecialSkillName() {
        return specialSkillName;
    }

    public PlayerCharacter createPlayer(CombatantFactory combatantFactory) {
        return combatantFactory.createPlayer(this);
    }
}
