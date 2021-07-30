package org.dionthorn;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Texture {

    private final Image image;
    private final int width;
    private final int height;

    public Texture(String filename) {
        this.image = new Image(getClass().getResource("/Textures/" + filename).toString());
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
    }

    public Color getPixelColor(int u, int v) {
        return getPixelReader().getColor(u, v);
    }

    public PixelReader getPixelReader() {
        return image.getPixelReader();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
