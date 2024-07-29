package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    private static BufferedImage spriteSheet;
    private static final int TILE_SIZE = 16;
    private static final int SHEET_WIDTH = 3;
    private static final int SHEET_HEIGHT = 4;

    public static BufferedImage loadSprite(String file) {
        BufferedImage sprite = null;
        try {
            sprite = ImageIO.read(new File("proj3/src/core/game assets/Sprite Sheet.PNG"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sprite;
    }

    public static BufferedImage getSprite(int row, int col) {
        if (spriteSheet == null) {
            spriteSheet = loadSprite("Sprite Sheet");
        }
        return spriteSheet.getSubimage(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
}