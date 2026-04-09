package sc2002.turnbased.actions;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.AttackResolution;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.ArcanePowerStatusEffect;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.NarrationEvent;

public class ArcaneBlastAction implements BattleAction {
    @Override
    public String getName() {
        return "Arcane Blast";
    }

    @Override
    public TargetingMode targetingMode(Combatant actor) {
        return TargetingMode.NONE;
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        List<BattleEvent> events = new ArrayList<>();
        List<Combatant> targets = context.getLivingEnemiesInTurnOrder();
        int attackUsed = actor.getAttack();

        events.add(new NarrationEvent(actor.getName() + " -> Arcane Blast -> all enemies"));
        for (Combatant enemy : targets) {
            AttackResolution attackResolution = actor.attack(enemy, attackUsed);
            if (attackResolution.targetEliminated()) {
                attackResolution = attackResolution.appendStatusEffectOutcomes(actor.addStatusEffect(new ArcanePowerStatusEffect(10)));
            }

            events.add(new ActionEvent(actor.getName(), getName(), enemy.getName(), attackResolution));
        }

        return events;
    }
}
