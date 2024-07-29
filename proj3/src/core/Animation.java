package core;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int frameIndex;
    private int frameCount;
    private int frameDuration;
    private int frameTicker;
    private boolean isRunning;

    public Animation(BufferedImage[] frames, int frameDuration) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        frameIndex = 0;
        frameCount = frames.length;
        frameTicker = 0;
        isRunning = false;
    }

    public void update() {
        if (!isRunning) return;

        frameTicker++;

        if (frameTicker >= frameDuration) {
            frameTicker = 0;
            frameIndex = (frameIndex + 1) % frameCount;
        }
    }

    public BufferedImage getSprite() {
        return frames[frameIndex];
    }

    public void start() {
        isRunning = true;
        frameIndex = 0;
        frameTicker = 0;
    }

    public void stop() {
        isRunning = false;
    }

    public void reset() {
        frameIndex = 0;
        frameTicker = 0;
    }
}