package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.io.*;
import java.awt.*;

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

    public GameState(TETile[][] world, int characterChoice, long randomSeed, int collectedCoins, int totalCoins, int level, boolean gameCompleted) {
        this.world = world;
        this.characterChoice = characterChoice;
        this.randomSeed = randomSeed;
        this.collectedCoins = collectedCoins;
        this.totalCoins = totalCoins;
        this.level = level;
        this.gameCompleted = gameCompleted;
        this.worldTileIds = convertWorldToTileIds(world);
    }

    // Convert the world to a 2D array of tile IDs
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

    // Convert tile IDs back to TETile objects
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

    // Get the ID for a given tile
    private int getTileId(TETile tile) {
        if (tile.equals(Tileset.AVATAR)) return 0;
        if (tile.equals(Tileset.WALL)) return 1;
        if (tile.equals(Tileset.FLOOR)) return 2;
        // Add more mappings as needed
        return -1; // Default to -1 if no match
    }

    // Get the TETile for a given ID
    private TETile getTileFromId(int id) {
        switch (id) {
            case 0: return Tileset.AVATAR;
            case 1: return Tileset.WALL;
            case 2: return Tileset.FLOOR;
            // Add more mappings as needed
            default: return Tileset.NOTHING; // Default to NOTHING if ID is invalid
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
        out.writeObject(worldTileIds); // Serialize the tile IDs
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        worldTileIds = (int[][]) in.readObject(); // Deserialize the tile IDs
        world = convertTileIdsToWorld(worldTileIds); // Rebuild the world from IDs
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
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}