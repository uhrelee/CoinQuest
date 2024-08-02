package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", 0);
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", 1);
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);
    public static final TETile CustomNothing = new TETile(' ', Color.black, Color.black, "CustomNothing", "proj3/src/core/game assets/grass2.png", 15);
    public static final TETile CustomFloor = new TETile('.', new Color(128, 192, 128), Color.black, "customGrass", "proj3/src/core/game assets/floor5.png", 13);
    public static final TETile CustomWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "customWall", "proj3/src/core/game assets/wall3.png", 14);
    public static final TETile CustomTree = new TETile(' ', Color.black, Color.black, "CustomNothing", "proj3/src/core/game assets/tree.png", 15);
    public static final TETile CustomBorder = new TETile(' ', Color.black, Color.black, "CustomNothing", "proj3/src/core/game assets/border.png", 15);
    public static final TETile CustomFlower = new TETile(' ', Color.black, Color.black, "CustomNothing", "proj3/src/core/game assets/flower.png", 15);
    public static final TETile Grass = new TETile(' ', Color.black, Color.black, "Grass", "proj3/src/core/game assets/grass.PNG", 15);
    public static final TETile Floor = new TETile('.', new Color(128, 192, 128), Color.black, "Floor", "proj3/src/core/game assets/floor.PNG", 13);
    public static final TETile LeftWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Left Wall", "proj3/src/core/game assets/left wall.PNG", 14);
    public static final TETile RightWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Right Wall", "proj3/src/core/game assets/right wall.PNG", 14);
    public static final TETile TopRightCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Top Right Corner", "proj3/src/core/game assets/top right outside corner.PNG", 14);
    public static final TETile TopLeftCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Top Left Corner", "proj3/src/core/game assets/top left outside corner.PNG", 14);
    public static final TETile BottomLeftCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Bottom Left Corner", "proj3/src/core/game assets/left bottom outside corner.PNG", 14);
    public static final TETile BottomRightCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Bottom Right Corner", "proj3/src/core/game assets/right bottom corner.PNG", 14);
    public static final TETile TopWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Top Wall", "proj3/src/core/game assets/top wall.PNG", 14);
    public static final TETile BottomWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Bottom Wall", "proj3/src/core/game assets/bottom wall.PNG", 14);
    public static final TETile LeftInnerCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Left Inner Corner", "proj3/src/core/game assets/left inner corner.PNG", 14);
    public static final TETile LeftTopInnerCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Left Top Inner Corner", "proj3/src/core/game assets/left top inner corner.PNG", 14);
    public static final TETile RightInnerCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Right Inner Corner", "proj3/src/core/game assets/right inner corner.PNG", 14);
    public static final TETile RightTopInnerCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Right Top Inner Corner", "proj3/src/core/game assets/right top inner corner.PNG", 14);
    public static final TETile SingleWall = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall", "proj3/src/core/game assets/single wall.PNG", 14);
    public static final TETile SingleWallLeft = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Left", "proj3/src/core/game assets/single wall left.PNG", 14);
    public static final TETile SingleWallMiddle = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Middle", "proj3/src/core/game assets/single wall middle.PNG", 14);
    public static final TETile SingleWallRight = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Right", "proj3/src/core/game assets/single wall right.PNG", 14);
    public static final TETile SingleWallVertical= new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Right", "proj3/src/core/game assets/single wall vertical.PNG", 14);
    public static final TETile DoubleWallTop = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Right Top Inner Corner", "proj3/src/core/game assets/double wall top.PNG", 14);
    public static final TETile DoubleWallBottom = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall", "proj3/src/core/game assets/double wall bottom.PNG", 14);
    public static final TETile WallAndTopLeftCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Left", "proj3/src/core/game assets/wall and top left corner.PNG", 14);
    public static final TETile WallAndTopRightCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Middle", "proj3/src/core/game assets/wall and top right corner.PNG", 14);
    public static final TETile BottomWallAndLeftCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Right", "proj3/src/core/game assets/bottom wall and left corner.PNG", 14);
    public static final TETile BottomWallAndRightCorner= new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Right", "proj3/src/core/game assets/bottom wall and right corner.PNG", 14);
    public static final TETile WallAndLeftBottomCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Left", "proj3/src/core/game assets/wall and left corner.PNG", 14);
    public static final TETile WallAndRightBottomCorner = new TETile('#', new Color(216, 128, 128), Color.darkGray, "Single Wall Middle", "proj3/src/core/game assets/wall and right corner.PNG", 14);
    public static final TETile FloorWithCoin = new TETile('.', new Color(128, 192, 128), Color.black, "FloorWithCoin", "proj3/src/core/game assets/FloorCoin.PNG", 20);
}


