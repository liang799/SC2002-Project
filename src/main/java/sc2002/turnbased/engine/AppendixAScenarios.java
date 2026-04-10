package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UsePowerStoneSkillAction;
import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.Combatant;

public final class AppendixAScenarios {
    private AppendixAScenarios() {
    }

    public static List<ScenarioScript> all() {
        return List.of(easyWarrior(), mediumWarrior(), mediumWizard());
    }

    public static ScenarioScript easyWarrior() {
        BattleSetup battleSetup = EasyLevelSetup.createWarriorPotionSmokeBombSetup();
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin A")))
            .addDecision(2, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Goblin A")))
            .addDecision(3, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin A")))
            .addDecision(4, PlayerDecision.untargeted(new UseSmokeBombAction()))
            .addDecision(5, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Goblin B")))
            .addDecision(6, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin C")))
            .addDecision(7, PlayerDecision.untargeted(new UsePotionAction()))
            .addDecision(8, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Goblin B")))
            .addDecision(9, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin B")))
            .addDecision(10, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin C")))
            .addDecision(11, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin C")));

        return new ScenarioScript(
            "Easy Difficulty - Warrior",
            battleSetup,
            decisions,
            11
        );
    }

    public static ScenarioScript mediumWarrior() {
        BattleSetup battleSetup = MediumLevelSetup.createWarriorPowerStonePotionSetup();
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Wolf")))
            .addDecision(2, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Wolf")))
            .addDecision(3, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin")))
            .addDecision(4, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin")))
            .addDecision(5, PlayerDecision.targeted(new UseSpecialSkillAction(), enemyNamed(battleSetup, "Goblin")))
            .addDecision(6, PlayerDecision.targeted(new UsePowerStoneSkillAction(), enemyNamed(battleSetup, "Wolf A")))
            .addDecision(7, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Wolf A")))
            .addDecision(8, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Wolf B")))
            .addDecision(9, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Wolf B")));

        return new ScenarioScript(
            "Medium Difficulty - Warrior",
            battleSetup,
            decisions,
            9
        );
    }

    public static ScenarioScript mediumWizard() {
        BattleSetup battleSetup = MediumLevelSetup.createWizardPowerStonePotionSetup();
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSpecialSkillAction()))
            .addDecision(2, PlayerDecision.targeted(new BasicAttackAction(), enemyNamed(battleSetup, "Goblin")))
            .addDecision(3, PlayerDecision.untargeted(new UsePowerStoneSkillAction()));

        return new ScenarioScript(
            "Medium Difficulty - Wizard",
            battleSetup,
            decisions,
            3
        );
    }

    private static Combatant enemyNamed(BattleSetup battleSetup, String enemyName) {
        for (Combatant enemy : battleSetup.getInitialEnemies()) {
            if (enemy.getName().equals(enemyName)) {
                return enemy;
            }
        }
        for (Combatant enemy : battleSetup.getBackupEnemies()) {
            if (enemy.getName().equals(enemyName)) {
                return enemy;
            }
        }
        throw new IllegalArgumentException("Unknown enemy in scenario setup: " + enemyName);
    }
}
