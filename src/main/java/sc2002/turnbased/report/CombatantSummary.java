package sc2002.turnbased.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombatantSummary {
    private final String name;
    private final int currentHp;
    private final int maxHp;
    private final int currentAttack;
    private final int baseAttack;
    private final boolean alive;
    private final List<String> activeStatuses;

    public CombatantSummary(
        String name,
        int currentHp,
        int maxHp,
        int currentAttack,
        int baseAttack,
        boolean alive,
        List<String> activeStatuses
    ) {
        this.name = name;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.currentAttack = currentAttack;
        this.baseAttack = baseAttack;
        this.alive = alive;
        this.activeStatuses = new ArrayList<>(activeStatuses);
    }

    public String getName() {
        return name;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentAttack() {
        return currentAttack;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public boolean isAlive() {
        return alive;
    }

    public List<String> getActiveStatuses() {
        return Collections.unmodifiableList(activeStatuses);
    }
}
