package sc2002.turnbased.actions;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.ArcanePowerStatusEffect;
import sc2002.turnbased.domain.status.StatusEffectObservationScope;
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
            int damage = Math.max(0, attackUsed - enemy.getDefense());
            int hpBefore = enemy.getCurrentHp();
            enemy.receiveDamage(damage);

            List<sc2002.turnbased.domain.status.event.StatusEffectEvent> statusEffectEvents = List.of();
            if (!enemy.isAlive()) {
                try (StatusEffectObservationScope observation = actor.statusEffects().openObservation()) {
                    actor.addStatusEffect(new ArcanePowerStatusEffect(10));
                    statusEffectEvents = observation.observedEvents();
                }
            }

            events.add(new ActionEvent(
                actor.getName(),
                getName(),
                enemy.getName(),
                hpBefore,
                enemy.getCurrentHp(),
                attackUsed,
                enemy.getDefense(),
                damage,
                !enemy.isAlive(),
                statusEffectEvents
            ));
        }

        return events;
    }
}
