package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Rooms {
    int CenterX;
    int CenterY;
    int height;
    int width;
    public static final List<Rooms> rooms = new ArrayList<>();
    public static Random rand;


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

    //generate a random number of rooms
    public static int generateRoomNumber() {
        List<Integer> list = new ArrayList<>();
        for (int i = 3; i <= 8; i++) {
            list.add(i);
        }
        int randomElement = list.get(rand.nextInt(list.size()));
        return randomElement;
    }


    //place a room
    public static void fillWithRandomTiles(TETile[][] tiles) {
        while (true) {
            int height = randomRoomHeight();
            int width = randomRoomWidth();
            int CenterX = chooseRoomCenterX();
            int CenterY = chooseRoomCenterY();
            Rooms newRoom = new Rooms(CenterX, CenterY, height, width);

            boolean overlaps = false;
            for (Rooms room : rooms) {
                if (newRoom.ifRoomsOverlap(room)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                for (int x = CenterX - width / 2; x < CenterX + width / 2; x++) {
                    for (int y = CenterY - height / 2; y < CenterY + height / 2; y++) {
                        tiles[x][y] = Tileset.CustomFloor;
                    }
                }
                rooms.add(newRoom);
                break;
            }
        }
    }

    //checks whether the rooms overlap
    public boolean ifRoomsOverlap(Rooms other) {
        if (this.CenterX + this.width / 2 + 1 < other.CenterX - other.width / 2 ||
                this.CenterX - this.width / 2 - 1 > other.CenterX + other.width / 2 ||
                this.CenterY + this.height / 2 + 1 < other.CenterY - other.height / 2 ||
                this.CenterY - this.height / 2 - 1 > other.CenterY + other.height / 2) {
            return false;
        } else {
            return true;
        }
        }


    //place several rooms
    public static void fillWithSeveralRooms(TETile[][] tiles) {
        int numberOfRooms = generateRoomNumber();
        for (int i = 0; i < numberOfRooms; i++) {
            fillWithRandomTiles(tiles);
        }
    }

    //generate random height of the room
    private static int randomRoomHeight() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        int randomElement = list.get(rand.nextInt(list.size()));
        return randomElement;
    }

    //generate random width of the room
    private static int randomRoomWidth() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        int randomElement = list.get(rand.nextInt(list.size()));
        return randomElement;
    }

    //generate random center X for a room
    private static int chooseRoomCenterX() {
        List<Integer> list = new ArrayList<>();
        int end = Main.WIDTH;
        int width = randomRoomWidth();
        for (int i = width / 2 + 3; i <= end - width / 2 - 3; i++) {
            list.add(i);
        }
        int randomElement = list.get(rand.nextInt(list.size()));
        return randomElement;
    }

    //generate random center Y for a room
    private static int chooseRoomCenterY() {
        List<Integer> list = new ArrayList<>();
        int end = Main.HEIGHT;
        int height = randomRoomHeight();
        for (int i = height / 2 + 3; i <= end - height / 2 - 3; i++) {
            list.add(i);
        }
        int randomElement = list.get(rand.nextInt(list.size()));
        return randomElement;
    }

    //connect room 1 with room 2, room 2 with room 3, room 3 with room 4 ...
    public static void connectRooms(TETile[][] tiles) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Rooms room1 = rooms.get(i);
            Rooms room2 = rooms.get(i + 1);
            drawHallway(tiles, room1.getCenterX(), room1.getCenterY(), room2.getCenterX(), room2.getCenterY());
        }
    }

    //make hallways between rooms
    private static void drawHallway(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int currentX = x1;
        int currentY = y1;

        while (currentX != x2 || currentY != y2) {
            tiles[currentX][currentY] = Tileset.CustomFloor;

            if (currentX < x2) {
                currentX++;
            } else if (currentX > x2) {
                currentX--;
            } else if (currentY < y2) {
                currentY++;
            } else if (currentY > y2) {
                currentY--;
            }
        }
        tiles[x2][y2] = Tileset.CustomFloor;
    }

    //build walls around rooms and hallways based on adjacent tile types
    public static void buildWalls(TETile[][] world) {
        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (world[x][y] == Tileset.CustomFloor) {
                    if (x > 0 && world[x - 1][y] == Tileset.CustomNothing) {
                        world[x - 1][y] = Tileset.CustomWall;
                    }
                    if (x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.CustomNothing) {
                        world[x + 1][y] = Tileset.CustomWall;
                    }
                    if (y > 0 && world[x][y - 1] == Tileset.CustomNothing) {
                        world[x][y - 1] = Tileset.CustomWall;
                    }
                    if (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.CustomNothing) {
                        world[x][y + 1] = Tileset.CustomWall;
                    }
                    if (x > 0 && y > 0 && world[x - 1][y - 1] == Tileset.CustomNothing) {
                        world[x - 1][y - 1] = Tileset.CustomWall;
                    }
                    if (x < Main.WIDTH - 1 && y > 0 && world[x + 1][y - 1] == Tileset.CustomNothing) {
                        world[x + 1][y - 1] = Tileset.CustomWall;
                    }
                    if (x > 0 && y < Main.HEIGHT - 1 && world[x - 1][y + 1] == Tileset.CustomNothing) {
                        world[x - 1][y + 1] = Tileset.CustomWall;
                    }
                    if (x < Main.WIDTH - 1 && y < Main.HEIGHT - 1 && world[x + 1][y + 1] == Tileset.CustomNothing) {
                        world[x + 1][y + 1] = Tileset.CustomWall;
                    }
                }
            }
        }
        //make sure the floor cannot be on the border
        for (int x = 0; x < Main.WIDTH; x++) {
            //check bottom
            if (world[x][0] == Tileset.FLOWER) {
                world[x][0] = Tileset.WALL;
            }
            //check top
            if (world[x][Main.HEIGHT - 1] == Tileset.CustomFloor) {
                world[x][Main.HEIGHT - 1] = Tileset.CustomWall;
            }
        }

        for (int y = 0; y < Main.HEIGHT; y++) {
            //check left
            if (world[0][y] == Tileset.CustomFloor) {
                world[0][y] = Tileset.CustomWall;
            }
            //check right
            if (world[Main.WIDTH - 1][y] == Tileset.CustomFloor) {
                world[Main.WIDTH - 1][y] = Tileset.CustomWall;
            }
        }
    }
}
