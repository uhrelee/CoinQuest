package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;
    private static final int MAX_LEVEL = 2;
    private static final int[] COINS_PER_LEVEL = {101, 201};
    private static final String HEART_RED = "proj3/src/core/game assets/HeartRed.PNG";
    private static final String HEART_GRAY = "proj3/src/core/game assets/HeartGray.PNG";
    private static final String LOSE_SCREEN = "proj3/src/core/game assets/You Lost Screen.png";

    private boolean gameOver = false;

    private Player player;
    private ArrayList<Enemy> enemies;
    private TETile[][] world;
    private int collectedCoins = 0;
    private int totalCoins = 0;
    private int level = 1;
    private Font brickSansFont;
    private boolean gameCompleted = false;
    private Random rand;

    public Game(TETile[][] generatedWorld, int characterChoice, long seed) {
        this.world = generatedWorld;
        this.rand = new Random(seed);
        initializeFont();
        initializeWorld(characterChoice);
        initializeEnemies();
        setupGraphics();
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

    public boolean isEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void setupGraphics() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    public void gameLoop() {
        while (!gameCompleted && !gameOver) {
            handleInput();
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
                // Respawn player at a random floor tile
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
            player.setPosition(respawnPoint.x, respawnPoint.y, world);
        }
    }

    private void displayGameOverScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(WIDTH / 2.0, HEIGHT / 2.0, LOSE_SCREEN, WIDTH, HEIGHT);
        StdDraw.show();
        StdDraw.pause(5000);
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
    }

    public void handleInput() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
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
}