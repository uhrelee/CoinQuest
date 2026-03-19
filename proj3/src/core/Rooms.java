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

    public int generateRoomNumber() {
        List<Integer> list = new ArrayList<>();
        for (int i = 3; i <= 8; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }

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
                        tiles[x][y] = Tileset.Floor;
                    }
                }
                currRooms.add(newRoom);
                break;
            }
        }
    }

    public boolean ifRoomsOverlap(Rooms other) {
        return this.CenterX + this.width / 2 + 1 >= other.CenterX - other.width / 2
                && this.CenterX - this.width / 2 - 1 <= other.CenterX + other.width / 2
                && this.CenterY + this.height / 2 + 1 >= other.CenterY - other.height / 2
                && this.CenterY - this.height / 2 - 1 <= other.CenterY + other.height / 2;
    }

    public void fillWithSeveralRooms(TETile[][] tiles) {
        int numberOfRooms = generateRoomNumber();
        for (int i = 0; i < numberOfRooms; i++) {
            fillWithRandomTiles(tiles);
        }
    }

    private int randomRoomHeight() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        return list.get(rand.nextInt(list.size()));
    }

    private int randomRoomWidth() {
        List<Integer> list = Arrays.asList(4, 5, 6, 7, 8);
        return list.get(rand.nextInt(list.size()));
    }

    private int chooseRoomCenterX() {
        List<Integer> list = new ArrayList<>();
        int end = Main.WIDTH;
        int w = randomRoomWidth();
        for (int i = w / 2 + 3; i <= end - w / 2 - 3; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }

    private int chooseRoomCenterY() {
        List<Integer> list = new ArrayList<>();
        int end = Main.HEIGHT;
        int h = randomRoomHeight();
        for (int i = h / 2 + 3; i <= end - h / 2 - 3; i++) {
            list.add(i);
        }
        return list.get(rand.nextInt(list.size()));
    }

    public void connectRooms(TETile[][] tiles) {
        for (int i = 0; i < currRooms.size() - 1; i++) {
            Rooms room1 = currRooms.get(i);
            Rooms room2 = currRooms.get(i + 1);
            drawHallway(tiles, room1.getCenterX(), room1.getCenterY(), room2.getCenterX(), room2.getCenterY());
        }
    }

    private void drawHallway(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int currentX = x1;
        int currentY = y1;

        while (currentX != x2 || currentY != y2) {
            tiles[currentX][currentY] = Tileset.Floor;

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
        tiles[x2][y2] = Tileset.Floor;
    }

    private void refineWallType(TETile[][] world, int x, int y) {
        boolean[] floor = {
            x > 0 && world[x - 1][y] == Tileset.Floor,
            x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor,
            y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Floor,
            y > 0 && world[x][y - 1] == Tileset.Floor
        };

        boolean[] grass = {
            x > 0 && world[x - 1][y] == Tileset.Grass,
            x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Grass,
            y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Grass,
            y > 0 && world[x][y - 1] == Tileset.Grass
        };

        boolean[] wall = {
            x > 0 && isWall(world[x - 1][y]),
            x < Main.WIDTH - 1 && isWall(world[x + 1][y]),
            y < Main.HEIGHT - 1 && isWall(world[x][y + 1]),
            y > 0 && isWall(world[x][y - 1])
        };

        boolean[] corner = {
            y < Main.HEIGHT - 1
                        && (world[x][y + 1] == Tileset.TopLeftCorner
                        || world[x][y + 1] == Tileset.TopRightCorner),
            y > 0
                        && (world[x][y - 1] == Tileset.BottomLeftCorner
                        || world[x][y - 1] == Tileset.BottomRightCorner)
        };

        setWallType(world, x, y, floor, grass, wall, corner);
    }

    private void setWallType(
            TETile[][] world, int x, int y,
            boolean[] floor, boolean[] grass,
            boolean[] wall, boolean[] corner) {
        if (floor[0] && floor[1]) {
            if (setDoubleWall(world, x, y, grass, floor)) {
                return;
            }
        }

        if (setVerticalWall(world, x, y, floor, grass, wall)) {
            return;
        }
        if (setHorizontalWall(world, x, y, floor, grass, wall, corner)) {
            return;
        }
        if (setCornerWall(world, x, y, floor, grass, wall, corner)) {
            return;
        }
        if (setInnerCornerWall(world, x, y, floor, wall)) {
            return;
        }
    }

    private boolean setDoubleWall(TETile[][] world, int x, int y, boolean[] grass, boolean[] floor) {
        if (grass[3] && (floor[2] || y == Main.HEIGHT - 1)) {
            world[x][y] = Tileset.DoubleWallTop;
            return true;
        } else if (grass[2] && (floor[3] || y == 0)) {
            world[x][y] = Tileset.DoubleWallBottom;
            return true;
        }
        return false;
    }

    private boolean setVerticalWall(TETile[][] world, int x, int y, boolean[] floor, boolean[] grass, boolean[] wall) {
        if (floor[0] && floor[1] && (grass[2] || wall[2]) && (grass[3] || wall[3])) {
            world[x][y] = Tileset.SingleWallVertical;
            return true;
        } else if (floor[2] && floor[0] && floor[1] && y > 0
                && (world[x][y - 1] == Tileset.SingleWallVertical
                || world[x][y - 1] == Tileset.DoubleWallBottom)) {
            world[x][y] = Tileset.DoubleWallTop;
            return true;
        } else if (floor[3] && floor[0] && floor[1] && y < Main.HEIGHT - 1
                && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.DoubleWallBottom;
            return true;
        }
        return false;
    }

    private boolean setHorizontalWall(
            TETile[][] world, int x, int y,
            boolean[] floor, boolean[] grass,
            boolean[] wall, boolean[] corner) {
        if (floor[0] && wall[1] && wall[3] && y > 0
                && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndTopLeftCorner;
            return true;
        } else if (floor[1] && wall[0] && wall[3] && y > 0
                && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndTopRightCorner;
            return true;
        } else if (floor[3] && wall[0] && wall[1] && y < Main.HEIGHT - 1
                && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.BottomWallAndLeftCorner;
            return true;
        } else if (floor[3] && wall[1] && wall[0] && y < Main.HEIGHT - 1
                && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.BottomWallAndRightCorner;
            return true;
        } else if (floor[1] && wall[0] && wall[3] && y > 0
                && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndRightBottomCorner;
            return true;
        } else if (floor[0] && wall[1] && wall[3] && y > 0
                && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndLeftBottomCorner;
            return true;
        }
        return false;
    }

    private boolean setCornerWall(
            TETile[][] world, int x, int y,
            boolean[] floor, boolean[] grass,
            boolean[] wall, boolean[] corner) {
        if (floor[3] && ((wall[2] || grass[2]) || y == Main.HEIGHT - 1)
                && (wall[0] || grass[0]) && (wall[1] || grass[1])) {
            world[x][y] = Tileset.TopWall;
            return true;
        } else if (floor[2] && ((wall[3] || grass[3]) || y == 0)
                && (wall[0] || grass[0]) && (wall[1] || grass[1])) {
            world[x][y] = Tileset.BottomWall;
            return true;
        } else if (floor[0] && !floor[1] && ((wall[1] || grass[1]) || x == Main.WIDTH - 1)
                && (wall[2] || grass[2]) && (wall[3] || grass[3])) {
            world[x][y] = Tileset.RightWall;
            return true;
        } else if (floor[1] && !floor[0] && ((wall[0] || grass[0]) || x == 0)
                && (wall[2] || grass[2]) && (wall[3] || grass[3])) {
            world[x][y] = Tileset.LeftWall;
            return true;
        } else if ((grass[2] || y == Main.HEIGHT - 1)
                && (grass[0] || x == 0) && (wall[3] || floor[3])
                && (wall[1] || floor[1]) && !corner[0] && !corner[1]) {
            world[x][y] = Tileset.TopLeftCorner;
            return true;
        } else if ((grass[2] || y == Main.HEIGHT - 1)
                && (grass[1] || x == Main.WIDTH - 1) && (wall[3] || floor[3])
                && (wall[0] || floor[0]) && !corner[0] && !corner[1]) {
            world[x][y] = Tileset.TopRightCorner;
            return true;
        } else if ((grass[3] || y == 0)
                && (grass[0] || x == 0) && (wall[2] || floor[2])
                && (wall[1] || floor[1]) && !corner[0] && !corner[1]) {
            world[x][y] = Tileset.BottomLeftCorner;
            return true;
        } else if ((grass[3] || y == 0)
                && (grass[1] || x == Main.WIDTH - 1) && (wall[2] || floor[2])
                && (wall[0] || floor[0]) && !corner[0] && !corner[1]) {
            world[x][y] = Tileset.BottomRightCorner;
            return true;
        }
        return false;
    }

    private boolean setInnerCornerWall(TETile[][] world, int x, int y, boolean[] floor, boolean[] wall) {
        if ((wall[0] || x == 0)
                && (wall[3] || y == 0) && floor[1] && floor[2]) {
            world[x][y] = Tileset.RightTopInnerCorner;
            return true;
        } else if ((wall[2] || y == Main.HEIGHT - 1)
                && (wall[0] || x == 0) && floor[1] && floor[3]) {
            world[x][y] = Tileset.RightInnerCorner;
            return true;
        } else if ((wall[1] || x == Main.WIDTH - 1)
                && (wall[3] || y == 0) && floor[0] && floor[2]) {
            world[x][y] = Tileset.LeftTopInnerCorner;
            return true;
        } else if ((wall[2] || y == Main.HEIGHT - 1)
                && (wall[1] || x == Main.WIDTH - 1) && floor[0] && floor[3]) {
            world[x][y] = Tileset.LeftInnerCorner;
            return true;
        } else if (floor[3] && floor[0] && floor[2]
                && (wall[1] || x == Main.WIDTH - 1)) {
            world[x][y] = Tileset.SingleWallLeft;
            return true;
        } else if (floor[3] && floor[1] && floor[2]
                && (wall[0] || x == 0)) {
            world[x][y] = Tileset.SingleWallRight;
            return true;
        } else if (floor[3] && floor[0] && floor[2] && floor[1]) {
            world[x][y] = Tileset.SingleWall;
            return true;
        } else if (floor[3] && floor[2] && (wall[0] || x == 0)
                && (wall[1] || x == Main.WIDTH - 1)) {
            world[x][y] = Tileset.SingleWallMiddle;
            return true;
        }
        return false;
    }

    public void handleEdgeCases(TETile[][] world) {
        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (isWall(world[x][y])) {
                    applyEdgeCaseRules(world, x, y);
                }
            }
        }
    }

    private void applyEdgeCaseRules(TETile[][] world, int x, int y) {
        if (world[x][y] == Tileset.SingleWallVertical
                || world[x][y] == Tileset.DoubleWallTop
                || world[x][y] == Tileset.DoubleWallBottom) {
            return;
        }

        boolean floorLeft = x > 0 && world[x - 1][y] == Tileset.Floor;
        boolean floorRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor;
        boolean floorUp = y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Floor;
        boolean floorDown = y > 0 && world[x][y - 1] == Tileset.Floor;

        if (floorLeft && floorRight) {
            if (y > 0 && (world[x][y - 1] == Tileset.Grass
                    || world[x][y - 1] == Tileset.SingleWallVertical
                    || world[x][y - 1] == Tileset.DoubleWallBottom)) {
                world[x][y] = Tileset.DoubleWallTop;
                return;
            } else if (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.Grass
                    || world[x][y + 1] == Tileset.SingleWallVertical
                    || world[x][y + 1] == Tileset.DoubleWallTop)) {
                world[x][y] = Tileset.DoubleWallBottom;
                return;
            }
        }
        if (world[x][y] != Tileset.DoubleWallTop && world[x][y] != Tileset.DoubleWallBottom) {
            if ((floorDown) && (floorLeft) && (floorRight) && (y < Main.HEIGHT - 1)
                    && (world[x][y + 1] == Tileset.SingleWallVertical)) {
                world[x][y] = Tileset.DoubleWallBottom;
            } else if ((floorLeft) && (y > 0) && (world[x][y - 1] == Tileset.SingleWallVertical)
                    && (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.RightWall
                            || world[x][y + 1] == Tileset.TopRightCorner))) {
                world[x][y] = Tileset.WallAndTopLeftCorner;
            } else if ((floorRight) && (y > 0) && (world[x][y - 1] == Tileset.SingleWallVertical)
                    && (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.LeftWall
                            || world[x][y + 1] == Tileset.TopLeftCorner))) {
                world[x][y] = Tileset.WallAndTopRightCorner;
            } else if ((floorDown) && (floorLeft) && (x < Main.WIDTH - 1)
                    && (world[x + 1][y] == Tileset.SingleWallRight
                            || (world[x + 1][y] == Tileset.SingleWallMiddle))
                    && (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.RightWall)) {
                world[x][y] = Tileset.BottomWallAndLeftCorner;
            } else if ((floorDown) && (floorRight) && (x > 0)
                    && (world[x - 1][y] == Tileset.SingleWallLeft
                            || world[x - 1][y] == Tileset.SingleWallMiddle)
                    && (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.LeftWall)) {
                world[x][y] = Tileset.BottomWallAndRightCorner;
            } else if ((floorRight) && (y < Main.HEIGHT - 1) && (world[x][y + 1] == Tileset.SingleWallVertical)
                    && (x > 0) && (world[x - 1][y] == Tileset.BottomWall
                    || world[x - 1][y] == Tileset.LeftWall)) {
                world[x][y] = Tileset.WallAndLeftBottomCorner;
            } else if ((floorLeft) && (y < Main.HEIGHT - 1) && (world[x][y + 1] == Tileset.SingleWallVertical)
                    && (x < Main.WIDTH - 1) && (world[x + 1][y] == Tileset.BottomWall
                    || world[x + 1][y] == Tileset.RightWall)) {
                world[x][y] = Tileset.WallAndRightBottomCorner;
            }
        }
    }

    public void buildWalls(TETile[][] world) {
        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (world[x][y] == Tileset.Floor) {
                    placeBasicWalls(world, x, y);
                }
            }
        }

        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (isWall(world[x][y])) {
                    refineWallType(world, x, y);
                }
            }
        }

        handleEdgeCases(world);
        for (int x = 0; x < Main.WIDTH; x++) {
            for (int y = 0; y < Main.HEIGHT; y++) {
                if (world[x][y] == Tileset.Grass) {
                    finalCheckDoubleWalls(world, x, y);
                }
            }
        }
    }

    // @source help from chatGPT
    private void finalCheckDoubleWalls(TETile[][] world, int x, int y) {
        boolean floorLeft = x > 0 && world[x - 1][y] == Tileset.Floor;
        boolean floorRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor;

        if (floorLeft && floorRight) {
            if (y > 0 && (world[x][y - 1] == Tileset.Grass || world[x][y - 1] == Tileset.DoubleWallBottom)) {
                world[x][y] = Tileset.DoubleWallTop;
            } else if (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.Grass
                    || world[x][y + 1] == Tileset.DoubleWallTop)) {
                world[x][y] = Tileset.DoubleWallBottom;
            }
        }
    }

    // @source help from chatGPT
    private void placeBasicWalls(TETile[][] world, int x, int y) {
        if (x > 0 && world[x - 1][y] == Tileset.Grass) {
            world[x - 1][y] = Tileset.LeftWall;
        }
        if (x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Grass) {
            world[x + 1][y] = Tileset.RightWall;
        }
        if (y > 0 && world[x][y - 1] == Tileset.Grass) {
            world[x][y - 1] = Tileset.BottomWall;
        }
        if (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Grass) {
            world[x][y + 1] = Tileset.TopWall;
        }
        if (x > 0 && y < Main.HEIGHT - 1 && world[x - 1][y + 1] == Tileset.Grass) {
            world[x - 1][y + 1] = Tileset.TopLeftCorner;
        }
        if (x < Main.WIDTH - 1 && y < Main.HEIGHT - 1 && world[x + 1][y + 1] == Tileset.Grass) {
            world[x + 1][y + 1] = Tileset.TopRightCorner;
        }
        if (x > 0 && y > 0 && world[x - 1][y - 1] == Tileset.Grass) {
            world[x - 1][y - 1] = Tileset.BottomLeftCorner;
        }
        if (x < Main.WIDTH - 1 && y > 0 && world[x + 1][y - 1] == Tileset.Grass) {
            world[x + 1][y - 1] = Tileset.BottomRightCorner;
        }
    }


    private boolean isWall(TETile tile) {
        return tile == Tileset.LeftWall || tile == Tileset.RightWall
                || tile == Tileset.TopWall || tile == Tileset.BottomWall
                || tile == Tileset.TopLeftCorner || tile == Tileset.TopRightCorner
                || tile == Tileset.BottomLeftCorner || tile == Tileset.BottomRightCorner
                || tile == Tileset.LeftInnerCorner || tile == Tileset.LeftTopInnerCorner
                || tile == Tileset.RightInnerCorner || tile == Tileset.RightTopInnerCorner
                || tile == Tileset.SingleWall || tile == Tileset.SingleWallLeft
                || tile == Tileset.SingleWallMiddle || tile == Tileset.SingleWallRight
                || tile == Tileset.SingleWallVertical || tile == Tileset.DoubleWallTop
                || tile == Tileset.DoubleWallBottom || tile == Tileset.WallAndTopLeftCorner
                || tile == Tileset.WallAndTopRightCorner || tile == Tileset.BottomWallAndLeftCorner
                || tile == Tileset.BottomWallAndRightCorner || tile == Tileset.WallAndLeftBottomCorner
                || tile == Tileset.WallAndRightBottomCorner;
    }

}
