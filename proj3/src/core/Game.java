package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.nio.file.Path;

public class Game {
    private static final File CWD = new File(".");
    private static final File SAVE_DIR = new File(CWD, "saves");
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;
    private static final int MAX_LEVEL = 3;
    private boolean isColonPressed = false;

    private Player player;
    private List<Enemy> enemies;
    private TETile[][] world;
    private int collectedCoins = 0;
    private int totalCoins = 0;
    private int level = 1;
    private Font brickSansFont;
    private boolean gameCompleted = false;
    private long randomSeed;

    public Game(TETile[][] generatedWorld, int characterChoice, long seed) {
        this.world = generatedWorld;
        this.randomSeed = seed;
        this.enemies = new ArrayList<>();
        initializeFont();
        initializeWorld(characterChoice);
        initializeEnemies();
        setupGraphics();
    }

    public Game(GameState gameState) {
        this.world = gameState.getWorld();
        this.randomSeed = gameState.getRandomSeed();
        this.collectedCoins = gameState.getCollectedCoins();
        this.totalCoins = gameState.getTotalCoins();
        this.level = gameState.getLevel();
        this.gameCompleted = gameState.isGameCompleted();
        this.enemies = new ArrayList<>();
        initializeFont();
        initializeWorld(gameState.getCharacterChoice());
        initializeEnemies(gameState.getEnemyPositions());
        setupGraphics();
    }

    private void initializeFont() {
        brickSansFont = FontManager.getBrickSansFont(13f);
    }

    private void initializeWorld(int characterChoice) {
        if (world == null) {
            world = new TETile[WIDTH][HEIGHT];
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.Floor) {
                    world[x][y] = Tileset.FloorWithCoin;
                    totalCoins++;
                }
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FloorWithCoin) {
                    if (player == null) {
                        player = new Player(x, y, world, this, characterChoice);
                        world[x][y] = Tileset.Floor;
                        totalCoins--;
                        break;
                    }
                }
            }
            if (player != null) break;
        }

        if (player == null) {
            throw new RuntimeException("No floor tiles found for player placement");
        }
    }

    private void initializeEnemies() {
        initializeEnemies(null);
    }

    private void initializeEnemies(List<int[]> enemyPositions) {
        enemies.clear();
        if (enemyPositions != null && !enemyPositions.isEmpty()) {
            for (int[] pos : enemyPositions) {
                enemies.add(new Enemy(pos[0], pos[1], world, player));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                int enemyX, enemyY;
                do {
                    enemyX = (int) (Math.random() * WIDTH);
                    enemyY = (int) (Math.random() * HEIGHT);
                } while (world[enemyX][enemyY] != Tileset.Floor && world[enemyX][enemyY] != Tileset.FloorWithCoin);
                enemies.add(new Enemy(enemyX, enemyY, world, player));
            }
        }
    }

    private void setupGraphics() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    public void gameLoop() {
        while (!gameCompleted) {
            handleInput();
            for (Enemy enemy : enemies) {
                enemy.move();
            }
            render();
            checkLevelCompletion();
            StdDraw.pause(50);
        }
        displayGameCompletionScreen();
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
        Random rand = new Random(randomSeed + level);
        Main.createWorld(newWorld, rand);

        this.world = newWorld;
        this.collectedCoins = 0;
        this.totalCoins = 0;

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.Floor) {
                    world[x][y] = Tileset.FloorWithCoin;
                    totalCoins++;
                }
            }
        }

        placePlayerAndEnemies();
    }

    private void placePlayerAndEnemies() {
        boolean playerPlaced = false;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FloorWithCoin && !playerPlaced) {
                    player.setPosition(x, y, world);
                    world[x][y] = Tileset.Floor;
                    totalCoins--;
                    playerPlaced = true;
                    break;
                }
            }
            if (playerPlaced) break;
        }

        initializeEnemies();
    }

    private void displayGameCompletionScreen() {
        StdDraw.clear(Color.BLACK);
        String winScreenPath = "proj3/src/core/game assets/Win Screen.png";
        StdDraw.picture(WIDTH / 2.0, HEIGHT / 2.0, winScreenPath, WIDTH, HEIGHT);
        StdDraw.show();
        StdDraw.pause(5000);
    }

    public void handleInput() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();

            if (isColonPressed) {
                if (key == 'Q' || key == 'q') {
                    quitAndSave();
                }
                isColonPressed = false;
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
                    case ':':
                        isColonPressed = true;
                        break;
                }
            }
        }
    }

    private void quitAndSave() {
        saveGameState("gameState.txt");
        System.exit(0);
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

        StdDraw.picture(WIDTH - 5.6, HEIGHT - 1, "proj3/src/core/game assets/Coin.PNG", 2.0, 2.0);

        StdDraw.setFont(brickSansFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(2, HEIGHT - 1, "Coins: " + collectedCoins + "/" + totalCoins);
        StdDraw.text(2, HEIGHT - 2, "Level: " + level);
        StdDraw.show();
    }

    public void saveGameState(String fileName) {
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }

        Path filePath = SAVE_DIR.toPath().resolve(fileName);
        List<int[]> enemyPositions = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemyPositions.add(new int[]{enemy.getX(), enemy.getY()});
        }
        GameState gameState = new GameState(world, player.getCharacterChoice(), randomSeed, collectedCoins, totalCoins, level, gameCompleted, enemyPositions);
        gameState.save(filePath.toString());
    }

    public void loadGameState(String fileName) {
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }

        Path filePath = SAVE_DIR.toPath().resolve(fileName);
        GameState gameState = GameState.load(filePath.toString());

        if (gameState != null) {
            this.world = gameState.getWorld();
            this.randomSeed = gameState.getRandomSeed();
            this.collectedCoins = gameState.getCollectedCoins();
            this.totalCoins = gameState.getTotalCoins();
            this.level = gameState.getLevel();
            this.gameCompleted = gameState.isGameCompleted();
            initializeFont();
            initializeWorld(gameState.getCharacterChoice());
            initializeEnemies(gameState.getEnemyPositions());
            setupGraphics();
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void incrementCollectedCoins() {
        collectedCoins++;
    }
}