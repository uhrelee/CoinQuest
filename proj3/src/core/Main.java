package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

//
public class Main {

    public static final int WIDTH = 65;
    public static final int HEIGHT = 55;
    public long SEED;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long SEED = Long.parseLong(args[0]);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        createWorld(world);
        ter.renderFrame(world);
    }
    private static void createWorld(TETile[][] world) {

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
