package sc2002.turnbased.report;

import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.AttackResolution;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;

public class ActionEvent implements BattleEvent {
    private final CombatantId actorId;
    private final String actorName;
    private final String actionName;
    private final CombatantId targetId;
    private final String targetName;
    private final int hpBefore;
    private final int hpAfter;
    private final int attackerAttack;
    private final int targetDefense;
    private final int damage;
    private final boolean targetEliminated;
    private final List<String> statusEffectNotes;

    public ActionEvent(
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
    ) {
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.actorName = actorName;
        this.actionName = actionName;
        this.targetId = Objects.requireNonNull(targetId, "targetId");
        this.targetName = targetName;
        this.hpBefore = hpBefore;
        this.hpAfter = hpAfter;
        this.attackerAttack = attackerAttack;
        this.targetDefense = targetDefense;
        this.damage = damage;
        this.targetEliminated = targetEliminated;
        this.statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
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
