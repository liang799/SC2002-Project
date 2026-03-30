# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is being built in layers so that the domain model, battle actions, engine, reporting, and CLI can evolve cleanly and remain understandable.

## Current Features

At the current project stage, the repository includes:

- combatant and player domain modeling
- status-effect and inventory abstractions
- the first battle action abstraction
- support for `BasicAttack`
- support for `Shield Bash`

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

The project now also includes the first action layer:

- `BattleAction`: common action abstraction
- `BasicAttackAction`: single-target attack using the assignment damage formula
- `ShieldBashAction`: Warrior special skill that applies damage and stun

These classes establish how battle behavior can be modeled separately from the combatant objects themselves.

## Current Scope

The repository currently covers:

- combatant representation
- player and enemy inheritance structure
- status-effect modeling
- inventory modeling
- reusable action abstractions for attacking and special-skill behavior

The battle engine, event reporting, and CLI execution flow are intended to be added in later phases.

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
