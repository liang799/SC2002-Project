# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is structured in layers so that the domain model, actions, reporting, battle engine, console UI, and testing can remain separate and easier to evolve.

## Current Features

At the current project stage, the repository includes:

- combatant and player domain modeling
- support for both `Warrior` and `Wizard`
- support for both `Goblin` and `Wolf`
- status-effect and inventory abstractions
- reusable action abstractions for `BasicAttack`, `Shield Bash`, and `Arcane Blast`
- item-driven action support for `Potion`, `Smoke Bomb`, and `Power Stone`
- structured reporting models for rounds, narration, and turn results
- a battle engine for turn processing
- speed-based turn order
- scripted player decision support
- Easy-level battle setup
- Medium-level setup preparation
- console formatting for battle events
- a runnable demo entry point for the Easy scenario
- a verification class for the Easy rounds 1-3 checkpoints

## Current Code Structure

### `src/main/java/sc2002/turnbased/domain`

The domain package contains the expanded battle model:

- `Combatant`: shared base type for battle entities and combat state
- `PlayerCharacter`: base type for player-controlled combatants
- `Warrior`: player class with the assignment Warrior stats
- `Wizard`: player class with the assignment Wizard stats
- `Goblin`: enemy class with Goblin stats
- `Wolf`: enemy class with Wolf stats
- `StatusEffect`: abstraction for battle status effects
- `StunStatusEffect`: stun effect model
- `TurnEffectResolution`: result of resolving one status effect
- `TurnWindow`: combined turn state for a combatant
- `Inventory`: stores item counts
- `ItemType`: item enumeration

### `src/main/java/sc2002/turnbased/actions`

The actions package now contains:

- `BattleAction`: common action abstraction
- `ActionExecutionContext`: action-facing view of engine state
- `BasicAttackAction`: single-target attack using the assignment damage formula
- `ShieldBashAction`: Warrior special skill that applies damage and stun
- `ArcaneBlastAction`: Wizard special skill affecting all current enemies
- `UsePotionAction`: item action for healing
- `UseSmokeBombAction`: item action for suppressing enemy damage
- `UsePowerStoneSkillAction`: item action for triggering a special skill without normal cooldown handling

### `src/main/java/sc2002/turnbased/report`

The report package provides structured output models:

- `BattleEvent`: common event marker
- `ActionEvent`: action execution details
- `RoundStartEvent`: signals the beginning of a round
- `SkippedTurnEvent`: represents skipped turns caused by stun or elimination
- `CombatantSummary`: round-end snapshot of one combatant
- `RoundSummaryEvent`: full round-end battle summary
- `NarrationEvent`: free-form battle narration line

### `src/main/java/sc2002/turnbased/engine`

The engine package drives battle flow:

- `TurnOrderStrategy`: abstraction for turn ordering
- `SpeedTurnOrderStrategy`: speed-based turn ordering implementation
- `PlayerDecision`: one player choice for a turn
- `PlayerDecisionProvider`: abstraction for supplying player decisions
- `ScriptedDecisionProvider`: scripted player input provider
- `BattleSetup`: stores the player, enemies, backup enemies, and inventory
- `EasyLevelSetup`: creates the Easy-level initial battle state
- `MediumLevelSetup`: prepares the Medium-level battle state
- `BattleEngine`: processes rounds, turns, actions, cooldowns, and summaries

### `src/main/java/sc2002/turnbased/ui`

The UI package provides the CLI-facing layer:

- `BattleConsoleFormatter`: converts structured battle events into readable console lines
- `EasyRoundsDemo`: runs the current Easy scenario flow and prints the result

### `src/test/java/sc2002/turnbased`

The test package currently includes:

- `EasyLevelRoundsVerifier`: checks the Easy rounds 1-3 state progression against the expected values

## Current Scope

The repository currently covers:

- combatant representation for Warrior, Wizard, Goblin, and Wolf
- status-effect modeling
- inventory modeling
- action abstractions, including special skills and item actions
- structured battle-reporting models
- turn-order and battle-engine flow
- Easy setup support and Medium-ready setup support
- console formatting and a runnable demo entry point
- verification of the Easy rounds 1-3 milestone

Full Appendix A scenario scripting and expanded scenario verification are intended to be added in later phases.

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
