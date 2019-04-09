package model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Protagonist extends Character {
    private static int nextID; //Unique id for all characters, this will be used for multiplayer
    protected int id;
    private int lives;
    private KeyInput keyInput;
    private boolean attacking = false;
    private boolean buttonAllreadyDown = false;
    private AnimationsState runningBorder;
    private AnimationsState idleBorder;
    private AnimationsState attackBorder;
    //    public Inventory inventory;

    public Protagonist(int x, int y, BufferedImage image, int spriteSheetWidth, int spriteSheetHeight, int renderWidth, int renderHeight, KeyInput keyInput) {
        super(x, y, image, spriteSheetWidth, spriteSheetHeight, renderWidth, renderHeight);
        this.id = nextID++;
        this.keyInput = keyInput;

        this.idleBorder = new AnimationsState(45,48,17, 5);
        this.runningBorder = new AnimationsState(52,38,20,5);
//        this.attackBorder = new AnimationsState();

        this.jfxImage = SwingFXUtils.toFXImage(this.spriteSheet.getSprite(0,0), null); //Initialise image for first animation
    }

    @Override
    protected void updateSprite() {

        //Update the direction, Do this first so vertical movement gets the updated bounding box
        if (this.velocityX > 0) {//right
            this.spriteDirection = 1;
        } else if (this.velocityX < 0) {//left
            this.spriteDirection = -1;
        } //other case is idle so leave the direction how it how it was before

        if (this.attacking) { //Attacking
            //Update attack animation
//            this.animationsState.copy(attackBorder);
            this.attacking = false; //Once the animation has finished, set this to false to only play the animation once

        } else if (this.velocityX == 0 && this.velocityY == 0) { //Idle
            //Update bounding box
            this.animationsState.copy(this.idleBorder);

            //Update idle sprite
            this.animationRow = 0;
            this.animationMaxCol = 3;
            if (this.animationCol < this.animationMaxCol) {
                this.animationCol++;
            } else {
                this.animationCol = 0;
            }
        } else { //Running
            this.animationsState.copy(runningBorder);
            //Update running animation sprite
            this.animationRow = 1;
            this.animationMaxCol = 6;
            if (this.animationCol < this.animationMaxCol) {
                this.animationCol++;
            } else {
                this.animationCol = 1;
            }
        }

        if (this.spriteDirection == -1) { //If the character is facing left, swap the left and right borders
            this.animationsState.flipBoundingBoxX();
        }

        this.jfxImage = SwingFXUtils.toFXImage(this.spriteSheet.getSprite(this.animationCol,this.animationRow), null);

    }

    @Override
    void attack() {

    }

    @Override
    void playSound() {

    }

    @Override
    void getHit() {

    }


    public void tick(double cameraX, double cameraY) {
        if (this.keyInput.up) this.velocityY = -5;
        else if(!this.keyInput.down) this.velocityY = 0;

        if (this.keyInput.down) this.velocityY = 5;
        else if(!this.keyInput.up) this.velocityY = 0;

        if (this.keyInput.right) {
            this.velocityX = 5;
            if (!this.buttonAllreadyDown) {
                this.updateSprite();
                this.buttonAllreadyDown = true;
            }

        } else if(!this.keyInput.left) {
            this.velocityX = 0;
            if (this.buttonAllreadyDown) {
                this.updateSprite();
                this.buttonAllreadyDown = false;
            }
        }

        if (this.keyInput.left) {
            this.velocityX = -5;
            if (!this.buttonAllreadyDown) {
                this.updateSprite();
                this.buttonAllreadyDown = true;
            }
        } else if(!this.keyInput.right) {
            this.velocityX = 0;
            if (this.buttonAllreadyDown) {
                this.updateSprite();
                this.buttonAllreadyDown = false;
            }
        }

        super.tick(cameraX,cameraY);

    }

    @Override
    public void render(GraphicsContext graphicsContext, double cameraX, double cameraY) {

        if (this.inCameraBounds(cameraX,cameraY)) {
            if (this.spriteDirection == 1) { //facing right
                graphicsContext.drawImage(this.jfxImage, this.x, this.y, this.spriteWidth, this.spriteHeight);
            } else {
                graphicsContext.drawImage(this.jfxImage, this.x + this.spriteWidth, this.y, -this.spriteWidth, this.spriteHeight);
            }
            this.renderBoundingBox(graphicsContext);
        }
    }

    @Override
    public Rectangle getBounds() {
        return super.getBounds();
    }

    protected void GetHit(){
        System.out.println("I got hit!");
    }

}

