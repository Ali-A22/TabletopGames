# Group B — Opponent-Aware MCTS Agent for Sushi Go

## Overview
This project implements an Opponent-Aware Monte Carlo Tree Search (OppAwareMCTSPlayer) agent for the game *Sushi Go!* using the Tabletop Agent Games (TAG) framework.

The agent extends `BasicMCTSPlayer` with:
- **Opponent Modelling** through `FrequencyOpponentModel`
- **Opponent-Aware Heuristic Evaluation** via `OpponentAwareHeuristic`
- **Enhanced MCTS parameters** (tree depth, rollout length, exploration constant)

All work is contained entirely within `players.groupB`.  
No changes were made to any TAG framework or game files.

---

## File Structure

```text
TabletopGames/
│
├── json/
│   ├── agents/
│   │   ├── OppAwareMCTSPlayer.json
│   │   ├── BasicMCTSPlayer.json
│   │   └── RandomPlayer.json
│   └── experiments/
│       └── SushiGo_GroupB.json
│
├── src/main/java/players/groupB/
│   ├── OppAwareMCTSPlayer.java
│   ├── FrequencyOpponentModel.java
│   ├── OpponentAwareHeuristic.java
│   ├── GroupBMCTSPlayer.java
│   ├── GroupBTreeNode.java
│   ├── RunOppAware.java
│   └── README.md
│
└── metrics/out/
```
---
## How to Run the Agent


###  IntelliJ Run Configuration

You can easily execute your OppAwareMCTS agent through IntelliJ IDEA by setting up a custom **Run Configuration**.

**Configuration Path:**  
`Run → Edit Configurations → + → Application`

**Settings:**
```text
Main class:        evaluation.RunGames
Program arguments: config=json/experiments/SushiGo_GroupB.json
Working directory: C:\Users\alias\Documents\TabletopGames
Java SDK:          17 or later
```
The experiment configuration file (SushiGo_GroupB.json) is located in
json/experiments/, and it automatically loads the agents defined in
json/agents/ — including OppAwareMCTSPlayer.json, BasicMCTSPlayer.json, and RandomPlayer.json.

After setting this up, click Run ▶ to start the tournament.
All experiment outputs will appear in:
metrics/out/OppAwareTournament/

### (Optional) Local Testing — `RunOppAware.java`

You can also test your **OppAwareMCTS** agent independently without relying on any JSON configuration by running the provided `RunOppAware.java` class.

**Configuration Path:**  
`src/main/java/players/groupB/RunOppAware.java`

**Description:**  
This standalone runner automatically creates a small 3-player tournament between:
-  **OppAwareMCTS**
-  **BasicMCTS**
-  **RandomPlayer**

It uses a built-in configuration (`dummyConfig`) and prints all tournament results directly to the console.

**Execution Steps:**
1. Open `RunOppAware.java` in IntelliJ.
2. Right-click inside the file and select **Run 'RunOppAware.main()'**.
3. Observe the tournament results in the Run console output.

**Expected Output Example:**
```text
Running OppAwareMCTS tournament...
Game: SushiGo, Players: 3, Mode: EXHAUSTIVE, TotalGames: 18, GamesPerMatchup: 3
...
✅ Tournament finished successfully!
```

---

##  Baseline Experiment

The baseline tournament evaluates the performance of **OppAwareMCTS** against the standard **BasicMCTS** and **RandomPlayer** agents under controlled conditions.

**Configuration File:**  
`json/experiments/SushiGo_GroupB.json`

**Experiment Setup:**
```text
Game:             SushiGo
Players:          OppAwareMCTS, BasicMCTS, RandomPlayer
Mode:             Random
Matchups:         500
Seed:             42
Thinking Time:    ~1000 ms per move
Total Runtime:    ~2 minutes 18 seconds (measured manually)
Destination Dir:  metrics/out/OppAwareTournament
```
**Results Summary:**

| Agent               | Win Rate (%)   | Mean Ordinal    | Mean Score |
| :------------------ | :------------- | :-------------- | :--------- |
|  **OppAwareMCTS** | **58.2 ± 2.2** | **1.42 ± 0.02** | **52.67**  |
|  BasicMCTSPlayer  | 41.8 ± 2.2     | 1.64 ± 0.03     | 47.06      |
|  RandomPlayer     | 0.0 ± 0.2      | 2.93 ± 0.01     | 31.28      |



