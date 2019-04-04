package model;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {


    protected int x,y;
    protected boolean isSolid = true;
    protected boolean movable = true;
    protected int width = 32, height = 32; //size of sprite
    protected SpriteSheet spriteSheet;
    public GameObject(int x, int y, boolean scale, BufferedImage image) { //Scale is true when loading from map image
        if (scale) {
            this.x = x * this.width;
            this.y = y * this.height;
        } else {
            this.x = x;
            this.y = y;
        }
        this.loadSpriteSheet(image);
    }


    public abstract void tick();
    public abstract void render(GraphicsContext graphicsContext);
    protected Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    protected int getWidth() {
        return this.width;
    }

    protected int getHeight() {
        return this.height;
    }

    protected abstract void loadSpriteSheet(BufferedImage image);

}
