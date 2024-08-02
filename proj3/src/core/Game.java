package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Game {
    private static final File CWD = new File(".");
    private static final File SAVE_DIR = new File(CWD, "saves");
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;
    private boolean isColonPressed = false;


    private Player player;
    private ArrayList<Enemy> enemies;
    private TETile[][] world;
    private int collectedCoins = 0;
    private int totalCoins = 0;
    private int level = 1;
    private Font brickSansFont;
    private static final int MAX_LEVEL = 3;
    private boolean gameCompleted = false;
    private long randomSeed;

    public Game(TETile[][] generatedWorld, int characterChoice, long seed) {
        this.world = generatedWorld;
        this.randomSeed = seed;
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
        initializeFont();
        initializeWorld(gameState.getCharacterChoice());
        initializeEnemies();
        setupGraphics();
    }

    private void initializeFont() {
        brickSansFont = FontManager.getBrickSansFont(13f);
    }

    private void initializeWorld(int characterChoice) {
        if (world == null) {
            world = new TETile[WIDTH][HEIGHT];
        }

        boolean floorWithCoinFound = false;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FloorWithCoin) {
                    floorWithCoinFound = true;
                    totalCoins++;
                }
            }
        }
        if (!floorWithCoinFound) {
            throw new RuntimeException("No floor tiles found for player placement");
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
        if (enemies == null) {
            enemies = new ArrayList<>();
        } else {
            enemies.clear();
        }

        for (int i = 2; i < 5; i++) {
            int enemyX = (int) (Math.random() * WIDTH);
            int enemyY = (int) (Math.random() * HEIGHT);
            if (world[enemyX][enemyY] == Tileset.FloorWithCoin) {
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
        Random rand = new Random(randomSeed);
        Main.createWorld(newWorld, rand);

        this.world = newWorld;
        this.collectedCoins = 0;
        this.totalCoins = 0;

        initializeWorld(player.getCharacterChoice());

        initializeEnemies();
    }

    private void displayGameCompletionScreen() {
        StdDraw.clear(Color.BLACK);
        String winScreenPath = "proj3/src/core/game assets/Win Screen.png";
        StdDraw.picture(WIDTH / 2.0, HEIGHT / 2.0, winScreenPath, WIDTH, HEIGHT);
        StdDraw.show();
    }

    public void handleInput() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();

            if (isColonPressed) {
                if (key == 'Q' || key == 'q') {
                    quitAndSave();  // Call the function to quit and save
                }
                // Reset colon state if any other key is pressed
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
                    default:
                        System.out.println("Unhandled Key: " + key);
                        break;
                }
            }
        }
    }

    private void quitAndSave() {
        saveGameState("gameState.txt");  // Save the game state with a default filename
        System.exit(0);  // Exit the program
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

        double coinIconWidth = 2.0;
        double coinIconHeight = 2.0;
        StdDraw.picture(WIDTH - 5.6, HEIGHT - 1, "proj3/src/core/game assets/Coin.PNG", coinIconWidth, coinIconHeight);

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
        GameState gameState = new GameState(world, player.getCharacterChoice(), randomSeed, collectedCoins, totalCoins, level, gameCompleted);
        gameState.save(filePath.toString());
    }

    public void loadGameState(String fileName) {
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }

        Path filePath = SAVE_DIR.toPath().resolve(fileName);
        GameState gameState = GameState.load(filePath.toString());

        if (gameState != null) {
            // Use the new constructor to initialize the game with the loaded state
            this.world = gameState.getWorld();
            this.randomSeed = gameState.getRandomSeed();
            this.collectedCoins = gameState.getCollectedCoins();
            this.totalCoins = gameState.getTotalCoins();
            this.level = gameState.getLevel();
            this.gameCompleted = gameState.isGameCompleted();
            initializeFont();
            initializeWorld(gameState.getCharacterChoice());
            initializeEnemies();
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