package sc2002.turnbased.actions;

import java.util.ArrayList;
import java.util.List;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.status.DamageAdjustment;
import sc2002.turnbased.domain.status.StunStatusEffect;
import sc2002.turnbased.report.ActionEvent;
import sc2002.turnbased.report.BattleEvent;

public class ShieldBashAction implements BattleAction {
    @Override
    public String getName() {
        return "Shield Bash";
    }

    @Override
    public List<BattleEvent> execute(ActionExecutionContext context, Combatant actor, Combatant target) {
        int baseDamage = Math.max(0, actor.getAttack() - target.getDefense());
        DamageAdjustment damageAdjustment = target.statusEffects().adjustIncomingDamage(target, actor, baseDamage);
        List<String> notes = new ArrayList<>(damageAdjustment.notes());
        int damage = damageAdjustment.damage();
        int hpBefore = target.getCurrentHp();
        target.receiveDamage(damage);

        if (target.isAlive()) {
            target.addStatusEffect(new StunStatusEffect(2));
            notes.add(target.getName() + " STUNNED (2 turns)");
        } else {
            notes.add("ELIMINATED");
        }

        return List.of(new ActionEvent(
            actor.getName(),
            getName(),
            target.getName(),
            hpBefore,
            target.getCurrentHp(),
            actor.getAttack(),
            target.getDefense(),
            damage,
            notes
        ));
    }
}
