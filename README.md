# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is structured in layers so that the domain model, actions, reporting, battle engine, and console UI can stay separated and easier to understand.

## Current Features

At the current project stage, the repository includes:

- combatant and player domain modeling
- status-effect and inventory abstractions
- reusable action abstractions for `BasicAttack` and `Shield Bash`
- structured reporting models for rounds and turn results
- a battle engine for turn processing
- speed-based turn order
- scripted player decision support
- Easy-level battle setup
- console formatting for battle events
- a runnable demo entry point for the Easy scenario

## Current Code Structure

### `src/main/java/sc2002/turnbased/domain`

The domain package contains the base battle model:

- `Combatant`: shared base type for battle entities
- `PlayerCharacter`: base type for player-controlled combatants
- `Warrior`: initial player class
- `Goblin`: initial enemy class
- `StatusEffect`: abstraction for battle status effects
- `StunStatusEffect`: stun effect model
- `TurnEffectResolution`: result of resolving one status effect
- `TurnWindow`: combined turn state for a combatant
- `Inventory`: stores item counts
- `ItemType`: item enumeration

### `src/main/java/sc2002/turnbased/actions`

The actions package contains:

- `BattleAction`: common action abstraction
- `BasicAttackAction`: single-target attack using the assignment damage formula
- `ShieldBashAction`: Warrior special skill that applies damage and stun

### `src/main/java/sc2002/turnbased/report`

The report package provides structured output models:

- `BattleEvent`: common event marker
- `ActionEvent`: action execution details
- `RoundStartEvent`: signals the beginning of a round
- `SkippedTurnEvent`: represents skipped turns caused by stun or elimination
- `CombatantSummary`: round-end snapshot of one combatant
- `RoundSummaryEvent`: full round-end battle summary

### `src/main/java/sc2002/turnbased/engine`

The engine package drives battle flow:

- `TurnOrderStrategy`: abstraction for turn ordering
- `SpeedTurnOrderStrategy`: speed-based turn ordering implementation
- `PlayerDecision`: one player choice for a turn
- `PlayerDecisionProvider`: abstraction for supplying player decisions
- `ScriptedDecisionProvider`: scripted player input provider
- `BattleSetup`: stores the player, enemies, and inventory for a battle
- `EasyLevelSetup`: creates the Easy-level initial battle state
- `BattleEngine`: processes rounds, turns, actions, cooldowns, and summaries

### `src/main/java/sc2002/turnbased/ui`

The UI package now provides the CLI-facing layer:

- `BattleConsoleFormatter`: converts structured battle events into readable console lines
- `EasyRoundsDemo`: runs the current Easy scenario flow and prints the result

## Current Scope

The repository currently covers:

- combatant representation
- status-effect modeling
- inventory modeling
- action abstractions
- structured battle-reporting models
- turn-order and battle-engine flow for the Easy setup
- console formatting and a runnable demo entry point

The verification layer is intended to be added in the next phase.

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
