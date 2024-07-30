package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenu {
    private int width;
    private int height;
    Color customPink = new Color(232, 127, 231);

    // Paths to the images
    private static final String MAIN_MENU_IMAGE = "proj3/src/core/game assets/Main Menu.png";
    private static final String SEED_SCREEN_IMAGE = "proj3/src/core/game assets/Seen Screen.png";
    private Font brickSansFont;

    //constructor
    public MainMenu(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        try {
            brickSansFont = Font.createFont(Font.TRUETYPE_FONT, new File("proj3/src/core/game assets/NTBrickSans.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(brickSansFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    //display main menu
    public void displayMenu() {
        StdDraw.clear();
        StdDraw.picture(this.width / 2, this.height / 2, MAIN_MENU_IMAGE);
        StdDraw.show();
    }

    //get string input from user
    public String getInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'N' || key == 'n') {
                    return "N" + collectSeed();
                }
                if (key == 'L' || key == 'l') {
                    return "L";
                }
                if (key == 'Q' || key == 'q') {
                    return "Q";
                }
            }
        }
    }

    //collect seed based on user input
    public String collectSeed() {
        StringBuilder typedString = new StringBuilder();
        drawSeedFrame(typedString.toString());
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char character = StdDraw.nextKeyTyped();
                if (Character.isDigit(character)) {
                    typedString.append(character);
                    drawSeedFrame(typedString.toString());
                } else if (character == 'S' || character == 's') {
                    break;
                }
            }
        }
        return typedString.toString();
    }

    //make the second frame after main where user inputs the seed
    public void drawSeedFrame(String seed) {
        StdDraw.clear();
        StdDraw.picture(this.width / 2, this.height / 2, SEED_SCREEN_IMAGE);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(brickSansFont.deriveFont(35f));
        StdDraw.text(this.width / 2, this.height / 2 - 1.5, seed);
        StdDraw.show();
    }
}



