package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    private static final String SPRITE_PATH = "proj3/src/core/game assets/";

    public static BufferedImage loadSprite(String filename) {
        BufferedImage sprite = null;
        try {
            File file = new File(SPRITE_PATH + filename);
            if (!file.exists()) {
                System.err.println("File not found: " + file.getAbsolutePath());
                return null;
            }
            sprite = ImageIO.read(file);
            System.out.println("Loaded sprite: " + filename);
        } catch (IOException e) {
            System.err.println("Error loading sprite: " + filename);
            e.printStackTrace();
        }
        return sprite;
    }

    public static String getSpriteFilePath(String prefix, Player.Direction direction) {
        switch (direction) {
            case UP:
                return SPRITE_PATH + prefix + "Back.PNG";
            case DOWN:
                return SPRITE_PATH + prefix + "Front.PNG";
            case LEFT:
                return SPRITE_PATH + prefix + "Left.PNG";
            case RIGHT:
                return SPRITE_PATH + prefix + "Right.PNG";
            default:
                return SPRITE_PATH + prefix + "Front.PNG";
        }
    }
}