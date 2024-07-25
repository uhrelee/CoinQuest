package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

//
public class Main {

    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    public static Random rand;

    public static void main(String[] args) {
        MainMenu menu = new MainMenu(WIDTH, HEIGHT);
        menu.DisplayMenu();
        String input = menu.getInput();
        if (input.startsWith("N")) {
            long SEED = Long.parseLong(input.substring(1, input.length() - 1));
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            for (int x = 0; x < WIDTH; x += 1) {
                for (int y = 0; y < HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
            rand = new Random(SEED);
            Rooms.rand = rand;
            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);
            createWorld(world);
            ter.renderFrame(world);
        } else if (input.startsWith("L")){
            // LOAD GAME HERE
        } else if (input.startsWith("Q")) {
            // QUIT THE GAME
            System.exit(0);
        }
    }

    public static void createWorld(TETile[][] world) {

        Rooms.fillWithSeveralRooms(world);
        Rooms.connectRooms(world);
        Rooms.buildWalls(world);

        while (emptySpaceProportion(world) > 0.5) {
            Rooms.fillWithSeveralRooms(world);
            Rooms.connectRooms(world);
            Rooms.buildWalls(world);
        }
    }

    public static double emptySpaceProportion(TETile[][] world) {
        int count = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.NOTHING) {
                    count++;
                }
            }
        }
        int area = HEIGHT * WIDTH;
        return (double) count / area;
    }
}
