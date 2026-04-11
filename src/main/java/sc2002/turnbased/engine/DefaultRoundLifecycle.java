package sc2002.turnbased.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.domain.status.CombatantStatusOutcome;
import sc2002.turnbased.report.BattleEvent;
import sc2002.turnbased.report.CombatantSummary;
import sc2002.turnbased.report.RoundSummaryEvent;
import sc2002.turnbased.report.StatusEffectReportEvent;

class DefaultRoundLifecycle implements RoundLifecycle {
    private final PlayerCharacter player;
    private final List<Combatant> spawnedEnemies;

    DefaultRoundLifecycle(PlayerCharacter player, List<Combatant> spawnedEnemies) {
        this.player = player;
        this.spawnedEnemies = spawnedEnemies;
    }

    @Override
    public RoundSummaryEvent createRoundSummary(int roundNumber) {
        List<CombatantSummary> enemySummaries = new ArrayList<>();
        for (Combatant enemy : spawnedEnemies) {
            enemySummaries.add(toSummary(enemy));
        }
        return new RoundSummaryEvent(
            roundNumber,
            toSummary(player),
            enemySummaries,
            player.getInventory().snapshot(),
            player.getSpecialSkillCooldown()
        );
    }

    @Override
    public void completeRound(Consumer<BattleEvent> emit) {
        emitStatusEffectOutcomes(player.completeRound(), emit);
        for (Combatant enemy : spawnedEnemies) {
            emitStatusEffectOutcomes(enemy.completeRound(), emit);
        }
    }

    private void emitStatusEffectOutcomes(
        List<CombatantStatusOutcome> statusEffectOutcomes,
        Consumer<BattleEvent> emit
    ) {
        if (statusEffectOutcomes.isEmpty()) {
            return;
        }
        emit.accept(StatusEffectReportEvent.fromStatusEffectOutcomes(statusEffectOutcomes));
    }

    private CombatantSummary toSummary(Combatant combatant) {
        return new CombatantSummary(
            combatant.combatantId(),
            combatant.getName(),
            combatant.getCurrentHp(),
            combatant.getMaxHp(),
            combatant.getAttack(),
            combatant.getBaseAttack(),
            combatant.isAlive(),
            combatant.getActiveStatuses()
        );
    }
}