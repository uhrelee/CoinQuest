# CoinQuest
2D tileset game built in Java featuring world generation and enemy AI using A* pathfinding

## Demo
![Gameplay Demo](demo.gif)

## Features
- Procedural world generation using seeded randomness for reproducibility
- Playable characters: Princess or Bandit
- Dynamic enemy behavior (player pursuit or area guarding) for enemy AI using A* pathfinding
- Collectible items: coins and healing potions
- Save/load system for persistent gameplay
- High score tracking using SQLite database

## Technical Highlights
- Implemented A* search algorithm with heuristic optimization for efficient pathfinding
- Designed grid-based world representation with collision handling
- Used priority queues and hash maps for optimized graph traversal
- Built modular game architecture separating rendering, logic, and AI systems
