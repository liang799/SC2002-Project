package sc2002.turnbased.ui.gui.command;

import java.util.Optional;

import sc2002.turnbased.actions.BasicAttackAction;
import sc2002.turnbased.actions.BattleAction;
import sc2002.turnbased.actions.DefendAction;
import sc2002.turnbased.actions.TargetingMode;
import sc2002.turnbased.actions.UsePotionAction;
import sc2002.turnbased.actions.UsePowerStoneSkillAction;
import sc2002.turnbased.actions.UseSmokeBombAction;
import sc2002.turnbased.actions.UseSpecialSkillAction;
import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.domain.ItemType;
import sc2002.turnbased.domain.PlayerCharacter;
import sc2002.turnbased.engine.PlayerDecision;
import sc2002.turnbased.ui.gui.model.PlayerTurnRequest;
import sc2002.turnbased.ui.gui.model.ResolvedPlayerCommand;
import sc2002.turnbased.ui.gui.view.BattleCommandPanel;

public final class PlayerCommandResolver {
    public Optional<ResolvedPlayerCommand> resolve(
        BattleCommandPanel.Command command,
        PlayerTurnRequest turn,
        CombatantId selectedTarget
    ) {
        BattleAction action = createAction(command);
        if (action == null || !isActionAvailable(command, turn.player())) {
            return Optional.empty();
        }

        if (action.targetingMode(turn.player()) == TargetingMode.SINGLE_ENEMY) {
            Combatant target = findLivingEnemy(turn, selectedTarget);
            if (target == null) {
                return Optional.empty();
            }
            return Optional.of(new ResolvedPlayerCommand(
                PlayerDecision.targeted(action, selectedTarget),
                action.getName(),
                " -> " + target.getName()
            ));
        }

        return Optional.of(new ResolvedPlayerCommand(
            PlayerDecision.untargeted(action),
            action.getName(),
            ""
        ));
    }

    private boolean itemAvailable(PlayerCharacter player, ItemType itemType) {
        return player.getInventory().countOf(itemType) > 0;
    }

    private BattleAction createAction(BattleCommandPanel.Command command) {
        return switch (command) {
            case BASIC_ATTACK -> new BasicAttackAction();
            case DEFEND -> new DefendAction();
            case POTION -> new UsePotionAction();
            case SPECIAL_SKILL -> new UseSpecialSkillAction();
            case POWER_STONE -> new UsePowerStoneSkillAction();
            case SMOKE_BOMB -> new UseSmokeBombAction();
            case PREVIOUS_TARGET, NEXT_TARGET -> null;
        };
    }

    private boolean isActionAvailable(BattleCommandPanel.Command command, PlayerCharacter player) {
        return switch (command) {
            case BASIC_ATTACK, DEFEND -> true;
            case POTION -> itemAvailable(player, ItemType.POTION);
            case SPECIAL_SKILL -> player.canUseSpecialSkill();
            case POWER_STONE -> itemAvailable(player, ItemType.POWER_STONE);
            case SMOKE_BOMB -> itemAvailable(player, ItemType.SMOKE_BOMB);
            case PREVIOUS_TARGET, NEXT_TARGET -> false;
        };
    }

    private Combatant findLivingEnemy(PlayerTurnRequest turn, CombatantId selectedTarget) {
        if (selectedTarget == null) {
            return null;
        }
        for (Combatant enemy : turn.livingEnemies()) {
            if (enemy.combatantId().equals(selectedTarget)) {
                return enemy;
            }
        }
        return null;
    }
}
