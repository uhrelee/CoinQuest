package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class MainMenu {
    private int width;
    private int height;

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
    }

    public void DisplayMenu() {
        StdDraw.clear(Color.PINK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height - 10, "CS61B: Best Game =^.^=");
        StdDraw.text(this.width / 2, this.height / 2, "New Game (N)");
        StdDraw.text(this.width / 2, this.height / 2 - 5, "Load Game (L)");
        StdDraw.text(this.width / 2, this.height / 2 - 10, "Quit (Q)");
        StdDraw.show();
    }

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

    public String collectSeed() {
        StringBuilder typedString = new StringBuilder();
        drawSeedFrame(typedString.toString());
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char character = StdDraw.nextKeyTyped();
                if (Character.isDigit(character)){
                    typedString.append(character);
                    drawSeedFrame(typedString.toString());
                } else if (character == 'S' || character == 's'){
                    break;
                }
            }
        }
        return typedString.toString();
    }

    public void drawSeedFrame(String seed) {
        StdDraw.clear(Color.PINK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2 + 5, "Enter Seed:");
        StdDraw.text(this.width / 2, this.height / 2 , seed);
        StdDraw.text(this.width / 2, this.height / 2 - 5, "Press 'S' to Start");
        StdDraw.show();
    }
}



