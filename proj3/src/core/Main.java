package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Main {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;

    public static void main(String[] args) {
        MainMenu menu = new MainMenu(WIDTH, HEIGHT);
        menu.displayMenu();
        String input = menu.getInput();
        if (input.startsWith("N")) {
            String[] parts = input.split(":");
            long seed = Long.parseLong(parts[0].substring(1));
            int characterChoice = Integer.parseInt(parts[1]);

            TETile[][] world = new TETile[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x += 1) {
                for (int y = 0; y < HEIGHT; y += 1) {
                    world[x][y] = Tileset.Grass;
                }
            }
            Random rand = new Random(seed);
            createWorld(world, rand);
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

    public static void createWorld(TETile[][] world, Random rand) {
        Rooms rooms = new Rooms(0, 0, 0, 0, rand);
        rooms.fillWithSeveralRooms(world);
        rooms.connectRooms(world);
        rooms.buildWalls(world);
        rooms.handleEdgeCases(world);

        while (emptySpaceProportion(world) > 0.5) {
            rooms.fillWithSeveralRooms(world);
            rooms.connectRooms(world);
            rooms.buildWalls(world);
            rooms.handleEdgeCases(world);
        }
    }

    public static double emptySpaceProportion(TETile[][] world) {
        int count = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.Grass) {
                    count++;
                }
            }
        }
        int area = HEIGHT * WIDTH;
        return (double) count / area;
    }
}