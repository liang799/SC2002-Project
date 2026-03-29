# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is being built around a modular battle model so that combatants, status effects, inventory, actions, and battle flow can be added in separate layers.

## Current Features

At the current project stage, the repository includes the core domain model together with the first status-effect and inventory abstractions:

- shared combatant state for HP and combat statistics
- a player-side abstraction for playable characters
- a concrete `Warrior` class
- a concrete `Goblin` enemy class
- a status-effect abstraction for turn-based effects
- a concrete stun effect model
- helper types for resolving status effects during a turn
- inventory and item-type tracking

## Current Code Structure

### `src/main/java/sc2002/turnbased/domain`

The project currently focuses on the domain layer:

- `Combatant`: shared base type for battle entities
- `PlayerCharacter`: base type for player-controlled combatants
- `Warrior`: initial player class
- `Goblin`: initial enemy class
- `StatusEffect`: abstraction for battle status effects
- `StunStatusEffect`: stun effect model
- `TurnEffectResolution`: stores the result of resolving one status effect
- `TurnWindow`: stores the combined turn state for a combatant
- `Inventory`: stores item counts
- `ItemType`: item enumeration

These classes prepare the project for later action handling and battle-engine logic while keeping the domain model separate and extensible.

## Current Scope

The repository currently covers:

- combatant representation
- player and enemy inheritance structure
- status-effect modeling
- inventory modeling

The battle engine, action execution, reporting, and CLI flow are intended to be added in later phases.

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
