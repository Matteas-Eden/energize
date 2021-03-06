package model;

import Multiplayer.Server;
import javafx.scene.canvas.GraphicsContext;
import sample.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Character{
    protected Protagonist target; //For path finding.
    protected AnimationsState dieState;
    protected int APPLY_DAMAGE_COL;
    protected int EnemyMovementSpeed;
    protected long freezeStartTime, freezeDuration;
    protected long windSpellStartTime, windSpellDuration;
    protected long lastDirectionChangeTime = 0;


    public Enemy(int x, int y, BufferedImage image, int spriteWidth, int spriteHeight, int renderWidth, int renderHeight, int levelWidth) {
        super(x, y, image, spriteWidth, spriteHeight, renderWidth, renderHeight, levelWidth);
    }

    protected void updateTarget(Protagonist target) {
        this.target = target;
    }

    @Override
    protected void updateAnimationState() {
        if (this.playDieAnimation) {
            this.animationsState.copy(this.dieState);
            if (this.animationsState.isLastFrame(this.currentAnimationCol)) {
                this.playDieAnimation = false; //Once the animation has finished, set this to false to only play the animation once
                this.keepRendering = false;
                this.isAlive = false;
                Handler.removeEnemy(this);
                this.endGame();
            }
        }else if (this.playGotAttackedAnimation) { //Got Hit
            this.animationsState.copy(this.gotHitState);
            if (this.animationsState.isLastFrame(this.currentAnimationCol)) {
                this.playGotAttackedAnimation = false;
                this.onFire = false;
            }
        } else if (this.playAttackAnimation) {
            this.animationsState.copy(this.attackState);
            if (this.currentAnimationCol == APPLY_DAMAGE_COL) {
                Handler.attack(this); //Wait until part way through this animation before actually hitting
            }
            if (this.animationsState.isLastFrame(this.currentAnimationCol)) {
                this.playAttackAnimation = false;
                this.attackTimer = 0;
            }
        } else if (this.velocityX == 0 && this.velocityY == 0) { //Idle
            this.animationsState.copy(this.idleState);
        } else { //Walking
            this.animationsState.copy(runningState);
        }
    }

    public void freeze(long duration) {
        this.playAttackAnimation = false;
        this.freezeDuration = duration;
        this.freezeStartTime = System.currentTimeMillis();
        this.frozen = true;
    }

    public void blowAway(long duration) {
        this.playAttackAnimation = false;
        this.windSpellDuration = duration;
        this.windSpellStartTime = System.currentTimeMillis();
        this.blownAway = true;
    }

    protected void render(GraphicsContext graphicsContext, double cameraX, double cameraY) { //Here so the boss can override
        super.render(graphicsContext, cameraX, cameraY);
    }

    protected boolean proximity(Level level) {
        return false;
    }

    private void updateSpellEffect() {
        if (this.frozen && System.currentTimeMillis() - this.freezeStartTime > this.freezeDuration) { //This will wear off even if the player is in the pause menu
            this.frozen = false; //TODO: Add ice sprite to show frozen (fire for fire scroll also)
        }
        if (this.blownAway && System.currentTimeMillis() - this.windSpellStartTime > this.windSpellDuration) {
            this.blownAway = false;
        }
    }

    protected void setOnFire(){
        this.onFire = true;
    }

    protected void tick(double cameraX, double cameraY, Level level) {
        if (this.target.isOnline()) {
            return;
        }

        int currentNodeId = (int)(this.x / Game.PIXEL_UPSCALE) + (int)(this.y / Game.PIXEL_UPSCALE) * level.getLevelWidth();
        int targetNodeId = (int)(this.target.getX() / Game.PIXEL_UPSCALE) + (int)(this.target.getY() / Game.PIXEL_UPSCALE) * level.getLevelWidth();
        this.updateSpellEffect();
//        if (Game.getNextRandomInt() > 98) { //Can print out path periodically to show off path finding.
//            level.getShortestPath().findAndPrintPath(currentNodeId, targetNodeId);
//        }

        if (this.blownAway) { //Push enemy's away at 5x speed
            this.velocityX = this.EnemyMovementSpeed * (this.x > target.getX() ? 5 : -5);
            this.velocityY = this.EnemyMovementSpeed * (this.y > target.getY() ? 5 : -5);
        } else if (this.frozen) {
            this.velocityX = 0;
            this.velocityY = 0;
        } else if (this.proximity(level)) { //Only move if protagonist is close enough
            int nextDirection = level.getShortestPath().nextDirection(currentNodeId, targetNodeId); //1=up,2=right,3=down,4=left
            if (nextDirection < 5 && Game.getNextRandomInt() > 5) { //If the shortest path has been calculated, updated based on that. With a small chance of random movement to avoid getting stuck
                switch (nextDirection) {
                case 1: //up
                    this.velocityY = -this.EnemyMovementSpeed;
                    break;
                case 2://right
                    this.velocityX = this.EnemyMovementSpeed;
                    break;
                case 3://down
                    this.velocityY = this.EnemyMovementSpeed;
                    break;
                case 4://left
                    this.velocityX = -this.EnemyMovementSpeed;
                    break;
                }
            } else { //The shortest path has not been calculated yet, so move randomly
                this.randomMovement();
            }
        } else {
            this.randomMovement();
        }


        super.tick(cameraX, cameraY);

        if (this.target.client != null) { // only send if not Single player
            String comands = Server.PACKET_ENEMY_UPDATE + Server.PACKET_ID + this.getSpawnID().getValue() + Server.PACKET_LEVEL_NUMBER
                    + this.getSpawnID().getKey() + Server.PACKET_POSITION + this.x + "," + this.y + Server.PACKET_POSITION + this.target.id + Server.PACKET_END;
            this.target.sendToServer(comands);
        }
    }

    protected void serverUpdatePosition(final double newX, final double newY, final double cameraX, final double cameraY) {
        this.velocityX = (float)(newX - this.x);
        this.velocityY = (float)(newY - this.y);
        if (Math.abs((float)(newX - this.x)) > 100 || Math.abs((float)(newY - this.y)) > 100) {
            this.x = newX;
            this.y = newY;
        } else {
            super.tick(cameraX, cameraY);
        }
    }


    private void randomMovement() {
        if (System.currentTimeMillis() - lastDirectionChangeTime > (Game.getNextRandomInt() * 5 + 500)) { //Change direction every 0.5 - 1. second
            this.velocityX = Game.getNextRandomInt() < 50 ? this.EnemyMovementSpeed : -this.EnemyMovementSpeed;
            this.velocityY = Game.getNextRandomInt() < 50 ? this.EnemyMovementSpeed : -this.EnemyMovementSpeed;
            if (Game.getNextRandomInt() < 5) { //50% chance of stopping;
                this.velocityX = 0;
                this.velocityY = 0;
            }
            lastDirectionChangeTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void getHit(int damage) {
        if (!this.playDieAnimation && !this.playGotAttackedAnimation) {

            //getting hit interrupts an enemy attack. To not make it interrupt and continue after getting hit, dont set these. Dont not be able to take damage while attacking, check above
//            this.playAttackAnimation = false;
//            this.playSpecialAttackAnimation = false;


            this.animationsState.copy(this.gotHitState);
            super.getHit(damage);
            if (this.currHealth <= 0) { //dead
                //Make bounding box 0
                this.target.addEnergy(10);
                this.playGotAttackedAnimation = false;
                this.playDieAnimation = true;
                this.playDeathSound();
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        if (this.playDieAnimation) {
            return new Rectangle(0,0,0,0);
        } else {
            return super.getBounds();
        }
    }

    protected abstract void playDeathSound();

}
