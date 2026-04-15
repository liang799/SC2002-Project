# SC2002 Turn-Based Combat Arena

This repository contains a Java turn-based combat game built for the SC2002 assignment. The production code now includes both CLI and 2D Swing GUI front ends, with the battle domain, actions, engine, reporting, and UI boundaries kept separate so the system is easier to explain, test, and extend.

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
- 2D Swing GUI gameplay with an arena background, player movement, enemy targeting, action hotkeys, HP bars, paced dialogue playback, and animated battle feedback
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
  - MVC Swing GUI entry point
- `ui.gui.view`
  - Swing panels and the `BattleView` contract for the arena, setup form, battle menu, and post-game choices
- `ui.gui.controller`
  - battle-flow orchestration and the bridge from Swing input into the blocking engine decision API
- `ui.gui.model`
  - small GUI session state and player-turn command records
- `ui.gui.command`
  - battle-menu command resolution into engine `PlayerDecision` objects
- `ui.gui.playback`
  - paced event narration and dialogue formatting
- `ui.gui.setup`
  - setup/replay launch requests
- `ui.gui.util`
  - Swing threading helpers
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

Or build and run the packaged 2D GUI jar:

```bash
mvn -DskipTests package
java -jar target/turnbased-arena-1.0-SNAPSHOT-gui.jar
```

2D GUI controls:

- Move the player with `WASD` or the arrow keys
- Click an enemy to target it, or cycle targets with `Q` and `E`
- Use the `1` to `4` battle menu: `Fight` opens attacks, `Bag` opens items, `Defend` resolves immediately, and `Target` opens target cycling
- Press `Esc` to return to the main battle menu from a submenu

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

- `uml-diagrams/plantuml_class_diagram.puml`

<!-- BEGIN GENERATED UML CLASS DIAGRAMS -->
Full-resolution PlantUML outputs are also included:

- [Class diagram PNG](docs/uml-diagrams/imgs/plantuml_class_diagram.png)
- [Class diagram SVG](docs/uml-diagrams/imgs/plantuml_class_diagram.svg)
<!-- END GENERATED UML CLASS DIAGRAMS -->

Legacy Mermaid diagrams have been moved to an outdated folder and are no longer maintained:

- `outdated/mermaid/README.md`
- `outdated/mermaid/mermaid_class_diagram.md`
- `outdated/mermaid/mermaid_sequence_diagram.md`

## Build Output

Compiled output is ignored through `.gitignore`:

```text
out/
```
