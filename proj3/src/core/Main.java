package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final long SEED = 987654;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        fillWithSeveralRooms(world);
        ter.renderFrame(world);
    }

    public static double emptySpaceProportion(TETile[][] world){
        int count = 0;
        for (int x = 0; x < WIDTH; x ++){
            for (int y = 0; y < HEIGHT; y++){
                if (world[x][y] == Tileset.NOTHING){
                    count ++;
                }
            }
        }
        int area = HEIGHT * WIDTH;
        return count / area;
    }

    public static int generateRoomNumber(){
        List<Integer> list = new ArrayList<>();
        for (int i = 10; i <= 20; i++) {
            list.add(i);
        }
        Random rand = new Random();
        int randomelement = list.get(rand.nextInt(list.size()));
        System.out.println("Number of rooms" + randomelement);
        return randomelement;
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
        for (int i = 0; i < numberOfRooms; i++){
            fillWithRandomTiles(tiles);
        }
    }

    private static int randomTileHeight() {
        List<Integer> list = Arrays.asList(5, 6, 7,  8, 9, 10, 11);
        Random rand = new Random();
        int randomelement = list.get(rand.nextInt(list.size()));
        System.out.println(randomelement);
        return randomelement;
        }

    private static int randomTileWidth() {
        List<Integer> list = Arrays.asList(5, 6, 7,  8, 9, 10, 11);
        Random rand = new Random();
        int randomelement = list.get(rand.nextInt(list.size()));
        System.out.println(randomelement);
        return randomelement;
    }

    private static int chooseRoomCenterX() {
        List<Integer> list = new ArrayList<>();
        int end = WIDTH;
        int width = randomTileWidth();
        for (int i = width/2 + 3; i <= end - width/2 - 3; i++) {
            list.add(i);
        }
        Random rand = new Random();
        int randomelement = list.get(rand.nextInt(list.size()));
        System.out.println("Random X cootdinate" + randomelement);
        return randomelement;
    }

    private static int chooseRoomCenterY(){
        List<Integer> list = new ArrayList<>();
        int end = HEIGHT;
        int height = randomTileHeight();
        for (int i = height/2 + 3; i <= end - height/2 - 3; i++) {
            list.add(i);
        }
        Random rand = new Random();
        int randomelement = list.get(rand.nextInt(list.size()));
        System.out.println("Random Y cootdinate" + randomelement);
        return randomelement;
    }

    }


