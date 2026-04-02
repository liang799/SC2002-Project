# Mermaid UML Class Diagram

This class diagram reflects the current codebase, including the native CLI flow, live battle-event streaming, setup/configuration objects, and the boundary-control-entity structure used by the project.

```mermaid
classDiagram
direction LR

namespace ui {
    class TurnBasedArenaCli {
        <<boundary>>
        -ui : ConsoleBattleUi
        -battleSetupFactory : BattleSetupFactory
        -formatter : BattleConsoleFormatter
        +TurnBasedArenaCli(ui:ConsoleBattleUi)
        +main(args:String[]) void
        +run() void
    }

    class ConsoleBattleUi {
        <<boundary>>
        +ConsoleBattleUi(scanner:Scanner, out:PrintStream)
        +showLoadingScreen() void
        +promptForPlayerType() PlayerType
        +promptForDifficultyLevel() DifficultyLevel
        +promptForItems(itemCount:int) List~ItemType~
        +showConfigurationSummary(configuration:GameConfiguration) void
        +showPlayerTurn(roundNumber:int, player:PlayerCharacter, enemies:List~Combatant~, inventory:Inventory) void
        +promptForSelection(prompt:String, options:List~String~) int
        +showMessage(message:String) void
        +showBattleTranscript(lines:List~String~) void
        +showBattleLines(lines:List~String~) void
        +promptPostGameChoice() PostGameChoice
    }

    class CliPlayerDecisionProvider {
        <<boundary>>
        -ui : ConsoleBattleUi
        -inventory : Inventory
        +CliPlayerDecisionProvider(ui:ConsoleBattleUi, inventory:Inventory)
        +decide(roundNumber:int, player:PlayerCharacter, livingEnemies:List~Combatant~) PlayerDecision
    }

    class BattleConsoleFormatter {
        <<boundary>>
        +format(events:List~BattleEvent~) List~String~
    }

    class PostGameChoice {
        <<enumeration>>
        REPLAY
        NEW_GAME
        EXIT
    }
}

namespace engine {
    class BattleEngine {
        <<control>>
        -player : PlayerCharacter
        -initialEnemies : List~Combatant~
        -reserveEnemies : List~Combatant~
        -spawnedEnemies : List~Combatant~
        -inventory : Inventory
        -turnOrderStrategy : TurnOrderStrategy
        -events : List~BattleEvent~
        +BattleEngine(setup:BattleSetup, turnOrderStrategy:TurnOrderStrategy)
        +runRounds(roundCount:int, provider:PlayerDecisionProvider) List~BattleEvent~
        +runRounds(roundCount:int, provider:PlayerDecisionProvider, listener:BattleEventListener) List~BattleEvent~
        +runUntilBattleEnds(provider:PlayerDecisionProvider) List~BattleEvent~
        +runUntilBattleEnds(provider:PlayerDecisionProvider, listener:BattleEventListener) List~BattleEvent~
    }

    class BattleEventListener {
        <<interface>>
        +onEvent(event:BattleEvent) void
    }

    class BattleSetupFactory {
        <<control>>
        +create(configuration:GameConfiguration) BattleSetup
    }

    class TurnOrderStrategy {
        <<interface>>
        +determineOrder(combatants:List~Combatant~) List~Combatant~
    }

    class SpeedTurnOrderStrategy {
        <<control>>
        +determineOrder(combatants:List~Combatant~) List~Combatant~
    }

    class PlayerDecisionProvider {
        <<interface>>
        +decide(roundNumber:int, player:PlayerCharacter, livingEnemies:List~Combatant~) PlayerDecision
    }

    class ScriptedDecisionProvider {
        <<control>>
        -decisionsByRound : Map~Integer, PlayerDecision~
        +addDecision(roundNumber:int, decision:PlayerDecision) ScriptedDecisionProvider
        +decide(roundNumber:int, player:PlayerCharacter, livingEnemies:List~Combatant~) PlayerDecision
    }

    class ScenarioScript {
        <<control>>
        -name : String
        -battleSetup : BattleSetup
        -decisionProvider : PlayerDecisionProvider
        -roundCount : int
        +getName() String
        +getBattleSetup() BattleSetup
        +getDecisionProvider() PlayerDecisionProvider
        +getRoundCount() int
    }

    class AppendixAScenarios {
        <<control>>
        +all() List~ScenarioScript~
        +easyWarrior() ScenarioScript
        +mediumWarrior() ScenarioScript
        +mediumWizard() ScenarioScript
    }

    class BattleSetup {
        <<entity>>
        -player : PlayerCharacter
        -initialEnemies : List~Combatant~
        -backupEnemies : List~Combatant~
        -inventory : Inventory
        +getPlayer() PlayerCharacter
        +getInitialEnemies() List~Combatant~
        +getBackupEnemies() List~Combatant~
        +getInventory() Inventory
    }

    class GameConfiguration {
        <<entity>>
        -playerType : PlayerType
        -difficultyLevel : DifficultyLevel
        -selectedItems : List~ItemType~
        +playerType() PlayerType
        +difficultyLevel() DifficultyLevel
        +selectedItems() List~ItemType~
    }

    class PlayerDecision {
        <<entity>>
        -action : BattleAction
        -targetReference : TargetReference
        +action() BattleAction
        +targetReference() TargetReference
        +targeted(action:BattleAction, targetName:String) PlayerDecision
        +untargeted(action:BattleAction) PlayerDecision
    }

    class TargetReference {
        <<entity>>
        -type : TargetType
        -combatantName : String
        +none() TargetReference
        +enemy(combatantName:String) TargetReference
        +resolveFrom(livingEnemies:List~Combatant~) Combatant
    }

    class TargetType {
        <<enumeration>>
        NONE
        ENEMY
    }

    class PlayerType {
        <<enumeration>>
        WARRIOR
        WIZARD
        -displayName : String
        -specialSkillName : String
        +getDisplayName() String
        +getSpecialSkillName() String
        +createPlayer() PlayerCharacter
    }

    class DifficultyLevel {
        <<enumeration>>
        EASY
        MEDIUM
        HARD
        -displayName : String
        -initialSpawnDescription : String
        -backupSpawnDescription : String
        -initialEnemyCount : int
        -backupEnemyCount : int
        +getDisplayName() String
        +getInitialSpawnDescription() String
        +getBackupSpawnDescription() String
        +getInitialEnemyCount() int
        +getBackupEnemyCount() int
        +getTotalEnemyCount() int
    }
}

namespace actions {
    class ActionExecutionContext {
        <<interface>>
        +getLivingEnemies() List~Combatant~
        +getLivingEnemiesInTurnOrder() List~Combatant~
        +getInventory() Inventory
    }

    class BattleAction {
        <<interface>>
        +getName() String
        +advancesCooldown() boolean
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class TargetingMode {
        <<enumeration>>
        NONE
        SINGLE_ENEMY
    }

    class BasicAttackAction {
        +getName() String
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class DefendAction {
        +getName() String
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class ShieldBashAction {
        -startsCooldown : boolean
        +ShieldBashAction()
        +ShieldBashAction(startsCooldown:boolean)
        +getName() String
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class ArcaneBlastAction {
        -startsCooldown : boolean
        +ArcaneBlastAction()
        +ArcaneBlastAction(startsCooldown:boolean)
        +getName() String
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class UsePotionAction {
        +getName() String
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class UseSmokeBombAction {
        +getName() String
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class UseSpecialSkillAction {
        +getName() String
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }

    class UsePowerStoneSkillAction {
        +getName() String
        +advancesCooldown() boolean
        +targetingMode(actor:Combatant) TargetingMode
        +execute(context:ActionExecutionContext, actor:Combatant, target:Combatant) List~BattleEvent~
    }
}

namespace domain {
    class Combatant {
        <<entity>>
        -name : String
        -baseStats : CombatStats
        -attack : int
        -currentHp : int
        -specialSkillCooldown : int
        -statusEffects : List~StatusEffect~
        +beginTurn() void
        +receiveDamage(damage:int) void
        +adjustIncomingDamage(attacker:Combatant, damage:int, notes:List~String~) int
        +heal(amount:int) void
        +adjustAttack(amount:int) void
        +addStatusEffect(effect:StatusEffect) void
        +openTurnWindow() TurnWindow
        +completeRound() void
    }

    class PlayerCharacter {
        <<entity>>
        +createSpecialSkillAction(startsCooldown:boolean) BattleAction
    }

    class EnemyCombatant {
        <<entity>>
        +selectAction(context:ActionExecutionContext) BattleAction
    }

    class Warrior {
        +Warrior()
        +createSpecialSkillAction(startsCooldown:boolean) BattleAction
    }

    class Wizard {
        +Wizard()
        +createSpecialSkillAction(startsCooldown:boolean) BattleAction
    }

    class Goblin {
        +Goblin(name:String)
    }

    class Wolf {
        +Wolf(name:String)
    }

    class CombatStats {
        <<value object>>
        +maxHp : int
        +attack : int
        +defense : int
        +speed : int
        +maxHp() int
        +attack() int
        +defense() int
        +speed() int
    }

    class Inventory {
        <<entity>>
        -itemCounts : EnumMap~ItemType, Integer~
        +add(itemType:ItemType, count:int) void
        +countOf(itemType:ItemType) int
        +use(itemType:ItemType) void
        +snapshot() Map~ItemType, Integer~
    }

    class ItemType {
        <<enumeration>>
        POTION
        POWER_STONE
        SMOKE_BOMB
        -displayName : String
        -description : String
        +getDisplayName() String
        +getDescription() String
    }

    class StatusEffect {
        <<interface>>
        +getName() String
        +onTurnOpportunity() TurnEffectResolution
        +getDefenseModifier() int
        +adjustIncomingDamage(owner:Combatant, attacker:Combatant, damage:int, notes:List~String~) int
        +onRoundCompleted() void
        +isExpired() boolean
    }

    class StunStatusEffect {
        -blockedTurnsRemaining : int
        +StunStatusEffect(blockedTurnsRemaining:int)
        +getName() String
        +onTurnOpportunity() TurnEffectResolution
        +isExpired() boolean
    }

    class DefendStatusEffect {
        -roundsRemaining : int
        +DefendStatusEffect(roundsRemaining:int)
        +getName() String
        +onTurnOpportunity() TurnEffectResolution
        +getDefenseModifier() int
        +onRoundCompleted() void
        +isExpired() boolean
    }

    class SmokeBombStatusEffect {
        -protectedEnemyAttacksRemaining : int
        +SmokeBombStatusEffect(protectedEnemyAttacksRemaining:int)
        +getName() String
        +onTurnOpportunity() TurnEffectResolution
        +adjustIncomingDamage(owner:Combatant, attacker:Combatant, damage:int, notes:List~String~) int
        +isExpired() boolean
    }

    class TurnEffectResolution {
        <<value object>>
        -blocksAction : boolean
        -blockerLabel : String
        -notes : List~String~
        +blocksAction() boolean
        +blockerLabel() String
        +notes() List~String~
    }

    class TurnWindow {
        <<value object>>
        -blocked : boolean
        -blockerLabel : String
        -notes : List~String~
        +isBlocked() boolean
        +getBlockerLabel() String
        +getNotes() List~String~
    }
}

namespace report {
    class BattleEvent {
        <<interface>>
    }

    class RoundStartEvent {
        -roundNumber : int
        +getRoundNumber() int
    }

    class ActionEvent {
        -actorName : String
        -actionName : String
        -targetName : String
        -hpBefore : int
        -hpAfter : int
        -attackerAttack : int
        -targetDefense : int
        -damage : int
        -notes : List~String~
        +getActorName() String
        +getActionName() String
        +getTargetName() String
        +getHpBefore() int
        +getHpAfter() int
        +getAttackerAttack() int
        +getTargetDefense() int
        +getDamage() int
        +getNotes() List~String~
    }

    class SkippedTurnEvent {
        -combatantName : String
        -reason : String
        -notes : List~String~
        +getCombatantName() String
        +getReason() String
        +getNotes() List~String~
    }

    class NarrationEvent {
        -text : String
        +getText() String
    }

    class CombatantSummary {
        -name : String
        -currentHp : int
        -maxHp : int
        -currentAttack : int
        -baseAttack : int
        -alive : boolean
        -activeStatuses : List~String~
        +getName() String
        +getCurrentHp() int
        +getMaxHp() int
        +getCurrentAttack() int
        +getBaseAttack() int
        +isAlive() boolean
        +getActiveStatuses() List~String~
    }

    class RoundSummaryEvent {
        -roundNumber : int
        -playerSummary : CombatantSummary
        -enemySummaries : List~CombatantSummary~
        -inventorySnapshot : Map~ItemType, Integer~
        -specialSkillCooldown : int
        +getRoundNumber() int
        +getPlayerSummary() CombatantSummary
        +getEnemySummaries() List~CombatantSummary~
        +getInventorySnapshot() Map~ItemType, Integer~
        +getSpecialSkillCooldown() int
    }
}

Combatant <|-- PlayerCharacter
Combatant <|-- EnemyCombatant
PlayerCharacter <|-- Warrior
PlayerCharacter <|-- Wizard
EnemyCombatant <|-- Goblin
EnemyCombatant <|-- Wolf
Combatant *-- CombatStats
Combatant *-- StatusEffect : active effects

StatusEffect <|.. StunStatusEffect
StatusEffect <|.. DefendStatusEffect
StatusEffect <|.. SmokeBombStatusEffect
StatusEffect --> TurnEffectResolution : returns
Combatant --> TurnWindow : creates

BattleAction <|.. BasicAttackAction
BattleAction <|.. DefendAction
BattleAction <|.. ShieldBashAction
BattleAction <|.. ArcaneBlastAction
BattleAction <|.. UsePotionAction
BattleAction <|.. UseSmokeBombAction
BattleAction <|.. UseSpecialSkillAction
BattleAction <|.. UsePowerStoneSkillAction
ActionExecutionContext <|.. BattleEngine

Warrior ..> ShieldBashAction : creates
Wizard ..> ArcaneBlastAction : creates
UseSpecialSkillAction ..> PlayerCharacter : delegates to special skill
UsePowerStoneSkillAction ..> PlayerCharacter : delegates to special skill

BattleEvent <|.. RoundStartEvent
BattleEvent <|.. ActionEvent
BattleEvent <|.. SkippedTurnEvent
BattleEvent <|.. NarrationEvent
BattleEvent <|.. RoundSummaryEvent
RoundSummaryEvent o-- CombatantSummary
RoundSummaryEvent --> ItemType : inventory snapshot

TurnOrderStrategy <|.. SpeedTurnOrderStrategy
PlayerDecisionProvider <|.. ScriptedDecisionProvider
PlayerDecisionProvider <|.. CliPlayerDecisionProvider
TargetReference --> TargetType
PlayerDecision --> BattleAction
PlayerDecision --> TargetReference

BattleSetupFactory --> GameConfiguration
BattleSetupFactory --> BattleSetup
BattleSetup --> PlayerCharacter
BattleSetup --> Combatant
BattleSetup --> Inventory
GameConfiguration --> PlayerType
GameConfiguration --> DifficultyLevel
GameConfiguration --> ItemType

ScenarioScript --> BattleSetup
ScenarioScript --> PlayerDecisionProvider
AppendixAScenarios ..> ScenarioScript : creates

BattleEventListener --> BattleEvent
BattleEngine --> BattleSetup
BattleEngine --> TurnOrderStrategy
BattleEngine --> PlayerDecisionProvider
BattleEngine --> PlayerDecision
BattleEngine --> BattleAction
BattleEngine --> BattleEvent
BattleEngine --> BattleEventListener : emits to

TurnBasedArenaCli --> ConsoleBattleUi
TurnBasedArenaCli --> BattleSetupFactory
TurnBasedArenaCli --> CliPlayerDecisionProvider
TurnBasedArenaCli --> BattleEngine
TurnBasedArenaCli --> BattleConsoleFormatter
TurnBasedArenaCli ..> BattleEventListener : lambda callback
ConsoleBattleUi --> PlayerType
ConsoleBattleUi --> DifficultyLevel
ConsoleBattleUi --> ItemType
ConsoleBattleUi --> GameConfiguration
CliPlayerDecisionProvider --> ConsoleBattleUi
CliPlayerDecisionProvider --> Inventory
BattleConsoleFormatter --> BattleEvent
```
