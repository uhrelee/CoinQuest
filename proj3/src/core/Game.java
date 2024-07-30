package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.Random;

public class Game {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    private static final int TILE_SIZE = 16;

    private Player player;
    private TETile[][] world;
    private int collectedCoins = 0;
    private int totalCoins = 0;
    private int level = 1;
    private Font brickSansFont;
    private static final int MAX_LEVEL = 3;
    private boolean gameCompleted = false;

    public Game(TETile[][] generatedWorld, int characterChoice) {
        this.world = generatedWorld;

        try {
            brickSansFont = Font.createFont(Font.TRUETYPE_FONT, new File("proj3/src/core/game assets/NTBrickSans.ttf")).deriveFont(13f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(brickSansFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
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
                    player = new Player(x, y, world, this, characterChoice);
                    world[x][y] = Tileset.Floor;
                    totalCoins--;
                    break;
                }
            }
            if (player != null) break;
        }

        if (player == null) {
            throw new RuntimeException("No floor tiles found for player placement");
        }

        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    public void gameLoop() {
        while (!gameCompleted) {
            handleInput();
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
        Random rand = new Random();
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

        // Place player on a new floor tile
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FloorWithCoin) {
                    player.setPosition(x, y, world);  // Pass the new world to the player
                    world[x][y] = Tileset.Floor;
                    totalCoins--;
                    return;
                }
            }
        }
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

        double coinIconWidth = 2.0;
        double coinIconHeight = 2.0;
        StdDraw.picture(WIDTH - 5.6, HEIGHT - 1, "proj3/src/core/game assets/Coin.PNG", coinIconWidth, coinIconHeight);

        StdDraw.setFont(brickSansFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH - 3, HEIGHT - 1, collectedCoins + "/" + totalCoins);
        StdDraw.text(WIDTH / 2, HEIGHT - 1, "Level " + level);

        StdDraw.picture(2, HEIGHT - 1, "proj3/src/core/game assets/HeartRed.PNG");

        StdDraw.show();
    }


    public void incrementCollectedCoins() {
        collectedCoins++;
    }
}