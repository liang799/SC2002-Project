package sc2002.turnbased.domain;

public abstract class PlayerCharacter extends Combatant {
    protected PlayerCharacter(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
    }
}
