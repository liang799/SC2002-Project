# SC2002 Turn-Based Combat Arena

This repository contains a Java command-line turn-based combat game built for the SC2002 assignment. The code is structured so the battle domain, actions, engine, reporting, UI, and validation remain separated and easier to explain, test, and extend.

## Current Scope

The current implementation includes:

- `Warrior` and `Wizard` as playable classes
- `Goblin` and `Wolf` as enemy types
- `BasicAttack`, `Defend`, and class-specific special skills
- item support for `Potion`, `Smoke Bomb`, and `Power Stone`
- status-effect support for stun, defend, and smoke-bomb protection
- Easy, Medium, and Hard setup support
- backup enemy spawn handling
- scripted Appendix A scenario support for Easy Warrior, Medium Warrior, and Medium Wizard
- native CLI gameplay with player class, item, and difficulty selection
- live battle-event display during gameplay
- structured battle-event reporting for testing and formatting
- verifier coverage for Easy checkpoints, Appendix A scenarios, and the custom battle-flow validation scenario

## Architecture

The project is organized into the following areas:

- `domain`
  - combatants, stats, inventory, and status effects
- `actions`
  - battle actions and item-triggered actions
- `engine`
  - round processing, setup/configuration, turn ordering, and event streaming
- `report`
  - structured battle events and round summaries
- `ui`
  - console interaction, live event display, and formatting
- `test`
  - executable verifier classes for scenario validation

## Running The Game

Compile everything:

```powershell
$files = Get-ChildItem -Recurse -File src/main/java,src/test/java | ForEach-Object { $_.FullName }
javac -d out $files
```

Run the native CLI game:

```powershell
java -cp out sc2002.turnbased.ui.TurnBasedArenaCli
```

Run the scripted Appendix demo:

```powershell
java -cp out sc2002.turnbased.ui.EasyRoundsDemo
```

## Validation

Run the automated test suite with:

```bash
mvn test
```

The test pyramid is organized as:

- `unit`
  - focused domain and value-object tests
- `integration`
  - deterministic engine and scenario-script coverage
- `e2e`
  - full battle-flow validation across setup, engine, decisions, and reporting

## Documentation

The repository includes Mermaid source for the current UML artifacts:

- `mermaid_class_diagram.md`
- `mermaid_sequence_diagram.md`

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
