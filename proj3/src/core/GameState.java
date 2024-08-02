package core;

import tileengine.SerializableTETile;
import tileengine.TETile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private SerializableTETile[][] world;
    private int playerX, playerY;
    private int characterChoice;
    private int lives;
    private int collectedCoins;
    private int totalCoins;
    private int level;
    private long randomSeed;
    private ArrayList<int[]> enemyPositions;

    public GameState(TETile[][] world, Player player, ArrayList<Enemy> enemies, Game game) {
        this.world = new SerializableTETile[world.length][world[0].length];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                this.world[x][y] = new SerializableTETile(world[x][y]);
            }
        }
        this.playerX = player.getX();
        this.playerY = player.getY();
        this.characterChoice = player.getCharacterChoice();
        this.lives = player.getLives();
        this.collectedCoins = game.getCollectedCoins();
        this.totalCoins = game.getTotalCoins();
        this.level = game.getLevel();
        this.randomSeed = game.getRandomSeed();
        this.enemyPositions = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemyPositions.add(new int[]{enemy.getX(), enemy.getY()});
        }
    }

    public TETile[][] getWorld() {
        TETile[][] originalWorld = new TETile[world.length][world[0].length];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                originalWorld[x][y] = world[x][y].toTETile();
            }
        }
        return originalWorld;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getCharacterChoice() {
        return characterChoice;
    }

    public int getLives() {
        return lives;
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

    public ArrayList<int[]> getEnemyPositions() {
        return enemyPositions;
    }

    public String serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize GameState", e);
        }
    }

    public static GameState deserialize(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            GameState state = (GameState) ois.readObject();
            ois.close();
            return state;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize GameState", e);
        }
    }
}