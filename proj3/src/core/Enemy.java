package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Enemy {
    private static final int TILE_SIZE = 16;
    private static final int MOVE_DELAY = 5;

    private BufferedImage spriteDown, spriteUp, spriteRight, spriteLeft;
    private BufferedImage currentSprite;

    private int x, y;
    private int moveCounter = 0;
    private Player player;
    private TETile[][] world;
    private Game game;

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(int startX, int startY, TETile[][] world, Player player, Game game) {
        this.x = startX;
        this.y = startY;
        this.world = world;
        this.player = player;
        this.game = game;

        spriteDown = Sprite.loadSprite("EnemyFront.PNG");
        spriteUp = Sprite.loadSprite("EnemyBack.PNG");
        spriteRight = Sprite.loadSprite("EnemyRight.PNG");
        spriteLeft = Sprite.loadSprite("EnemyLeft.PNG");

        currentSprite = spriteDown;
    }

    public void render() {
        String tempFilePath = "temp_enemy_sprite.png";
        try {
            javax.imageio.ImageIO.write(currentSprite, "png", new java.io.File(tempFilePath));
            StdDraw.picture(x + 0.5, y + 0.5, tempFilePath, 1, 1);
            new java.io.File(tempFilePath).delete();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void move() {
        if (moveCounter >= MOVE_DELAY) {
            moveCounter = 0;
            List<Direction> path = findPathToPlayer();
            if (path != null && !path.isEmpty()) {
                Direction dir = path.get(0);
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
                }
            }
        } else {
            moveCounter++;
        }
    }

    private List<Direction> findPathToPlayer() {
        int targetX = player.getX();
        int targetY = player.getY();
        boolean[][] visited = new boolean[world.length][world[0].length];
        Map<Point, Point> cameFrom = new HashMap<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        visited[x][y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(cameFrom, new Point(x, y), current);
            }

            for (Direction dir : Direction.values()) {
                int newX = current.x;
                int newY = current.y;
                switch (dir) {
                    case UP:
                        newY += 1;
                        break;
                    case DOWN:
                        newY -= 1;
                        break;
                    case LEFT:
                        newX -= 1;
                        break;
                    case RIGHT:
                        newX += 1;
                        break;
                }

                if (canMoveTo(newX, newY) && !visited[newX][newY]) {
                    queue.add(new Point(newX, newY));
                    visited[newX][newY] = true;
                    cameFrom.put(new Point(newX, newY), current);
                }
            }
        }
        return null;
    }

    private List<Direction> reconstructPath(Map<Point, Point> cameFrom, Point start, Point end) {
        List<Direction> path = new LinkedList<>();
        Point current = end;

        while (!current.equals(start)) {
            Point prev = cameFrom.get(current);
            if (prev != null) {
                if (current.x == prev.x && current.y == prev.y + 1) {
                    path.add(0, Direction.UP);
                } else if (current.x == prev.x && current.y == prev.y - 1) {
                    path.add(0, Direction.DOWN);
                } else if (current.x == prev.x + 1 && current.y == prev.y) {
                    path.add(0, Direction.RIGHT);
                } else if (current.x == prev.x - 1 && current.y == prev.y) {
                    path.add(0, Direction.LEFT);
                }
                current = prev;
            } else {
                break;
            }
        }
        return path;
    }

    private boolean canMoveTo(int newX, int newY) {
        return newX >= 0 && newX < world.length &&
                newY >= 0 && newY < world[0].length &&
                (world[newX][newY] == Tileset.Floor || world[newX][newY] == Tileset.FloorWithCoin) &&
                !game.isEnemyAt(newX, newY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
