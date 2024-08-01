package core;

import edu.princeton.cs.algs4.StdDraw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenu {
    private int width;
    private int height;
    Color customPink = new Color(232, 127, 231);

    // Paths to the images
    private static final String MAIN_MENU_IMAGE = "proj3/src/core/game assets/Main Menu.png";
    private static final String SEED_SCREEN_IMAGE = "proj3/src/core/game assets/Seen Screen.png";
    private static final String CHARACTER_SELECTION_IMAGE = "proj3/src/core/game assets/CharacterSelection.png";

    //constructor
    public MainMenu(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setFont(FontManager.getBrickSansFont(12f));
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
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
                    String seed = "N" + collectSeed();
                    int characterChoice = selectCharacter();
                    return seed + ":" + characterChoice;
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
        StdDraw.picture(this.width / 2.0, this.height / 2.0, SEED_SCREEN_IMAGE);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(FontManager.getBrickSansFont(35f));
        StdDraw.text(this.width / 2.0, this.height / 2.0 - 1.5, seed);
        StdDraw.show();
    }

    // Character selection screen
    public int selectCharacter() {
        StdDraw.clear();
        StdDraw.picture(this.width / 2, this.height / 2, CHARACTER_SELECTION_IMAGE);
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '1') {
                    return 1;
                }
                if (key == '2') {
                    return 2;
                }
            }
        }
    }
}