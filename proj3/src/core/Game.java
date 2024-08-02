package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;
    private static final int MAX_LEVEL = 2;
    private static final int[] COINS_PER_LEVEL = {101, 201};
    private static final String SAVE_FILE = "proj3/save_game.txt";

    private static final String HEART_RED = "proj3/src/core/game assets/HeartRed.PNG";
    private static final String HEART_GRAY = "proj3/src/core/game assets/HeartGray.PNG";
    private static final String LOSE_SCREEN = "proj3/src/core/game assets/You Lost Screen.png";

    private Player player;
    private ArrayList<Enemy> enemies;
    private TETile[][] world;
    private int collectedCoins = 0;
    private int totalCoins = 0;
    private int level = 1;
    private Font brickSansFont;
    private boolean gameCompleted = false;
    private boolean gameOver = false;
    private Random rand;
    private long randomSeed;
    private boolean quitRequested = false;

    public Game(TETile[][] generatedWorld, int characterChoice, long seed) {
        this.world = generatedWorld;
        this.randomSeed = seed;
        this.rand = new Random(seed);
        initializeFont();
        initializeWorld(characterChoice);
        initializeEnemies();
        setupGraphics();
        System.out.println("Game initialized with new world");
    }

    private Game(GameState state) {
        this.world = state.getWorld();
        this.randomSeed = state.getRandomSeed();
        this.rand = new Random(randomSeed);
        this.collectedCoins = state.getCollectedCoins();
        this.totalCoins = state.getTotalCoins();
        this.level = state.getLevel();
        this.quitRequested = false;
        this.gameCompleted = false;
        this.gameOver = false;
        initializeFont();
        this.player = new Player(state.getPlayerX(), state.getPlayerY(), world, this, state.getCharacterChoice());
        player.setLives(state.getLives());
        initializeEnemies(state.getEnemyPositions());
        setupGraphics();
        System.out.println("Game loaded from saved state");
        initializeGameLoop();
        resetInputHandling();
    }

    private void initializeGameLoop() {
        this.gameCompleted = false;
        this.gameOver = false;
        this.quitRequested = false;
    }

    private void resetInputHandling() {
        // Clear any pending input
        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
        }
    }

    private void initializeFont() {
        try {
            brickSansFont = Font.createFont(Font.TRUETYPE_FONT, new File("proj3/src/core/game assets/NTBrickSans.ttf")).deriveFont(13f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(brickSansFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    private void initializeWorld(int characterChoice) {
        placeCoins();
        placePlayer(characterChoice);
    }

    private void placeCoins() {
        int coinsToPlace = COINS_PER_LEVEL[level - 1];
        totalCoins = coinsToPlace;
        ArrayList<Point> floorTiles = new ArrayList<>();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.Floor) {
                    floorTiles.add(new Point(x, y));
                }
            }
        }

        while (coinsToPlace > 0 && !floorTiles.isEmpty()) {
            int index = rand.nextInt(floorTiles.size());
            Point p = floorTiles.remove(index);
            world[p.x][p.y] = Tileset.FloorWithCoin;
            coinsToPlace--;
        }
    }

    private void placePlayer(int characterChoice) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FloorWithCoin) {
                    player = new Player(x, y, world, this, characterChoice);
                    world[x][y] = Tileset.Floor;
                    totalCoins--;
                    return;
                }
            }
        }
        throw new RuntimeException("No floor tiles found for player placement");
    }

    private void initializeEnemies() {
        enemies = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            placeEnemy();
        }
    }

    private void initializeEnemies(ArrayList<int[]> enemyPositions) {
        enemies = new ArrayList<>();
        for (int[] pos : enemyPositions) {
            enemies.add(new Enemy(pos[0], pos[1], world, player, this));
        }
    }

    private void placeEnemy() {
        int attempts = 0;
        while (attempts < 100) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            if (world[x][y] == Tileset.Floor && !isEnemyAt(x, y)) {
                enemies.add(new Enemy(x, y, world, player, this));
                return;
            }
            attempts++;
        }
        System.out.println("Warning: Could not place an enemy after 100 attempts");
    }

    private void setupGraphics() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    public void gameLoop() {
        while (!gameCompleted && !gameOver && !quitRequested) {
            handleInput();
            if (quitRequested) {
                saveGame();
                System.exit(0);
            }
            moveEnemies();
            checkCollisions();
            render();
            checkLevelCompletion();
            StdDraw.pause(50);
        }
        if (gameCompleted) {
            displayGameCompletionScreen();
        } else if (gameOver) {
            displayGameOverScreen();
        }
    }


    public void continueGameLoop() {
        gameLoop();
    }

    public void handleInput() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            if (key == ':') {
                waitForQuitCommand();
            } else {
                switch (key) {
                    case 'w':
                        player.move(Player.Direction.UP);
                        break;
                    case 's':
                        player.move(Player.Direction.DOWN);
                        break;
                    case 'a':
                        player.move(Player.Direction.LEFT);
                        break;
                    case 'd':
                        player.move(Player.Direction.RIGHT);
                        break;
                    case 'e':
                        player.interact();
                        break;
                }
            }
        }
    }

    private void waitForQuitCommand() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                System.out.println("Quit key pressed: " + key); // Debug print
                if (key == 'q') {
                    quitRequested = true;
                }
                break;
            }
        }
    }


    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move();
        }
    }

    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == player.getX() && enemy.getY() == player.getY()) {
                player.loseLife();
                if (player.getLives() == 0) {
                    gameOver = true;
                }
                respawnPlayer();
                break;
            }
        }
    }

    private void respawnPlayer() {
        ArrayList<Point> floorTiles = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.Floor && !isEnemyAt(x, y)) {
                    floorTiles.add(new Point(x, y));
                }
            }
        }
        if (!floorTiles.isEmpty()) {
            Point respawnPoint = floorTiles.get(rand.nextInt(floorTiles.size()));
            player.setPosition(respawnPoint.x, respawnPoint.y);
        }
    }

    private void checkLevelCompletion() {
        if (collectedCoins == totalCoins) {
            if (level < MAX_LEVEL) {
                level++;
                generateNewLevel();
            } else {
                gameCompleted = true;
            }
        }
    }

    private void generateNewLevel() {
        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                newWorld[x][y] = Tileset.Grass;
            }
        }
        Main.createWorld(newWorld, rand);
        this.world = newWorld;
        this.collectedCoins = 0;
        initializeWorld(player.getCharacterChoice());
        initializeEnemies();
    }

    private void displayGameCompletionScreen() {
        StdDraw.clear(Color.BLACK);
        String winScreenPath = "proj3/src/core/game assets/Win Screen.png";
        StdDraw.picture(WIDTH / 2.0, HEIGHT / 2.0, winScreenPath, WIDTH, HEIGHT);
        StdDraw.show();
        StdDraw.pause(5000);
    }

    private void displayGameOverScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(WIDTH / 2.0, HEIGHT / 2.0, LOSE_SCREEN, WIDTH, HEIGHT);
        StdDraw.show();
        StdDraw.pause(5000);
    }


    public void render() {
        StdDraw.clear(Color.BLACK);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (world[x][y] != null) {
                    world[x][y].draw(x, y);
                }
            }
        }
        player.render();
        for (Enemy enemy : enemies) {
            enemy.render();
        }
        renderUI();
        StdDraw.show();
    }

    private void renderUI() {
        StdDraw.picture(WIDTH - 5.6, HEIGHT - 1, "proj3/src/core/game assets/Coin.PNG", 2.0, 2.0);
        StdDraw.setFont(brickSansFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH - 3, HEIGHT - 1, collectedCoins + "/" + totalCoins);
        StdDraw.text(WIDTH / 2, HEIGHT - 1, "Level " + level);

        if (player.getLives() == 2) {
            StdDraw.picture(2, HEIGHT - 1, HEART_RED);
            StdDraw.picture(3, HEIGHT - 1, HEART_RED);
        } else if (player.getLives() == 1) {
            StdDraw.picture(2, HEIGHT - 1, HEART_RED);
            StdDraw.picture(3, HEIGHT - 1, HEART_GRAY);
        } else {
            StdDraw.picture(2, HEIGHT - 1, HEART_GRAY);
            StdDraw.picture(3, HEIGHT - 1, HEART_GRAY);
        }
    }

    public void incrementCollectedCoins() {
        collectedCoins++;
    }

    public boolean isEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y) {
                return true;
            }
        }
        return false;
    }

    // Getters for GameState
    public int getCollectedCoins() {
        return collectedCoins;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public int getLevel() {
        return level;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    private void saveGame() {
        GameState state = new GameState(world, player, enemies, this);
        String serializedState = state.serialize();
        FileUtils.writeFile(SAVE_FILE, serializedState);
    }

    public static Game loadGame() {
        if (FileUtils.fileExists(SAVE_FILE)) {
            String serializedState = FileUtils.readFile(SAVE_FILE);
            GameState state = GameState.deserialize(serializedState);
            return new Game(state);
        }
        return null;
    }
}
