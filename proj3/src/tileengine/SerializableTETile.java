package tileengine;

import tileengine.TETile;

import java.io.Serializable;
import java.awt.Color;

public class SerializableTETile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final char character;
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;
    private final int id;

    public SerializableTETile(TETile tile) {
        this.character = tile.character();
        this.textColor = tile.getTextColor();
        this.backgroundColor = tile.getBackgroundColor();
        this.description = tile.description();
        this.filepath = tile.getFilePath(); // Ensure getter method is used
        this.id = tile.id();
    }

    public TETile toTETile() {
        return new TETile(character, textColor, backgroundColor, description, filepath, id);
    }
}