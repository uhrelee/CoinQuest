package tileengine;

import java.awt.Color;
import java.io.Serializable;

public class SerializableTETile implements Serializable {
    private char character;
    private Color textColor;
    private Color backgroundColor;
    private String description;
    private String filepath;
    private int id;

    public SerializableTETile(TETile tile) {
        this.character = tile.character();
        this.textColor = tile.getTextColor();
        this.backgroundColor = tile.getBackgroundColor();
        this.description = tile.description();
        this.filepath = tile.getFilePath();
        this.id = tile.getId();
    }

    public TETile toTETile() {
        return new TETile(character, textColor, backgroundColor, description, filepath, id);
    }
}
