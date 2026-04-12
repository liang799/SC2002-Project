package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.EnemyCombatant;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.support.TestDependencies;
import sc2002.turnbased.support.TestEnemyCombatantBuilder;

@Tag("unit")
class BattleStateTest {
    @Test
    void combatantsAliveAtRoundStart_WhenCalled_IncludesPlayerAndLivingEnemiesOnly() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant aliveEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Alive Goblin")
            .withHp(30)
            .build();
        EnemyCombatant deadEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Dead Goblin")
            .withCurrentHp(0)
            .withMaxHp(30)
            .build();

        BattleState battleState = new BattleState(new BattleSetup(player, List.of(aliveEnemy, deadEnemy), List.of()));

        List<Combatant> combatants = battleState.combatantsAliveAtRoundStart();

        assertEquals(List.of(player, aliveEnemy), combatants);
    }

    @Test
    void isBattleOver_WhenOnlyReserveEnemiesRemain_ReturnsFalse() {
        PlayerCharacter player = TestDependencies.warrior();
        EnemyCombatant deadInitialEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Dead Initial")
            .withCurrentHp(0)
            .withMaxHp(30)
            .build();
        EnemyCombatant reserveEnemy = TestEnemyCombatantBuilder.anEnemyCombatant(new BasicAttackAction())
            .named("Reserve")
            .withHp(20)
            .build();

        BattleState battleState = new BattleState(new BattleSetup(player, List.of(deadInitialEnemy), List.of(reserveEnemy)));

        assertFalse(battleState.isBattleOver());
    }

    @Test
    void isBattleOver_WhenPlayerIsDefeated_ReturnsTrue() {
        PlayerCharacter player = TestDependencies.warrior();
        player.receiveDamage(player.getCurrentHp());

        BattleState battleState = new BattleState(new BattleSetup(player, List.of(), List.of()));

        assertTrue(battleState.isBattleOver());
    }
}