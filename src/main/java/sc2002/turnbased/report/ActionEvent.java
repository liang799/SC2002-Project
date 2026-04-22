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
        actorName = Objects.requireNonNull(actorName, "actorName");
        actionName = Objects.requireNonNull(actionName, "actionName");
        targetId = Objects.requireNonNull(targetId, "targetId");
        targetName = Objects.requireNonNull(targetName, "targetName");
        statusEffectNotes = List.copyOf(Objects.requireNonNull(statusEffectNotes, "statusEffectNotes"));
    }

    public ActionEvent(Combatant actor, String actionName, Combatant target, AttackResolution attackResolution) {
        this(
            requireActor(actor).combatantId(),
            requireCombatantName(actor, "actor"),
            actionName,
            requireTarget(target).combatantId(),
            requireCombatantName(target, "target"),
            requireAttackResolution(attackResolution).hpBefore(),
            requireAttackResolution(attackResolution).hpAfter(),
            requireAttackResolution(attackResolution).attackUsed(),
            requireAttackResolution(attackResolution).targetDefense(),
            requireAttackResolution(attackResolution).damage(),
            requireAttackResolution(attackResolution).targetEliminated(),
            StatusEffectReportMapper.toNotes(requireAttackResolution(attackResolution).statusEffectOutcomes())
        );
    }

    private static Combatant requireActor(Combatant actor) {
        return Objects.requireNonNull(actor, "actor");
    }

    private static Combatant requireTarget(Combatant target) {
        return Objects.requireNonNull(target, "target");
    }

    private static AttackResolution requireAttackResolution(AttackResolution attackResolution) {
        return Objects.requireNonNull(attackResolution, "attackResolution");
    }

    private static String requireCombatantName(Combatant combatant, String role) {
        return Objects.requireNonNull(Objects.requireNonNull(combatant, role).getName(), role + ".name");
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
