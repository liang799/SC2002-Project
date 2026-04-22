package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.AttackResolution;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;

public record ActionEvent(
    CombatantId actorId,
    String actorName,
    String actionName,
    CombatantId targetId,
    String targetName,
    int hpBefore,
    int hpAfter,
    int attackerAttack,
    int targetDefense,
    int damage,
    boolean targetEliminated,
    List<String> statusEffectNotes
) implements BattleEvent {
    public ActionEvent {
        actorId = Objects.requireNonNull(actorId, "actorId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public ActionEvent(Combatant actor, String actionName, Combatant target, AttackResolution attackResolution) {
        this(
            actor.combatantId(),
            actor.getName(),
            actionName,
            target.combatantId(),
            target.getName(),
            attackResolution.hpBefore(),
            attackResolution.hpAfter(),
            attackResolution.attackUsed(),
            attackResolution.targetDefense(),
            attackResolution.damage(),
            attackResolution.targetEliminated(),
            StatusEffectReportMapper.toNotes(attackResolution.statusEffectOutcomes())
        );
    }

    @Override
    public <T> T visit(Visitor<T> visitor) {
        return visitor.onAction(this);
    }

    public CombatantId getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public String getActionName() {
        return actionName;
    }

    public CombatantId getTargetId() {
        return targetId;
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

    public List<String> getStatusEffectNotes() {
        return statusEffectNotes;
    }
}
