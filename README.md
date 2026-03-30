# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is being organized into clear layers so that battle entities, actions, reporting, engine logic, and CLI presentation remain separated and easier to maintain.

## Current Features

At the current project stage, the repository includes:

- combatant and player domain modeling
- status-effect and inventory abstractions
- reusable action abstractions for `BasicAttack` and `Shield Bash`
- structured reporting models for round flow and battle output

## Current Code Structure

### `src/main/java/sc2002/turnbased/domain`

The domain package currently contains the base battle model:

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

The actions package currently contains:

- `BattleAction`: common action abstraction
- `BasicAttackAction`: single-target attack using the assignment damage formula
- `ShieldBashAction`: Warrior special skill that applies damage and stun

### `src/main/java/sc2002/turnbased/report`

The report package now provides structured output models:

- `BattleEvent`: common event marker
- `ActionEvent`: action execution details
- `RoundStartEvent`: signals the beginning of a round
- `SkippedTurnEvent`: represents skipped turns caused by stun or elimination
- `CombatantSummary`: round-end snapshot of one combatant
- `RoundSummaryEvent`: full round-end battle summary

These reporting classes are intended to let later layers format output cleanly without mixing presentation logic into the domain model.

## Current Scope

The repository currently covers:

- combatant representation
- player and enemy inheritance structure
- status-effect modeling
- inventory modeling
- reusable action abstractions
- structured battle-reporting models

The battle engine, turn processing flow, and CLI execution are intended to be added in later phases.

## Project Direction

The full project is intended to support:

- turn-based combat in the command line
- multiple player and enemy types
- actions and special skills
- status effects and items
- battle flow management separated from UI formatting

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
