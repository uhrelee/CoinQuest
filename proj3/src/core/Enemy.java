package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int TILE_SIZE = 16;
    private static final int MOVE_DELAY = 5;

    private String spritePrefix = "Enemy"; // Default prefix for enemy sprites
    private int x, y;
    private int moveCounter = 0;
    private Direction facing = Direction.DOWN;
    private Player player;
    private TETile[][] world;
    private Game game;

    private static class Node {
        Point point;
        int gCost;
        int hCost;

        Node(Point point, int gCost, int hCost) {
            this.point = point;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        int fCost() {
            return gCost + hCost;
        }
    }

    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Enemy(int startX, int startY, TETile[][] world, Player player, Game game) {
        this.x = startX;
        this.y = startY;
        this.world = world;
        this.player = player;
        this.game = game;
    }

    public void render() {
        StdDraw.picture(x + 0.5, y + 0.5, Sprite.getEnemyFilePath(spritePrefix, facing), 1, 1);
    }

    public void move() {
        if (moveCounter >= MOVE_DELAY) {
            moveCounter = 0;
            List<Direction> path = findPathToPlayer();
            if (path != null && !path.isEmpty()) {
                Direction dir = path.get(0);
                int newX = x;
                int newY = y;

                facing = dir; // Update facing direction

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
                    default:
                        System.out.println("Invalid direction: " + dir);
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
        Point start = new Point(x, y);
        Point target = new Point(player.getX(), player.getY());

        PriorityQueue<Node> openSet =
                new PriorityQueue<>(Comparator.comparingInt(node -> node.fCost()));

        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();

        gScore.put(start, 0);
        openSet.add(new Node(start, 0, heuristic(start, target)));

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            Point current = currentNode.point;

            // Reached player
            if (current.equals(target)) {
                return reconstructPath(cameFrom, start, current);
            }

            for (Direction dir : Direction.values()) {
                int newX = current.x;
                int newY = current.y;

                switch (dir) {
                    case UP: newY += 1; break;
                    case DOWN: newY -= 1; break;
                    case LEFT: newX -= 1; break;
                    case RIGHT: newX += 1; break;
                }

                if (!canMoveTo(newX, newY)) continue;

                Point neighbor = new Point(newX, newY);
                int tentativeG = gScore.get(current) + 1;

                if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);

                    int h = heuristic(neighbor, target);
                    openSet.add(new Node(neighbor, tentativeG, h));
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
        return newX >= 0 && newX < world.length
                && newY >= 0 && newY < world[0].length
                && (world[newX][newY].equals(Tileset.Floor) || world[newX][newY].equals(Tileset.FloorWithCoin))
                && !game.isEnemyAt(newX, newY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
