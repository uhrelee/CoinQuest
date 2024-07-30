package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    private static BufferedImage spriteFront, spriteBack, spriteLeft, spriteRight;
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

    public static BufferedImage getFrontSprite() {
        if (spriteFront == null) {
            spriteFront = loadSprite("SpriteFront.PNG");
        }
        return spriteFront;
    }

    public static BufferedImage getBackSprite() {
        if (spriteBack == null) {
            spriteBack = loadSprite("SpriteBack.PNG");
        }
        return spriteBack;
    }

    public static BufferedImage getLeftSprite() {
        if (spriteLeft == null) {
            spriteLeft = loadSprite("SpriteLeft.PNG");
        }
        return spriteLeft;
    }

    public static BufferedImage getRightSprite() {
        if (spriteRight == null) {
            spriteRight = loadSprite("SpriteRight.PNG");
        }
        return spriteRight;
    }

    public static String getSpriteFilePath(Player.Direction direction) {
        switch (direction) {
            case UP:
                return SPRITE_PATH + "SpriteBack.PNG";
            case DOWN:
                return SPRITE_PATH + "SpriteFront.PNG";
            case LEFT:
                return SPRITE_PATH + "SpriteLeft.PNG";
            case RIGHT:
                return SPRITE_PATH + "SpriteRight.PNG";
            default:
                return SPRITE_PATH + "SpriteFront.PNG";
        }
    }
}