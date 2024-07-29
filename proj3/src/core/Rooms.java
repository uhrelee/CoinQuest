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
        boolean floorLeft = x > 0 && world[x - 1][y] == Tileset.Floor;
        boolean floorRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor;
        boolean floorUp = y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Floor;
        boolean floorDown = y > 0 && world[x][y - 1] == Tileset.Floor;

        boolean grassLeft = x > 0 && world[x - 1][y] == Tileset.Grass;
        boolean grassRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Grass;
        boolean grassUp = y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Grass;
        boolean grassDown = y > 0 && world[x][y - 1] == Tileset.Grass;

        boolean wallLeft = x > 0 && isWall(world[x - 1][y]);
        boolean wallRight = x < Main.WIDTH - 1 && isWall(world[x + 1][y]);
        boolean wallUp = y < Main.HEIGHT - 1 && isWall(world[x][y + 1]);
        boolean wallDown = y > 0 && isWall(world[x][y - 1]);

        boolean cornerUp = y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.TopLeftCorner || world[x][y + 1] == Tileset.TopRightCorner);
        boolean cornerDown = y > 0 && (world[x][y - 1] == Tileset.BottomLeftCorner || world[x][y - 1] == Tileset.BottomRightCorner);

        if (floorLeft && floorRight) {
            if (grassDown && (floorUp || y == Main.HEIGHT - 1)) {
                world[x][y] = Tileset.DoubleWallTop;
                return;
            } else if (grassUp && (floorDown || y == 0)) {
                world[x][y] = Tileset.DoubleWallBottom;
                return;
            }
        }
        if (floorLeft && floorRight && (grassUp || wallUp) && (grassDown || wallDown)) {
            world[x][y] = Tileset.SingleWallVertical;
        }
        else if (floorUp && floorLeft && floorRight && y > 0 && (world[x][y - 1] == Tileset.SingleWallVertical || world[x][y - 1] == Tileset.DoubleWallBottom)) {
            world[x][y] = Tileset.DoubleWallTop;
        } else if (floorDown && floorLeft && floorRight && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.DoubleWallBottom;
        } else if (floorLeft && wallRight && wallDown && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndTopLeftCorner;
        } else if (floorRight && wallLeft && wallDown && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndTopRightCorner;
        } else if (floorDown && wallLeft && wallRight && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.BottomWallAndLeftCorner;
        } else if (floorDown && wallRight && wallLeft && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.BottomWallAndRightCorner;
        } else if (floorRight && wallLeft && wallDown && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndRightBottomCorner;
        } else if (floorLeft && wallRight && wallDown && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical) {
            world[x][y] = Tileset.WallAndLeftBottomCorner;
        }
        else if (floorDown && ((wallUp || grassUp) || y == Main.HEIGHT - 1) && (wallLeft || grassLeft) && (wallRight || grassRight)) {
            world[x][y] = Tileset.TopWall;
        } else if (floorUp && ((wallDown || grassDown) || y == 0) && (wallLeft || grassLeft) && (wallRight || grassRight)) {
            world[x][y] = Tileset.BottomWall;
        } else if (floorLeft && !floorRight && ((wallRight || grassRight) || x == Main.WIDTH - 1) && (wallUp || grassUp) && (wallDown || grassDown)) {
            world[x][y] = Tileset.RightWall;
        } else if (floorRight && !floorLeft && ((wallLeft || grassLeft) || x == 0) && (wallUp || grassUp) && (wallDown || grassDown)) {
            world[x][y] = Tileset.LeftWall;
        }
        else if ((grassUp || y == Main.HEIGHT - 1) && (grassLeft || x == 0) && (wallDown || floorDown) && (wallRight || floorRight) && !cornerUp && !cornerDown) {
            world[x][y] = Tileset.TopLeftCorner;
        } else if ((grassUp || y == Main.HEIGHT - 1) && (grassRight || x == Main.WIDTH - 1) && (wallDown || floorDown) && (wallLeft || floorLeft) && !cornerUp && !cornerDown) {
            world[x][y] = Tileset.TopRightCorner;
        } else if ((grassDown || y == 0) && (grassLeft || x == 0) && (wallUp || floorUp) && (wallRight || floorRight) && !cornerUp && !cornerDown) {
            world[x][y] = Tileset.BottomLeftCorner;
        } else if ((grassDown || y == 0) && (grassRight || x == Main.WIDTH - 1) && (wallUp || floorUp) && (wallLeft || floorLeft) && !cornerUp && !cornerDown) {
            world[x][y] = Tileset.BottomRightCorner;
        }
        else if ((wallLeft || x == 0) && (wallDown || y == 0) && floorRight && floorUp) {
            world[x][y] = Tileset.RightTopInnerCorner;
        } else if ((wallUp || y == Main.HEIGHT - 1) && (wallLeft || x == 0) && floorRight && floorDown) {
            world[x][y] = Tileset.RightInnerCorner;
        } else if ((wallRight || x == Main.WIDTH - 1) && (wallDown || y == 0) && floorLeft && floorUp) {
            world[x][y] = Tileset.LeftTopInnerCorner;
        } else if ((wallUp || y == Main.HEIGHT - 1) && (wallRight || x == Main.WIDTH - 1) && floorLeft && floorDown) {
            world[x][y] = Tileset.LeftInnerCorner;
        }
        else if (floorDown && floorLeft && floorUp && (wallRight || x == Main.WIDTH - 1)) {
            world[x][y] = Tileset.SingleWallLeft;
        } else if (floorDown && floorRight && floorUp && (wallLeft || x == 0)) {
            world[x][y] = Tileset.SingleWallRight;
        } else if (floorDown && floorLeft && floorUp && floorRight) {
            world[x][y] = Tileset.SingleWall;
        } else if (floorDown && floorUp && (wallLeft || x == 0) && (wallRight || x == Main.WIDTH - 1)) {
            world[x][y] = Tileset.SingleWallMiddle;
        }
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
        if (world[x][y] == Tileset.SingleWallVertical ||
                world[x][y] == Tileset.DoubleWallTop ||
                world[x][y] == Tileset.DoubleWallBottom) {
            return;
        }

        boolean floorLeft = x > 0 && world[x - 1][y] == Tileset.Floor;
        boolean floorRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor;
        boolean floorUp = y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Floor;
        boolean floorDown = y > 0 && world[x][y - 1] == Tileset.Floor;

        if (floorLeft && floorRight) {
            if (y > 0 && (world[x][y - 1] == Tileset.Grass || world[x][y - 1] == Tileset.SingleWallVertical || world[x][y - 1] == Tileset.DoubleWallBottom)) {
                world[x][y] = Tileset.DoubleWallTop;
                return;
            } else if (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.Grass || world[x][y + 1] == Tileset.SingleWallVertical || world[x][y + 1] == Tileset.DoubleWallTop)) {
                world[x][y] = Tileset.DoubleWallBottom;
                return;
            }
        }if (world[x][y] != Tileset.DoubleWallTop && world[x][y] != Tileset.DoubleWallBottom) {
            if (floorDown && floorLeft && floorRight && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical) {
                world[x][y] = Tileset.DoubleWallBottom;
            }
            else if (floorLeft && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical && (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.RightWall || world[x][y + 1] == Tileset.TopRightCorner))) {
                world[x][y] = Tileset.WallAndTopLeftCorner;
            }
            else if (floorRight && y > 0 && world[x][y - 1] == Tileset.SingleWallVertical && (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.LeftWall || world[x][y + 1] == Tileset.TopLeftCorner))) {
                world[x][y] = Tileset.WallAndTopRightCorner;
            }
            else if (floorDown && floorLeft && x < Main.WIDTH - 1 && (world[x + 1][y] == Tileset.SingleWallRight || world[x + 1][y] == Tileset.SingleWallMiddle) && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.RightWall) {
                world[x][y] = Tileset.BottomWallAndLeftCorner;
            }
            else if (floorDown && floorRight && x > 0 && (world[x - 1][y] == Tileset.SingleWallLeft || world[x - 1][y] == Tileset.SingleWallMiddle) && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.LeftWall) {
                world[x][y] = Tileset.BottomWallAndRightCorner;
            }
            else if (floorRight && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical && x > 0 && (world[x - 1][y] == Tileset.BottomWall || world[x - 1][y] == Tileset.LeftWall)) {
                world[x][y] = Tileset.WallAndLeftBottomCorner;
            }
            else if (floorLeft && y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.SingleWallVertical && x < Main.WIDTH - 1 && (world[x + 1][y] == Tileset.BottomWall || world[x + 1][y] == Tileset.RightWall)) {
                world[x][y] = Tileset.WallAndRightBottomCorner;
            }
        }
    }


    private boolean isVerticalWall(TETile tile) {
        return tile == Tileset.TopWall || tile == Tileset.BottomWall ||
                tile == Tileset.SingleWallVertical || tile == Tileset.DoubleWallTop ||
                tile == Tileset.DoubleWallBottom;
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

    private void finalCheckDoubleWalls(TETile[][] world, int x, int y) {
        boolean floorLeft = x > 0 && world[x - 1][y] == Tileset.Floor;
        boolean floorRight = x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Floor;

        if (floorLeft && floorRight) {
            if (y > 0 && (world[x][y - 1] == Tileset.Grass || world[x][y - 1] == Tileset.DoubleWallBottom)) {
                world[x][y] = Tileset.DoubleWallTop;
            } else if (y < Main.HEIGHT - 1 && (world[x][y + 1] == Tileset.Grass || world[x][y + 1] == Tileset.DoubleWallTop)) {
                world[x][y] = Tileset.DoubleWallBottom;
            }
        }
    }


    private void placeBasicWalls(TETile[][] world, int x, int y) {
        if (x > 0 && world[x - 1][y] == Tileset.Grass) world[x - 1][y] = Tileset.LeftWall;
        if (x < Main.WIDTH - 1 && world[x + 1][y] == Tileset.Grass) world[x + 1][y] = Tileset.RightWall;
        if (y > 0 && world[x][y - 1] == Tileset.Grass) world[x][y - 1] = Tileset.BottomWall;
        if (y < Main.HEIGHT - 1 && world[x][y + 1] == Tileset.Grass) world[x][y + 1] = Tileset.TopWall;
        if (x > 0 && y < Main.HEIGHT - 1 && world[x - 1][y + 1] == Tileset.Grass) world[x - 1][y + 1] = Tileset.TopLeftCorner;
        if (x < Main.WIDTH - 1 && y < Main.HEIGHT - 1 && world[x + 1][y + 1] == Tileset.Grass) world[x + 1][y + 1] = Tileset.TopRightCorner;
        if (x > 0 && y > 0 && world[x - 1][y - 1] == Tileset.Grass) world[x - 1][y - 1] = Tileset.BottomLeftCorner;
        if (x < Main.WIDTH - 1 && y > 0 && world[x + 1][y - 1] == Tileset.Grass) world[x + 1][y - 1] = Tileset.BottomRightCorner;
    }


    private boolean isWall(TETile tile) {
        return tile == Tileset.LeftWall || tile == Tileset.RightWall ||
                tile == Tileset.TopWall || tile == Tileset.BottomWall ||
                tile == Tileset.TopLeftCorner || tile == Tileset.TopRightCorner ||
                tile == Tileset.BottomLeftCorner || tile == Tileset.BottomRightCorner ||
                tile == Tileset.LeftInnerCorner || tile == Tileset.LeftTopInnerCorner ||
                tile == Tileset.RightInnerCorner || tile == Tileset.RightTopInnerCorner ||
                tile == Tileset.SingleWall || tile == Tileset.SingleWallLeft ||
                tile == Tileset.SingleWallMiddle || tile == Tileset.SingleWallRight ||
                tile == Tileset.SingleWallVertical || tile == Tileset.DoubleWallTop ||
                tile == Tileset.DoubleWallBottom || tile == Tileset.WallAndTopLeftCorner ||
                tile == Tileset.WallAndTopRightCorner || tile == Tileset.BottomWallAndLeftCorner ||
                tile == Tileset.BottomWallAndRightCorner || tile == Tileset.WallAndLeftBottomCorner ||
                tile == Tileset.WallAndRightBottomCorner;
    }

}
