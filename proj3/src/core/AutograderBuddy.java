package core;

// import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public static final int WIDTH = 65;
    public static final int HEIGHT = 55;

    public static TETile[][] getWorldFromInput(String input) {
        if (input.isEmpty()) {
            throw new RuntimeException("Input cannot be empty!");
        }

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.CustomNothing;
            }
        }

        char firstChar = input.charAt(0);
        if (firstChar == 'N') {
            int endIndex = input.indexOf('S');
            if (endIndex == -1) {
                throw new RuntimeException("Invalid input format.");
            }
            String seedString = input.substring(1, endIndex);
            long seed;
            try {
                seed = Long.parseLong(seedString);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid seed format.");
            }
            String movements = input.substring(endIndex + 1);

            Random rand = new Random(seed);
            Main.createWorld(world, rand);

            Game game = new Game(world, 0, seed);
            game.simulateMovements(movements);
            if (movements.endsWith(":Q")) {
                game.saveGame();
            }
        } else if (firstChar == 'L') {
            Game loadedGame = Game.loadGame();
            if (loadedGame == null) {
                throw new RuntimeException("No saved game found.");
            }
            String movements = input.substring(1);
            loadedGame.simulateMovements(movements);
            if (movements.endsWith(":Q")) {
                loadedGame.saveGame();
            }
            world = loadedGame.getWorld();
        } else {
            throw new RuntimeException("Invalid input format.");
        }

        return world;
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.CustomFloor.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character()
                || t.character() == Tileset.Floor.character()
                || t.character() == Tileset.FloorWithCoin.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.CustomWall.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character()
                || t.character() == Tileset.LeftWall.character()
                || t.character() == Tileset.RightWall.character()
                || t.character() == Tileset.TopWall.character()
                || t.character() == Tileset.BottomWall.character()
                || t.character() == Tileset.TopLeftCorner.character()
                || t.character() == Tileset.TopRightCorner.character()
                || t.character() == Tileset.BottomLeftCorner.character()
                || t.character() == Tileset.BottomRightCorner.character()
                || t.character() == Tileset.LeftInnerCorner.character()
                || t.character() == Tileset.LeftTopInnerCorner.character()
                || t.character() == Tileset.RightInnerCorner.character()
                || t.character() == Tileset.RightTopInnerCorner.character()
                || t.character() == Tileset.SingleWall.character()
                || t.character() == Tileset.SingleWallLeft.character()
                || t.character() == Tileset.SingleWallMiddle.character()
                || t.character() == Tileset.SingleWallRight.character()
                || t.character() == Tileset.SingleWallVertical.character()
                || t.character() == Tileset.DoubleWallTop.character()
                || t.character() == Tileset.DoubleWallBottom.character()
                || t.character() == Tileset.WallAndTopLeftCorner.character()
                || t.character() == Tileset.WallAndTopRightCorner.character()
                || t.character() == Tileset.BottomWallAndLeftCorner.character()
                || t.character() == Tileset.BottomWallAndRightCorner.character()
                || t.character() == Tileset.WallAndLeftBottomCorner.character()
                || t.character() == Tileset.WallAndRightBottomCorner.character();
    }
}
