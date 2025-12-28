# Time Travel Debugger (Java)

A generic, framework-agnostic **time travel debugging engine** for state-based Java applications.

This project allows developers to record, rewind, replay, and persist application state changes
using an action-based architecture with **snapshot optimization and deterministic replay**.

---

##  Features

- Action-based state management
- Undo / Redo support
- Jump to any point in history
- Snapshot + action replay architecture
- Branch truncation after time travel
- Deterministic state reconstruction
- JSON persistence (save & load timelines)
- Generic design (works with any Serializable state)
- Fully tested with JUnit 5

---

##  Architecture Overview

- **Action** – Represents a state-changing event
- **Reducer** – Pure function that applies an action to a state
- **Store** – Manages state history and time travel
- **HistoryEntry** – Stores actions and optional snapshots
- **TimelineIO** – Handles JSON persistence
- **Snapshot Replay** – Restores state from nearest snapshot + action replay

---

##  Example Usage

```java
Store<AppState> store = new Store<>(new AppState(), reducer);

store.dispatch(Action.of("ADD_ITEM", Map.of("name", "milk")));
store.dispatch(Action.of("ADD_ITEM", Map.of("name", "bread")));

store.undo();
store.redo();
