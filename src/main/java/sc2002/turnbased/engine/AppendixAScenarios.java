package sc2002.turnbased.engine;

import java.util.List;

import sc2002.turnbased.actions.ArcaneBlastAction;
import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.ShieldBashAction;
import sc2002.turnbased.actions.UsePotionAction;
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
            .addDecision(1, new PlayerDecision(new BasicAttackAction(), "Goblin A"))
            .addDecision(2, new PlayerDecision(new ShieldBashAction(), "Goblin A"))
            .addDecision(3, new PlayerDecision(new BasicAttackAction(), "Goblin A"))
            .addDecision(4, new PlayerDecision(new UseSmokeBombAction(), null))
            .addDecision(5, new PlayerDecision(new ShieldBashAction(), "Goblin B"))
            .addDecision(6, new PlayerDecision(new BasicAttackAction(), "Goblin C"))
            .addDecision(7, new PlayerDecision(new UsePotionAction(), null))
            .addDecision(8, new PlayerDecision(new ShieldBashAction(), "Goblin B"))
            .addDecision(9, new PlayerDecision(new BasicAttackAction(), "Goblin B"))
            .addDecision(10, new PlayerDecision(new BasicAttackAction(), "Goblin C"))
            .addDecision(11, new PlayerDecision(new BasicAttackAction(), "Goblin C"));

        return new ScenarioScript(
            "Easy Difficulty - Warrior",
            EasyLevelSetup.createWarriorPotionSmokeBombSetup(),
            decisions,
            11
        );
    }

    public static ScenarioScript mediumWarrior() {
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, new PlayerDecision(new ShieldBashAction(), "Wolf"))
            .addDecision(2, new PlayerDecision(new BasicAttackAction(), "Wolf"))
            .addDecision(3, new PlayerDecision(new BasicAttackAction(), "Goblin"))
            .addDecision(4, new PlayerDecision(new BasicAttackAction(), "Goblin"))
            .addDecision(5, new PlayerDecision(new ShieldBashAction(), "Goblin"))
            .addDecision(6, new PlayerDecision(new UsePowerStoneSkillAction(new ShieldBashAction(false)), "Wolf A"))
            .addDecision(7, new PlayerDecision(new BasicAttackAction(), "Wolf A"))
            .addDecision(8, new PlayerDecision(new BasicAttackAction(), "Wolf B"))
            .addDecision(9, new PlayerDecision(new BasicAttackAction(), "Wolf B"));

        return new ScenarioScript(
            "Medium Difficulty - Warrior",
            MediumLevelSetup.createWarriorPowerStonePotionSetup(),
            decisions,
            9
        );
    }

    public static ScenarioScript mediumWizard() {
        ScriptedDecisionProvider decisions = new ScriptedDecisionProvider()
            .addDecision(1, new PlayerDecision(new ArcaneBlastAction(), null))
            .addDecision(2, new PlayerDecision(new BasicAttackAction(), "Goblin"))
            .addDecision(3, new PlayerDecision(new UsePowerStoneSkillAction(new ArcaneBlastAction(false)), null));

        return new ScenarioScript(
            "Medium Difficulty - Wizard",
            MediumLevelSetup.createWizardPowerStonePotionSetup(),
            decisions,
            3
        );
    }
}
