# CoinQuest
2D tileset game built in Java featuring world generation and enemy AI using A* pathfinding

[March 2026: currently being updated] 
## Demo
[insert demo here] ![Gameplay Demo](demo.gif)

## Features
- Procedural world generation using seeded randomness for reproducibility
- Playable characters: Princess or Bandit
- Dynamic enemy behavior (player pursuit or area guarding) for enemy AI using A* pathfinding
- Collectible items: coins and healing potions
- Save/load system for persistent gameplay
- High score tracking using SQLite database

## Technical Highlights
- Designed grid-based world representation with collision handling
- Updated from BFS search to A* search algorithm with heuristic optimization for more efficient pathfinding
- Built modular game architecture separating rendering, logic, and AI systems
