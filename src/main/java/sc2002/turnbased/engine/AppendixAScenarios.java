package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.actions.UsePowerStoneSkillAction;
import sc2002.turnbased.actions.UseSmokeBombAction;

public final class AppendixAScenarios {
    private AppendixAScenarios() {
    }

    public static List<ScenarioScript> all() {
        return List.of(easyWarrior(), mediumWarrior(), mediumWizard());
    }

    public static ScenarioScript easyWarrior() {
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new BasicAttackAction(), "Goblin A"))
            .addDecision(2, PlayerDecision.targeted(new UseSpecialSkillAction(), "Goblin A"))
            .addDecision(3, PlayerDecision.targeted(new BasicAttackAction(), "Goblin A"))
            .addDecision(4, PlayerDecision.untargeted(new UseSmokeBombAction()))
            .addDecision(5, PlayerDecision.targeted(new UseSpecialSkillAction(), "Goblin B"))
            .addDecision(6, PlayerDecision.targeted(new BasicAttackAction(), "Goblin C"))
            .addDecision(7, PlayerDecision.untargeted(new UsePotionAction()))
            .addDecision(8, PlayerDecision.targeted(new UseSpecialSkillAction(), "Goblin B"))
            .addDecision(9, PlayerDecision.targeted(new BasicAttackAction(), "Goblin B"))
            .addDecision(10, PlayerDecision.targeted(new BasicAttackAction(), "Goblin C"))
            .addDecision(11, PlayerDecision.targeted(new BasicAttackAction(), "Goblin C"));

        return new ScenarioScript(
            "Easy Difficulty - Warrior",
            EasyLevelSetup.createWarriorPotionSmokeBombSetup(),
            decisions,
            11
        );
    }

    public static ScenarioScript mediumWarrior() {
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.targeted(new UseSpecialSkillAction(), "Wolf"))
            .addDecision(2, PlayerDecision.targeted(new BasicAttackAction(), "Wolf"))
            .addDecision(3, PlayerDecision.targeted(new BasicAttackAction(), "Goblin"))
            .addDecision(4, PlayerDecision.targeted(new BasicAttackAction(), "Goblin"))
            .addDecision(5, PlayerDecision.targeted(new UseSpecialSkillAction(), "Goblin"))
            .addDecision(6, PlayerDecision.targeted(new UsePowerStoneSkillAction(), "Wolf A"))
            .addDecision(7, PlayerDecision.targeted(new BasicAttackAction(), "Wolf A"))
            .addDecision(8, PlayerDecision.targeted(new BasicAttackAction(), "Wolf B"))
            .addDecision(9, PlayerDecision.targeted(new BasicAttackAction(), "Wolf B"));

        return new ScenarioScript(
            "Medium Difficulty - Warrior",
            MediumLevelSetup.createWarriorPowerStonePotionSetup(),
            decisions,
            9
        );
    }

    public static ScenarioScript mediumWizard() {
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, PlayerDecision.untargeted(new UseSpecialSkillAction()))
            .addDecision(2, PlayerDecision.targeted(new BasicAttackAction(), "Goblin"))
            .addDecision(3, PlayerDecision.untargeted(new UsePowerStoneSkillAction()));

        return new ScenarioScript(
            "Medium Difficulty - Wizard",
            MediumLevelSetup.createWizardPowerStonePotionSetup(),
            decisions,
            3
        );
    }
}
