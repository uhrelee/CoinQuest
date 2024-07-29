package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.image.BufferedImage;

public class Player {
    private static final int TILE_SIZE = 16;

    private BufferedImage spriteDown, spriteUp, spriteRight, spriteLeft;
    private BufferedImage currentSprite;

    private int x, y;
    private Direction facing = Direction.DOWN;
    private TETile[][] world;

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(int startX, int startY, TETile[][] world) {
        this.x = startX;
        this.y = startY;
        this.world = world;

        spriteDown = Sprite.getSprite(0, 0);
        spriteUp = Sprite.getSprite(1, 0);
        spriteRight = Sprite.getSprite(2, 0);
        spriteLeft = Sprite.getSprite(3, 0);

        currentSprite = spriteDown;
        world[x][y] = Tileset.Floor;
    }

    public void render() {
        String tempFilePath = "temp_player_sprite.png";
        try {
            javax.imageio.ImageIO.write(currentSprite, "png", new java.io.File(tempFilePath));
            StdDraw.picture(x + 0.5, y + 0.5, tempFilePath, 1, 1);
            new java.io.File(tempFilePath).delete();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void move(Direction dir) {
        int newX = x;
        int newY = y;

        switch (dir) {
            case UP:
                newY += 1;
                currentSprite = spriteUp;
                break;
            case DOWN:
                newY -= 1;
                currentSprite = spriteDown;
                break;
            case LEFT:
                newX -= 1;
                currentSprite = spriteLeft;
                break;
            case RIGHT:
                newX += 1;
                currentSprite = spriteRight;
                break;
        }

        if (canMoveTo(newX, newY)) {
            x = newX;
            y = newY;

            if (world[x][y] == Tileset.FloorWithCoin) {
                world[x][y] = Tileset.Floor;
            }
            facing = dir;
        }
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

