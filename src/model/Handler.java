package model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import sample.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Handler { //This class will hold all the game objects and is responsible for rendering each one
    private static ArrayList<GameObject> gameObjects = new ArrayList<>();

    //Use a timeline instead of render, or tick methods to control the speed of the animation
    public static Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
        for (GameObject object: gameObjects) {
            object.updateSprite();
        }
    }));


    public static void tick() {
        for (GameObject object : gameObjects) {
            object.tick();
        }
    }

    public static void render(GraphicsContext graphicsContext){
        for (GameObject object : gameObjects) {
            object.render(graphicsContext);
        }
    }

    public static void addObject(GameObject object) {
        gameObjects.add(object);

    }

    public static void removeObject(GameObject object) {
        //Dont need to check if it contains the object because if it doesnt exist, it doesnt throw error.
        gameObjects.remove(object);
    }

    public static boolean checkCollision (GameObject character) {
        for (GameObject object : gameObjects) {
            if (!object.equals(character) && character.getBounds().intersects(object.getBounds())) {
                return true;
            }
        }
        return false;
    }
}
