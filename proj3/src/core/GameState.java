package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.io.*;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient TETile[][] world; // Mark as transient to handle custom serialization
    private transient int[][] worldTileIds; // This will store the tile IDs
    private int characterChoice;
    private long randomSeed;
    private int collectedCoins;
    private int totalCoins;
    private int level;
    private boolean gameCompleted;
    private List<int[]> enemyPositions;


    public GameState(TETile[][] world, int characterChoice, long randomSeed, int collectedCoins, int totalCoins, int level, boolean gameCompleted, List<int[]> enemyPositions) {
        this.world = world;
        this.characterChoice = characterChoice;
        this.randomSeed = randomSeed;
        this.collectedCoins = collectedCoins;
        this.totalCoins = totalCoins;
        this.level = level;
        this.gameCompleted = gameCompleted;
        this.worldTileIds = convertWorldToTileIds(world);
        this.enemyPositions = enemyPositions;
    }

    private int[][] convertWorldToTileIds(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        int[][] tileIds = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tileIds[x][y] = getTileId(world[x][y]);
            }
        }
        return tileIds;
    }

    public List<int[]> getEnemyPositions() {
        return enemyPositions;
    }

    private TETile[][] convertTileIdsToWorld(int[][] tileIds) {
        int width = tileIds.length;
        int height = tileIds[0].length;
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = getTileFromId(tileIds[x][y]);
            }
        }
        return world;
    }

    private int getTileId(TETile tile) {
        if (tile.equals(Tileset.AVATAR)) return 0;
        if (tile.equals(Tileset.WALL)) return 1;
        if (tile.equals(Tileset.FLOOR)) return 2;
        if (tile.equals(Tileset.NOTHING)) return 3;
        if (tile.equals(Tileset.GRASS)) return 4;
        if (tile.equals(Tileset.WATER)) return 5;
        if (tile.equals(Tileset.FLOWER)) return 6;
        if (tile.equals(Tileset.LOCKED_DOOR)) return 7;
        if (tile.equals(Tileset.UNLOCKED_DOOR)) return 8;
        if (tile.equals(Tileset.SAND)) return 9;
        if (tile.equals(Tileset.MOUNTAIN)) return 10;
        if (tile.equals(Tileset.TREE)) return 11;
        if (tile.equals(Tileset.CELL)) return 12;
        if (tile.equals(Tileset.CustomNothing)) return 15;
        if (tile.equals(Tileset.CustomFloor)) return 13;
        if (tile.equals(Tileset.CustomWall)) return 14;
        if (tile.equals(Tileset.CustomTree)) return 15;
        if (tile.equals(Tileset.CustomBorder)) return 15;
        if (tile.equals(Tileset.CustomFlower)) return 15;
        if (tile.equals(Tileset.Grass)) return 15;
        if (tile.equals(Tileset.Floor)) return 13;
        if (tile.equals(Tileset.LeftWall)) return 14;
        if (tile.equals(Tileset.RightWall)) return 14;
        if (tile.equals(Tileset.TopRightCorner)) return 14;
        if (tile.equals(Tileset.TopLeftCorner)) return 14;
        if (tile.equals(Tileset.BottomLeftCorner)) return 14;
        if (tile.equals(Tileset.BottomRightCorner)) return 14;
        if (tile.equals(Tileset.TopWall)) return 14;
        if (tile.equals(Tileset.BottomWall)) return 14;
        if (tile.equals(Tileset.LeftInnerCorner)) return 14;
        if (tile.equals(Tileset.LeftTopInnerCorner)) return 14;
        if (tile.equals(Tileset.RightInnerCorner)) return 14;
        if (tile.equals(Tileset.RightTopInnerCorner)) return 14;
        if (tile.equals(Tileset.SingleWall)) return 14;
        if (tile.equals(Tileset.SingleWallLeft)) return 14;
        if (tile.equals(Tileset.SingleWallMiddle)) return 14;
        if (tile.equals(Tileset.SingleWallRight)) return 14;
        if (tile.equals(Tileset.SingleWallVertical)) return 14;
        if (tile.equals(Tileset.DoubleWallTop)) return 14;
        if (tile.equals(Tileset.DoubleWallBottom)) return 14;
        if (tile.equals(Tileset.WallAndTopLeftCorner)) return 14;
        if (tile.equals(Tileset.WallAndTopRightCorner)) return 14;
        if (tile.equals(Tileset.BottomWallAndLeftCorner)) return 14;
        if (tile.equals(Tileset.BottomWallAndRightCorner)) return 14;
        if (tile.equals(Tileset.WallAndLeftBottomCorner)) return 14;
        if (tile.equals(Tileset.WallAndRightBottomCorner)) return 14;
        if (tile.equals(Tileset.FloorWithCoin)) return 13;
        return -1;
    }

    private TETile getTileFromId(int id) {
        switch (id) {
            case 0: return Tileset.AVATAR;
            case 1: return Tileset.WALL;
            case 2: return Tileset.FLOOR;
            case 3: return Tileset.NOTHING;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.WATER;
            case 6: return Tileset.FLOWER;
            case 7: return Tileset.LOCKED_DOOR;
            case 8: return Tileset.UNLOCKED_DOOR;
            case 9: return Tileset.SAND;
            case 10: return Tileset.MOUNTAIN;
            case 11: return Tileset.TREE;
            case 12: return Tileset.CELL;
            case 13: return Tileset.CustomFloor;
            case 14: return Tileset.CustomWall;
            case 15: return Tileset.CustomNothing;
            case 16: return Tileset.CustomTree;
            case 17: return Tileset.CustomBorder;
            case 18: return Tileset.CustomFlower;
            case 19: return Tileset.Grass;
            case 20: return Tileset.Floor;
            case 21: return Tileset.LeftWall;
            case 22: return Tileset.RightWall;
            case 23: return Tileset.TopRightCorner;
            case 24: return Tileset.TopLeftCorner;
            case 25: return Tileset.BottomLeftCorner;
            case 26: return Tileset.BottomRightCorner;
            case 27: return Tileset.TopWall;
            case 28: return Tileset.BottomWall;
            case 29: return Tileset.LeftInnerCorner;
            case 30: return Tileset.LeftTopInnerCorner;
            case 31: return Tileset.RightInnerCorner;
            case 32: return Tileset.RightTopInnerCorner;
            case 33: return Tileset.SingleWall;
            case 34: return Tileset.SingleWallLeft;
            case 35: return Tileset.SingleWallMiddle;
            case 36: return Tileset.SingleWallRight;
            case 37: return Tileset.SingleWallVertical;
            case 38: return Tileset.DoubleWallTop;
            case 39: return Tileset.DoubleWallBottom;
            case 40: return Tileset.WallAndTopLeftCorner;
            case 41: return Tileset.WallAndTopRightCorner;
            case 42: return Tileset.BottomWallAndLeftCorner;
            case 43: return Tileset.BottomWallAndRightCorner;
            case 44: return Tileset.WallAndLeftBottomCorner;
            case 45: return Tileset.WallAndRightBottomCorner;
            case 46: return Tileset.FloorWithCoin;
            default: return Tileset.NOTHING;
        }
    }

    public TETile[][] getWorld() {
        return convertTileIdsToWorld(worldTileIds);
    }

    public int getCharacterChoice() {
        return characterChoice;
    }

    public long getRandomSeed() {
        return randomSeed;
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

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(convertWorldToTileIds(world));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.worldTileIds = (int[][]) in.readObject();
        this.world = convertTileIdsToWorld(this.worldTileIds);
    }

    public void save(String fileName) {
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState load(String fileName) {
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }
        GameState gameState = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            gameState = (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return gameState;
    }
}