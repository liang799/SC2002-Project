package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.status.event.StatusEffectEvent;

public class ActionEvent implements BattleEvent {
    private final String actorName;
    private final String actionName;
    private final String targetName;
    private final int hpBefore;
    private final int hpAfter;
    private final int attackerAttack;
    private final int targetDefense;
    private final int damage;
    private final boolean targetEliminated;
    private final List<StatusEffectEvent> statusEffectEvents;

    public ActionEvent(
        String actorName,
        String actionName,
        String targetName,
        int hpBefore,
        int hpAfter,
        int attackerAttack,
        int targetDefense,
        int damage,
        boolean targetEliminated,
        List<StatusEffectEvent> statusEffectEvents
    ) {
        this.actorName = actorName;
        this.actionName = actionName;
        this.targetName = targetName;
        this.hpBefore = hpBefore;
        this.hpAfter = hpAfter;
        this.attackerAttack = attackerAttack;
        this.targetDefense = targetDefense;
        this.damage = damage;
        this.targetEliminated = targetEliminated;
        this.statusEffectEvents = List.copyOf(Objects.requireNonNull(statusEffectEvents, "statusEffectEvents"));
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

    public boolean isTargetEliminated() {
        return targetEliminated;
    }

    public List<StatusEffectEvent> getStatusEffectEvents() {
        return statusEffectEvents;
    }
}
