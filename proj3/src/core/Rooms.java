package core;

public class Rooms {
    int CenterX;
    int CenterY;
    int height;
    int width;


    public Rooms(int centerX, int CenterY, int height, int width){
        this.CenterX = centerX;
        this.CenterY = CenterY;
        this.height = height;
        this.width = width;
    }

    public int getCenterX() {
        return this.CenterX;
    }
    public int getCenterY() {
        return this.CenterY;
    }
    public int getHeight() {
        return this.height;

    }public int getWidth() {
        return this.width;
    }


}
