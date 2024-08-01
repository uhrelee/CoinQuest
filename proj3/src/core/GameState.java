package core;

import tileengine.TETile;
import java.io.*;
import java.awt.*;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private TETile[][] world;
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
    }

    public TETile[][] getWorld() {
        return world;
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

    public void save(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState load(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}