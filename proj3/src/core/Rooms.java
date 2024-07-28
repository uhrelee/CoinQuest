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
    private List<Rooms> currRooms;
    private Random rand;


    public Rooms(int centerX, int centerY, int height, int width, Random rand) {
        this.CenterX = centerX;
        this.CenterY = centerY;
        this.height = height;
        this.width = width;
        this.currRooms = new ArrayList<>();
        this.rand = rand;
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

    // generate a random number of rooms
    public int generateRoomNumber() {
        List<Integer> list = new ArrayList<>();
        for (int i = 3; i <= 8; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }


    // place a room
    public void fillWithRandomTiles(TETile[][] tiles) {
        while (true) {
            int h = randomRoomHeight();
            int w = randomRoomWidth();
            int centerX = chooseRoomCenterX();
            int centerY = chooseRoomCenterY();
            Rooms newRoom = new Rooms(centerX, centerY, h, w, rand);

            boolean overlaps = false;
            for (Rooms room : currRooms) {
                if (newRoom.ifRoomsOverlap(room)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                for (int x = centerX - w / 2; x < centerX + w / 2; x++) {
                    for (int y = centerY - h / 2; y < centerY + h / 2; y++) {
                        tiles[x][y] = Tileset.CustomFloor;
                    }
                }
                currRooms.add(newRoom);
                break;
            }
        }
    }

    // @source help from chatGPT
    // checks whether the rooms overlap
    public boolean ifRoomsOverlap(Rooms other) {
        return this.CenterX + this.width / 2 + 1 >= other.CenterX - other.width / 2
                && this.CenterX - this.width / 2 - 1 <= other.CenterX + other.width / 2
                && this.CenterY + this.height / 2 + 1 >= other.CenterY - other.height / 2
                && this.CenterY - this.height / 2 - 1 <= other.CenterY + other.height / 2;
    }


    // place several rooms
    public void fillWithSeveralRooms(TETile[][] tiles) {
        int numberOfRooms = generateRoomNumber();
        for (int i = 0; i < numberOfRooms; i++) {
            fillWithRandomTiles(tiles);
        }
    }

    // generate random height of the room
    private int randomRoomHeight() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        return list.get(rand.nextInt(list.size()));
    }

    // generate random width of the room
    private int randomRoomWidth() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        return list.get(rand.nextInt(list.size()));
    }

    // generate random center X for a room
    private int chooseRoomCenterX() {
        List<Integer> list = new ArrayList<>();
        int end = Main.WIDTH;
        int w = randomRoomWidth();
        for (int i = w / 2 + 3; i <= end - w / 2 - 3; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }

    // generate random center Y for a room
    private int chooseRoomCenterY() {
        List<Integer> list = new ArrayList<>();
        int end = Main.HEIGHT;
        int h = randomRoomHeight();
        for (int i = h / 2 + 3; i <= end - h / 2 - 3; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }

    //connect room 1 with room 2, room 2 with room 3, room 3 with room 4 ...
    public void connectRooms(TETile[][] tiles) {
        for (int i = 0; i < currRooms.size() - 1; i++) {
            Rooms room1 = currRooms.get(i);
            Rooms room2 = currRooms.get(i + 1);
            drawHallway(tiles, room1.getCenterX(), room1.getCenterY(), room2.getCenterX(), room2.getCenterY());
        }
    }

    // @source help from chatGPT
    // make hallways between rooms
    private void drawHallway(TETile[][] tiles, int x1, int y1, int x2, int y2) {
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

    // @source help from chatGPT
    // build walls around rooms and hallways based on adjacent tile types
    public void buildWalls(TETile[][] world) {
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
