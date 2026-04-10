# SC2002 Turn-Based Combat Arena

This repository contains a Java turn-based combat game built for the SC2002 assignment. The production code now includes both CLI and Swing GUI front ends, with the battle domain, actions, engine, reporting, and UI boundaries kept separate so the system is easier to explain, test, and extend.

## Current Scope

The current implementation includes:

- `Warrior` and `Wizard` as playable classes
- `Goblin` and `Wolf` as enemy types
- `BasicAttack`, `Defend`, and class-specific special skills
- item support for `Potion`, `Smoke Bomb`, and `Power Stone`
- status-effect support for `Arcane Power`, `Defend`, `Strength Boost`, `Smoke Bomb`, and `Stun`
- Easy, Medium, and Hard setup support
- custom wave configuration support
- backup enemy spawn handling
- scripted Appendix A scenario support for Easy Warrior, Medium Warrior, and Medium Wizard
- native CLI gameplay with player class, item, difficulty, and custom wave selection
- Swing GUI gameplay with the same battle rules and setup flow
- live battle-event display during gameplay
- structured battle-event reporting for testing and formatting
- verifier coverage for Easy checkpoints, Appendix A scenarios, and the custom battle-flow validation scenario

## Architecture

The project is organized into the following source packages:

- `bootstrap`
  - composition root that wires factories, actions, and registry creation
- `domain`
  - combatants, stats, inventory, value objects, and special-skill state
- `domain.status`
  - status-effect abstractions, concrete effects, outcome records, and the registry that applies them
- `actions`
  - battle action strategies such as basic attacks, defend, item usage, and special-skill execution
- `engine`
  - battle orchestration, setup/configuration, turn ordering, player decisions, waves, and event streaming
- `report`
  - structured battle events, summaries, and status-effect note mapping
- `ui`
  - CLI boundary classes, prompts, transcript formatting, and demo entry points
- `ui.gui`
  - Swing GUI boundary classes and graphical decision input
- `test`
  - executable verifier classes for scenario validation

The class diagram reflects several intentional design patterns in the code:

- Strategy
  - `BattleAction`, `StatusEffect`, `TurnOrderStrategy`, and `PlayerDecisionProvider` vary behavior behind interfaces
- Factory
  - `CombatantFactory`, `SpecialSkillFactory`, and `StatusEffectRegistryFactory` centralize object creation
- Observer
  - `BattleEventListener` streams battle events to CLI and GUI consumers
- Composition-heavy domain model
  - `Combatant` owns `Inventory`, `HitPoints`, `CombatStats`, and `StatusEffectRegistry`
  - `PlayerCharacter` owns `SpecialSkill`

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

Run the Swing GUI:

```powershell
java -cp out sc2002.turnbased.ui.gui.TurnBasedArenaGui
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

The maintained UML source of truth is the PlantUML class diagram:

- `UML Class Diagram/plantuml_class_diagram.puml`

Rendered PlantUML outputs are also included:

- `UML Class Diagram/plantuml_class_diagram.png`
- `UML Class Diagram/plantuml_class_diagram.svg`

Legacy Mermaid diagrams have been moved to an outdated folder and are no longer maintained:

- `outdated/mermaid/README.md`
- `outdated/mermaid/mermaid_class_diagram.md`
- `outdated/mermaid/mermaid_sequence_diagram.md`

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
