package model;

import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Protagonist extends Character {
    private static int nextID = 0; //Unique id for all characters, this will be used for multilayer
    protected int id;
    private int lives; //Keep track of how many lives, Can pick up hearts which increase this. 0 = dead.
    //private KeyInput keyInput; //The keyboard inputs to move the character.
    private boolean buttonAlreadyDown = false; //To only update animation state on button initial press, not on hold.
    //The different animation states to hold the borders and which sprite from sprite sheet to use.
    private AnimationsState runningState;
    private AnimationsState idleState;
    private AnimationsState attackState;
    private AnimationsState gotHitState;

    //    public Inventory inventory;

    public Protagonist(int x, int y, BufferedImage image, int spriteWidth, int spriteHeight, int renderWidth, int renderHeight, int levelWidth) {
        super(x, y, image, spriteWidth, spriteHeight, renderWidth, renderHeight, levelWidth);
        this.id = nextID++; //Give each protagonist a unique id. (Will be used for multilayer)
        //this.keyInput = keyInput;

        //Set up the bounding boxes and sprite selection for the different animation options.
        this.idleState = new AnimationsState(45,48,17, 5, 3, 0, 0);
        this.runningState = new AnimationsState(52,38,20,5, 6, 1, 1);
//        this.attackState = new AnimationsState(45,0,0,5,6,6,0);
        this.attackState = new AnimationsState(45,48,17,5,6,6,0);
        this.gotHitState = new AnimationsState(0,0,0,0,6,9,0); //Place holder till get hit sprite

        this.jfxImage = SwingFXUtils.toFXImage(this.spriteSheet.getSprite(0,0), null); //Initialise image for first animation
    }

    @Override
    protected void attack() {
        this.animationsState.copy(this.attackState); //Set the state to update the bounding boxes
        super.attack();
        Handler.attack(this);
    }

    @Override
    void playSound() {
        System.out.println("Beep");
    }

    @Override
    protected void getHit() {
        this.animationsState.copy(this.gotHitState);
        super.getHit();
        if (this.currHealth <= 0) { //died
            this.playGotAttackedAnimation = false;
            this.playDieAnimation = true; //Can leave other play animation booleans true as die has implicit priority when checking.
        }
    }

    @Override
    void updateAnimationState() {
        //Determine what state the player is in, and update the animation accordingly.
        //IMPLICIT PRIORITY. ORDER = DIE, ATTACKING, GotHit, IDLE/RUNNING
        //After die animation last frame, fade out ...Game over
        if (this.playAttackAnimation) { //Attacking
            //Update attack animation
            this.animationsState.copy(this.attackState);
            if (this.animationsState.isLastFrame(this.currentAnimationCol)) {
                this.playAttackAnimation = false; //Once the animation has finished, set this to false to only play the animation once
            }
        }else if (this.playGotAttackedAnimation) {
            this.animationsState.copy(this.gotHitState);
            if (this.animationsState.isLastFrame(this.currentAnimationCol)) {
                this.playGotAttackedAnimation = false; //Once the animation has finished, set this to false to only play the animation once
            }
        } else if (this.velocityX == 0 && this.velocityY == 0) { //Idle
            this.animationsState.copy(this.idleState);
        } else { //Running (As this is the only other option)
            this.animationsState.copy(runningState);
        }
    }


    public void tick(double cameraX, double cameraY, KeyInput keyInput) {
        //Update the velocity according to what keys are pressed.
        //If the key has just been pressed, update the animation. This leads to more responsive animations.
        if(this.playGotAttackedAnimation || this.playDieAnimation || this.playAttackAnimation) { //If the player is in an animation, disable movement
            this.velocityX = 0;
            this.velocityY = 0;
        } else {
            if (keyInput.getKeyPressed("up")) this.velocityY = -5;
            else if (!keyInput.getKeyPressed("down")) this.velocityY = 0;

            if (keyInput.getKeyPressed("down")) this.velocityY = 5;
            else if (!keyInput.getKeyPressed("up")) this.velocityY = 0;

            if (keyInput.getKeyPressed("right")) {
                this.velocityX = 5;
                if (!this.buttonAlreadyDown) {
                    this.updateSprite();
                    this.buttonAlreadyDown = true;
                }

            } else if (!keyInput.getKeyPressed("left")) {
                this.velocityX = 0;
                if (this.buttonAlreadyDown) {
                    this.updateSprite();
                    this.buttonAlreadyDown = false;
                }
            }

            if (keyInput.getKeyPressed("left")) {
                this.velocityX = -5;
                if (!this.buttonAlreadyDown) {
                    this.updateSprite();
                    this.buttonAlreadyDown = true;
                }
            } else if (!keyInput.getKeyPressed("right")) {
                this.velocityX = 0;
                if (this.buttonAlreadyDown) {
                    this.updateSprite();
                    this.buttonAlreadyDown = false;
                }
            }
        }

        if (keyInput.getKeyPressed("attack")){
            this.attack();
        }

        if (keyInput.getKeyPressed("jump")){
            System.out.println("Jump for joy");// Using this to make it easier to custom add key bindings later
        }

        if (keyInput.getKeyPressed("useItem")){
            System.out.println("Using an item");
        }

        if (keyInput.getKeyPressed("useSpecial")){
            System.out.println("Azarath, metrion, zinthos!"); //Outdated reference
        }

        if (keyInput.getKeyPressed("cheatKey")){
            System.out.println("Wow, cheating in 2019?");
        }

        if (keyInput.getKeyPressed("pause")){
            //Set a pause game flag true
        }

        if (keyInput.getKeyPressed("quit")) {
            System.exit(0);
        }
        super.tick(cameraX,cameraY); //Check collisions and update x and y

    }

    @Override
    public Rectangle getBounds() {
        return super.getBounds();
    }

    protected void GetHit(){
        System.out.println("I got hit!");
        this.playGotAttackedAnimation = true;
        if (this.currHealth <= 0) { //loose life
            this.lives --;
            if (this.lives <= 0) { //died
                this.playDieAnimation = true; //Can leave other play animation booleans true as die has implicit priority when checking.
            }
        }
    }

}

