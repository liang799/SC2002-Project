# SC2002 Turn-Based Combat Arena

This repository contains a Java implementation of the SC2002 turn-based combat assignment. The project is being built around object-oriented battle components so that player classes, enemies, actions, and battle rules can be implemented in a modular way.

## Current Features

At the current project stage, the repository covers the base combatant model for the battle system:

- shared combatant state for HP and combat statistics
- a player-side abstraction for playable characters
- a concrete `Warrior` class
- a concrete `Goblin` enemy class

## Current Code Structure

### `src/main/java/sc2002/turnbased/domain`

This package currently contains the base domain model for battle participants:

- `Combatant`: the shared base type for battle entities
- `PlayerCharacter`: a base type for player-controlled combatants
- `Warrior`: the initial player class for the assignment
- `Goblin`: the initial enemy class for the assignment

These classes establish the foundation for later battle mechanics such as actions, status effects, turn order, and the battle engine.

## Current Scope

The repository currently focuses on the first part of the game model:

- representing combatants with assignment stats
- separating player and enemy types through inheritance
- preparing the domain structure for later battle logic

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
