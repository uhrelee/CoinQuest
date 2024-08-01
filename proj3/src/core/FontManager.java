package core;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class FontManager {
    private static Font brickSansFont;

    public static Font getBrickSansFont(float size) {
        if (brickSansFont == null) {
            try {
                File fontFile = new File("src/core/game assets/NTBrickSans.ttf");
                if (!fontFile.exists()) {
                    throw new IOException("Font file not found: " + fontFile.getAbsolutePath());
                }
                brickSansFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(brickSansFont);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
                // Fallback to a default font
                brickSansFont = new Font("Serif", Font.PLAIN, (int) size);
            }
        }
        return brickSansFont;
    }
}
