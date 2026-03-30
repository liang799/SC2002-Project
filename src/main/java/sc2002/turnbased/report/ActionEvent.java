package sc2002.turnbased.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionEvent implements BattleEvent {
    private final String actorName;
    private final String actionName;
    private final String targetName;
    private final int hpBefore;
    private final int hpAfter;
    private final int attackerAttack;
    private final int targetDefense;
    private final int damage;
    private final List<String> notes;

    public ActionEvent(
        String actorName,
        String actionName,
        String targetName,
        int hpBefore,
        int hpAfter,
        int attackerAttack,
        int targetDefense,
        int damage,
        List<String> notes
    ) {
        this.actorName = actorName;
        this.actionName = actionName;
        this.targetName = targetName;
        this.hpBefore = hpBefore;
        this.hpAfter = hpAfter;
        this.attackerAttack = attackerAttack;
        this.targetDefense = targetDefense;
        this.damage = damage;
        this.notes = new ArrayList<>(notes);
    }

    public String getActorName() {
        return actorName;
    }

    public String getActionName() {
        return actionName;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getHpBefore() {
        return hpBefore;
    }

    public int getHpAfter() {
        return hpAfter;
    }

    public int getAttackerAttack() {
        return attackerAttack;
    }

    public int getTargetDefense() {
        return targetDefense;
    }

    public int getDamage() {
        return damage;
    }

    public List<String> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}
