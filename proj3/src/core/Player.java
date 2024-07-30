package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.image.BufferedImage;

public class Player {
    private static final int TILE_SIZE = 16;

    private BufferedImage currentSprite;
    private String spritePrefix;

    private int x, y;
    private Direction facing = Direction.DOWN;
    private TETile[][] world;
    private Game game;

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(int startX, int startY, TETile[][] world, Game game, int characterChoice) {
        this.x = startX;
        this.y = startY;
        this.world = world;
        this.game = game;

        spritePrefix = (characterChoice == 1) ? "Sprite" : "Guy";
        currentSprite = Sprite.loadSprite(spritePrefix + "Front.PNG");
    }

    public void render() {
        if (currentSprite == null) {
            System.err.println("Current sprite is null!");
            return;
        }
        StdDraw.picture(x + 0.5, y + 0.5, Sprite.getSpriteFilePath(spritePrefix, facing), 1, 1);
    }

    public void move(Direction dir) {
        int newX = x;
        int newY = y;

        facing = dir;

        switch (dir) {
            case UP:
                newY += 1;
                currentSprite = Sprite.loadSprite(spritePrefix + "Back.PNG");
                break;
            case DOWN:
                newY -= 1;
                currentSprite = Sprite.loadSprite(spritePrefix + "Front.PNG");
                break;
            case LEFT:
                newX -= 1;
                currentSprite = Sprite.loadSprite(spritePrefix + "Left.PNG");
                break;
            case RIGHT:
                newX += 1;
                currentSprite = Sprite.loadSprite(spritePrefix + "Right.PNG");
                break;
        }

        if (canMoveTo(newX, newY)) {
            if (world[newX][newY] == Tileset.FloorWithCoin) {
                collectCoin(newX, newY);
            }
            x = newX;
            y = newY;
        }
    }

    private void collectCoin(int newX, int newY) {
        world[newX][newY] = Tileset.Floor;
        game.incrementCollectedCoins();
    }

    private boolean canMoveTo(int newX, int newY) {
        return newX >= 0 && newX < world.length &&
                newY >= 0 && newY < world[0].length &&
                (world[newX][newY] == Tileset.Floor || world[newX][newY] == Tileset.FloorWithCoin);
    }

    public void interact() {
        int interactX = x;
        int interactY = y;

        switch (facing) {
            case UP:
                interactY++;
                break;
            case DOWN:
                interactY--;
                break;
            case LEFT:
                interactX--;
                break;
            case RIGHT:
                interactX++;
                break;
        }

        if (interactX >= 0 && interactX < world.length && interactY >= 0 && interactY < world[0].length) {
            System.out.println("Interacting with tile: " + world[interactX][interactY]);
        }
    }
}