<h1 align="center">SC2002 Turn-Based Combat Arena</h1>

<p align="center">
  <strong>Java 17 combat game with a tested battle engine, native CLI flow, and a 2D Swing arena.</strong>
</p>

<p align="center">
  <a href="https://github.com/liang799/SC2002-Project/actions/workflows/unit-tests.yml">
    <img alt="Tests" src="https://img.shields.io/github/actions/workflow/status/liang799/SC2002-Project/unit-tests.yml?branch=main&label=tests&logo=githubactions&logoColor=white">
  </a>
  <a href="#quality-gates">
    <img alt="Line coverage" src="https://img.shields.io/badge/line%20coverage-53.9%25-yellow">
  </a>
  <a href="https://github.com/liang799/SC2002-Project/blob/main/LICENSE">
    <img alt="License: GPL-3.0" src="https://img.shields.io/github/license/liang799/SC2002-Project?label=license">
  </a>
  <a href="https://github.com/liang799/SC2002-Project/issues">
    <img alt="GitHub issues" src="https://img.shields.io/github/issues/liang799/SC2002-Project?label=issues">
  </a>
  <a href="https://github.com/liang799/SC2002-Project/stargazers">
    <img alt="GitHub stars" src="https://img.shields.io/github/stars/liang799/SC2002-Project?style=flat&label=stars">
  </a>
  <a href="https://github.com/liang799/SC2002-Project/network/members">
    <img alt="GitHub forks" src="https://img.shields.io/github/forks/liang799/SC2002-Project?style=flat&label=forks">
  </a>
  <img alt="Java 17" src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white">
  <img alt="Maven" src="https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white">
</p>

<p align="center">
  <img width="1178" height="702" alt="2D Swing battle arena screenshot" src="https://github.com/user-attachments/assets/b4bc118f-9e32-4ebf-86cd-7d107b017eb7" />
</p>

## Recruiter Snapshot

| Signal | Evidence |
| --- | --- |
| Object-oriented design | Strategy, Factory, Observer, and composition-heavy domain modeling |
| Product surface | CLI gameplay plus an interactive Swing GUI with targeting, items, special skills, and animated feedback |
| Quality bar | 151 automated JUnit 5 tests across unit, integration, and end-to-end flows |
| Delivery discipline | Maven build, GitHub Actions CI, JaCoCo reports, packaged CLI/GUI jars, and tag-based releases |
| Maintainability | Clear boundaries between domain, engine, reporting, setup, CLI, and GUI layers |

## About

This repository contains a Java turn-based combat game built for the SC2002 assignment. The project treats a compact game brief as a maintainable software system: battle rules live in the domain and engine layers, user interaction is handled at the boundaries, and automated tests lock down core gameplay behavior.

## Gameplay

- Play as a `Warrior` or `Wizard`
- Fight `Goblin` and `Wolf` enemy waves across Easy, Medium, and Hard setups
- Use `Basic Attack`, `Defend`, class-specific special skills, `Potion`, `Smoke Bomb`, and `Power Stone`
- Apply status effects including `Arcane Power`, `Defend`, `Strength Boost`, `Smoke Bomb`, and `Stun`
- Configure custom waves with backup enemy spawn handling
- Run scripted Appendix A scenarios for Easy Warrior, Medium Warrior, and Medium Wizard
- Choose between native CLI gameplay and a 2D Swing GUI battle arena

## Architecture

| Package | Responsibility |
| --- | --- |
| `bootstrap` | Composition root for factories, actions, and status-effect registry creation |
| `domain` | Combatants, stats, inventory, value objects, and special-skill state |
| `domain.status` | Status-effect abstractions, concrete effects, outcomes, and registry logic |
| `actions` | Battle action strategies for attacks, defense, items, and special skills |
| `engine` | Battle orchestration, setup, turn ordering, decisions, waves, and event streaming |
| `report` | Structured battle events, combatant summaries, and status-effect reporting |
| `ui` | CLI entry points, prompts, transcript formatting, and demo runners |
| `ui.gui` | Swing MVC entry point, controller, command resolution, playback, setup, and views |
| `test` | Unit, integration, and end-to-end validation helpers and scenarios |

## Design Patterns

- Strategy: `BattleAction`, `StatusEffect`, `TurnOrderStrategy`, and `PlayerDecisionProvider` vary behavior behind stable interfaces
- Factory: `CombatantFactory`, `SpecialSkillFactory`, and `StatusEffectRegistryFactory` centralize object creation
- Observer: `BattleEventListener` streams battle events to CLI and GUI consumers
- Composition-heavy domain model: `Combatant` owns inventory, hit points, combat stats, and status effects, while `PlayerCharacter` owns its special skill

## Run Locally

Requires Java 17 and Maven.

Build the project:

```bash
mvn -DskipTests package
```

Run the packaged CLI:

```bash
java -jar target/turnbased-arena-1.0-SNAPSHOT-cli.jar
```

Run the packaged 2D GUI:

```bash
java -jar target/turnbased-arena-1.0-SNAPSHOT-gui.jar
```

Run the scripted Appendix demo after compiling:

```bash
java -cp target/classes sc2002.turnbased.ui.EasyRoundsDemo
```

## GUI Controls

- Move with `WASD` or the arrow keys
- Click an enemy to target it, or cycle targets with `Q` and `E`
- Use `1` to `4` to navigate battle actions: `Fight`, `Bag`, `Defend`, and `Target`
- Press `Esc` to return from a submenu to the main battle menu

## Quality Gates

| Command | Purpose |
| --- | --- |
| `mvn test` | Runs the automated JUnit 5 test suite |
| `mvn verify` | Runs tests, builds jars, and generates JaCoCo coverage reports |

Current JaCoCo line coverage from the latest local verification run is **53.9%** (`1,714 / 3,178` lines).

Coverage reports are generated at:

- `target/site/jacoco/index.html`
- `target/site/jacoco/jacoco.xml`

GitHub Actions uploads the HTML report as the `jacoco-coverage-report` artifact on test workflow runs.

## Documentation

The maintained UML source of truth is the PlantUML class diagram:

- `uml-diagrams/plantuml_class_diagram.puml`

Full-resolution PlantUML outputs are included:

- [Class diagram PNG](docs/uml-diagrams/imgs/plantuml_class_diagram.png)
- [Class diagram SVG](docs/uml-diagrams/imgs/plantuml_class_diagram.svg)

Legacy Mermaid diagrams have been moved to an outdated folder and are no longer maintained:

- `outdated/mermaid/README.md`
- `outdated/mermaid/mermaid_class_diagram.md`
- `outdated/mermaid/mermaid_sequence_diagram.md`

## License

This project is licensed under the GNU General Public License v3.0. See [LICENSE](LICENSE) for details.
