package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rooms {
    int CenterX;
    int CenterY;
    int height;
    int width;
    private static final List<Rooms> rooms = new ArrayList<>();



    public Rooms(int centerX, int CenterY, int height, int width) {
        this.CenterX = centerX;
        this.CenterY = CenterY;
        this.height = height;
        this.width = width;
    }

    public int getCenterX() {
        return this.CenterX;
    }
    public int getCenterY() {
        return this.CenterY;
    }
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }
    public static int generateRoomNumber() {
        List<Integer> list = new ArrayList<>();
        for (int i = 5; i <= 10; i++) {
            list.add(i);
        }
        int randomElement = list.get(Main.rand.nextInt(list.size()));
        System.out.println("Number of rooms" + randomElement);
        return randomElement;
    }


    public static void fillWithRandomTiles(TETile[][] tiles) {
        int height = randomTileHeight();
        int width = randomTileWidth();
        int CenterX = chooseRoomCenterX();
        int CenterY = chooseRoomCenterY();
        if (tiles[CenterX][CenterY] != Tileset.FLOWER) {
            for (int x = CenterX - width / 2; x < CenterX + width / 2; x += 1) {
                for (int y = CenterY - height / 2; y < CenterY + height / 2; y += 1) {
                    tiles[x][y] = Tileset.FLOWER;
                    rooms.add(new Rooms(CenterX, CenterY, height, width));
                }
            }
        } else {
            CenterX = chooseRoomCenterX();
            CenterY = chooseRoomCenterY();
            fillWithRandomTiles(tiles);
        }
    }

    public static void fillWithSeveralRooms(TETile[][] tiles) {
        int numberOfRooms = generateRoomNumber();
        for (int i = 0; i < numberOfRooms; i++) {
            fillWithRandomTiles(tiles);
        }
    }

    private static int randomTileHeight() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7,  8, 9, 10);
        int randomElement = list.get(Main.rand.nextInt(list.size()));
        System.out.println(randomElement);
        return randomElement;
    }

    private static int randomTileWidth() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7,  8, 9, 10);
        int randomElement = list.get(Main.rand.nextInt(list.size()));
        System.out.println(randomElement);
        return randomElement;
    }

    private static int chooseRoomCenterX() {
        List<Integer> list = new ArrayList<>();
        int end = Main.WIDTH;
        int width = randomTileWidth();
        for (int i = width/2 + 3; i <= end - width/2 - 3; i++) {
            list.add(i);
        }
        int randomElement = list.get(Main.rand.nextInt(list.size()));
        System.out.println("Random X cootdinate" + randomElement);
        return randomElement;
    }

    private static int chooseRoomCenterY() {
        List<Integer> list = new ArrayList<>();
        int end = Main.HEIGHT;
        int height = randomTileHeight();
        for (int i = height/2 + 3; i <= end - height/2 - 3; i++) {
            list.add(i);
        }
        int randomElement = list.get(Main.rand.nextInt(list.size()));
        System.out.println("Random Y cootdinate" + randomElement);
        return randomElement;
    }
    public static void connectRooms(TETile[][] tiles) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Rooms room1 = rooms.get(i);
            Rooms room2 = rooms.get(i + 1);
            drawHallway(tiles, room1.getCenterX(), room1.getCenterY(),room2.getCenterX(), room2.getCenterY());
        }
    }

    private static void drawHallway(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        if (x1 != x2) {
            if (x1 < x2) {
                for (int x = x1; x <= x2; x++) {
                    tiles[x][y1] = Tileset.FLOWER;
                }
            } else {
                for (int x = x2; x <= x1; x++) {
                    tiles[x][y1] = Tileset.FLOWER;
                }
            }
        }
        if (y1 != y2) {
            if (y1 < y2) {
                for (int y = y1; y <= y2; y++) {
                    tiles[x2][y] = Tileset.FLOWER;
                }
            } else {
                for (int y = y2; y <= y1; y++) {
                    tiles[x2][y] = Tileset.FLOWER;
                }
            }
        }
    }
    public static void buildWalls(TETile[][] world) {
        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOWER) {
                    if (x > 0 && world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                    if (x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (y > 0 && world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                    if (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                    if (x > 0 && y > 0 && world[x - 1][y - 1] == Tileset.NOTHING) {
                        world[x - 1][y - 1] = Tileset.WALL;
                    }
                    if (x < Main.WIDTH - 1 && y > 0 && world[x + 1][y - 1] == Tileset.NOTHING) {
                        world[x + 1][y - 1] = Tileset.WALL;
                    }
                    if (x > 0 && y < Main.HEIGHT - 1 && world[x - 1][y + 1] == Tileset.NOTHING) {
                        world[x - 1][y + 1] = Tileset.WALL;
                    }
                    if (x < Main.WIDTH - 1 && y < Main.HEIGHT - 1 && world[x + 1][y + 1] == Tileset.NOTHING) {
                        world[x + 1][y + 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

}
