package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;
    public static final int HUD_HEIGHT = 2;
    private static final int MAX_LEVEL = 2;
    private static final int[] COINS_PER_LEVEL = {101, 201};
    private static final String SAVE_FILE = "proj3/save_game.txt";

    private static final String HEART_RED = "proj3/src/core/game assets/HeartRed.PNG";
    private static final String HEART_GRAY = "proj3/src/core/game assets/HeartGray.PNG";
    private static final String LOSE_SCREEN = "proj3/src/core/game assets/You Lost Screen Resized.png";
    private static final String COIN_IMAGE = "proj3/src/core/game assets/Coin.PNG";
    private static final String HEALTH_POTION = "proj3/src/core/game assets/Health Potion.PNG";

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
    private int potionX;
    private int potionY;


    public Game(TETile[][] generatedWorld, int characterChoice, long seed) {
        this.world = generatedWorld;
        this.randomSeed = seed;
        this.rand = new Random(seed);
        initializeFont();
        initializeWorld(characterChoice);
        initializeEnemies();
        setupGraphics();
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
    }

    private void initializeFont() {
        try {
            brickSansFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("proj3/src/core/game assets/NTBrickSans.ttf")).deriveFont(13f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(brickSansFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    private void initializeWorld(int characterChoice) {
        placeCoins();
        placePlayer(characterChoice);
        placePotion();
    }

    private void placePotion() {
        while (true) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            if (world[x][y] == Tileset.Floor) {
                potionX = x;
                potionY = y;
                break;
            }
        }
    }
    public void collectPotion(int x, int y) {
        if (x == potionX && y == potionY) {
            potionX = -1;
            potionY = -1;
            player.gainLife();
        }
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
        List<int[]> floorTiles = new ArrayList<>();
        Random random = new Random();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.FloorWithCoin)) {
                    floorTiles.add(new int[]{x, y});
                }
            }
        }

        if (floorTiles.isEmpty()) {
            throw new RuntimeException("No floor tiles found for player placement");
        }

        int[] selectedTile = floorTiles.get(random.nextInt(floorTiles.size()));
        int x = selectedTile[0];
        int y = selectedTile[1];

        player = new Player(x, y, world, this, characterChoice);
        world[x][y] = Tileset.Floor;
        totalCoins--;
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
            if (world[x][y].equals(Tileset.Floor) && !isEnemyAt(x, y)) {
                enemies.add(new Enemy(x, y, world, player, this));
                return;
            }
            attempts++;
        }
        System.out.println("Warning: Could not place an enemy after 100 attempts");
    }

    private void setupGraphics() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, (HEIGHT + HUD_HEIGHT) * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT + HUD_HEIGHT);
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
            checkPotionCollection();
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

    private void checkPotionCollection() {
        int playerX = player.getX();
        int playerY = player.getY();
        if (playerX == potionX && playerY == potionY) {
            collectPotion(playerX, playerY);
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
                if (world[x][y].equals(Tileset.Floor) && !isEnemyAt(x, y)) {
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
        String winScreenPath = "proj3/src/core/game assets/Win Screen Resized.png"; // Use resized image
        StdDraw.picture(WIDTH / 2.0, (HEIGHT + HUD_HEIGHT) / 2.0, winScreenPath, WIDTH, HEIGHT + HUD_HEIGHT);
        StdDraw.show();
        StdDraw.pause(5000);
    }

    private void displayGameOverScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(WIDTH / 2.0, (HEIGHT + HUD_HEIGHT) / 2.0, LOSE_SCREEN, WIDTH, HEIGHT + HUD_HEIGHT);
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'r' || key == 'R') {
                    restartGame();
                    return;
                } else if (key == 'q' || key == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    private void restartGame() {
        MainMenu menu = new MainMenu(WIDTH, HEIGHT);
        menu.displayMenu();
        String input = menu.getInput();

        if (input.startsWith("N")) {
            String[] parts = input.split(":");
            long seed = Long.parseLong(parts[0].substring(1));
            int characterChoice = Integer.parseInt(parts[1]);

            TETile[][] world = new TETile[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    world[x][y] = Tileset.Grass;
                }
            }
            Random rand = new Random(seed);
            Main.createWorld(world, rand);
            Game game = new Game(world, characterChoice, seed);
            game.gameLoop();
        } else if (input.startsWith("L")) {
            Game loadedGame = Game.loadGame();
            if (loadedGame != null) {
                loadedGame.gameLoop();
            } else {
                System.out.println("No saved game found. Exiting.");
                System.exit(0);
            }
        } else if (input.startsWith("Q")) {
            System.exit(0);
        }
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
                    default:
                        System.out.println("Invalid key: " + key);
                        break;
                }
            }
        }
    }

    private void waitForQuitCommand() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (key == 'q') {
                    quitRequested = true;
                }
                break;
            }
        }
    }
    public void render() {
        StdDraw.clear(Color.BLACK);
        renderWorld();
        renderPlayerAndEnemies();
        renderPotion();
        if (!gameCompleted && !gameOver) {
            renderHUD();
        }
        StdDraw.show();
    }

    private void renderWorld() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (world[x][y] != null) {
                    world[x][y].draw(x, y);
                }
            }
        }
    }

    private void renderPlayerAndEnemies() {
        player.render();
        for (Enemy enemy : enemies) {
            enemy.render();
        }
    }

    private void renderPotion() {
        if (potionX >= 0 && potionY >= 0) {
            StdDraw.picture(potionX + 0.5, potionY + 0.5, HEALTH_POTION, 1, 1);
        }
    }


    private void renderHUD() {
        Color hudColor = Color.decode("#92D34E");
        StdDraw.setPenColor(hudColor);
        StdDraw.filledRectangle(WIDTH / 2.0, HEIGHT + HUD_HEIGHT / 2.0, WIDTH / 2.0, HUD_HEIGHT / 2.0);
        StdDraw.setFont(brickSansFont);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.picture(9.8, HEIGHT + 1.1, COIN_IMAGE, 1.5, 1.5);
        StdDraw.textRight(9, HEIGHT + 1, collectedCoins + "/" + totalCoins);
        StdDraw.text(WIDTH / 2, HEIGHT + 1, "Level " + level);

        if (player.getLives() == 3) {
            StdDraw.picture(1, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(2, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(3, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
        } else if (player.getLives() == 2) {
            StdDraw.picture(1, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(2, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(3, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
        } else if (player.getLives() == 4) {
            StdDraw.picture(1, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(2, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(3, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(4, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
        } else if (player.getLives() == 1) {
            StdDraw.picture(1, HEIGHT + HUD_HEIGHT - 1, HEART_RED);
            StdDraw.picture(2, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
            StdDraw.picture(3, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
        } else {
            StdDraw.picture(1, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
            StdDraw.picture(2, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
            StdDraw.picture(3, HEIGHT + HUD_HEIGHT - 1, HEART_GRAY);
        }

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            int x = (int) mouseX;
            int y = (int) mouseY;
            TETile tile = world[x][y];
            StdDraw.textRight(WIDTH - 1, HEIGHT + 1, "Tile: " + tile.description());
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

    public void simulateMovements(String movements) {
        for (char move : movements.toCharArray()) {
            if (move == 'Q') {
                quitRequested = true;
                break;
            }
            switch (move) {
                case 'W': player.move(Player.Direction.UP); break;
                case 'S': player.move(Player.Direction.DOWN); break;
                case 'A': player.move(Player.Direction.LEFT); break;
                case 'D': player.move(Player.Direction.RIGHT); break;
                default: break;
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void saveGame() {
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
