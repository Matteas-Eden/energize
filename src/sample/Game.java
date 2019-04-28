package sample;

import FXMLControllers.EndScreenController;
import FXMLControllers.HUDController;
import FXMLControllers.MainMenuController;
import Multiplayer.Client;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

import javax.sound.sampled.Clip;
import java.util.Random;

public class Game extends Canvas {

    //For FPS / UPS counter
    private long time = 0;
    private int frames = 0;
    private int updates = 0;
    private KeyInput keyInput;

    //For game loop
    private AnimationTimer animationTimer;
    private long previousTime = System.nanoTime();
    private double delta = 0;
    private final double NS = 1000000000 / 60.0;

    //For handling UI
    private static AnchorPane root = new AnchorPane();
    public InGameMenuController inGameMenuController;

    private static Scene gameScene;
    private Camera camera;
    private Protagonist protagonist = null;
    private Map map;
    private Stage stage;
    public static final int SCALE = 1; //To scale the full game
    public static final int PIXEL_UPSCALE = 64 * Game.SCALE; //Place each tile, 1 tile width form the next.
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;
    private static Random randomMovement;//used for enemy movement.
    private static long randomSeed;
    private String onlineCommands = "";

    public static final int HUD_WIDTH = 200;
    private NewHUD hud;

    private MainMenuController controller;

    public Game(MainMenuController menuController, long _randomSeed) {
        //Setup the canvas
        super(Game.SCREEN_WIDTH,Game.SCREEN_HEIGHT);
        this.controller = menuController;
        controller.setGameActive(true);
        Handler.clearForNewGame();
        this.stage = Main.getStage();
        this.stage.setTitle("Tutorial Room");
        root = new AnchorPane();
        root.getChildren().add(this);
        gameScene = new Scene(root, SCREEN_WIDTH+HUD_WIDTH,SCREEN_HEIGHT, false);
        gameScene.getStylesheets().add(Main.class.getResource("/css/globalStyle.css").toExternalForm());
        randomSeed = _randomSeed;

        //////////////////Update the BGM////////////////////
        SoundController.changeMusic("gameBGM");

        stage.setScene(gameScene);
        EndScreenController.scoreSaved = false; //Only allow a new score to be saved if the a new game is started.

        stage.show();
        this.keyInput = new KeyInput(gameScene); //Keyboard inputs
        this.camera = new Camera(Game.SCREEN_WIDTH/2,Game.SCREEN_HEIGHT/2);
        randomMovement = new Random(randomSeed);

        //////////////////// Make the map /////////////////////////////////////
        this.map = new Map(this, randomSeed);
        this.map.loadLevel();

        //////////////////Load MenuElement//////////////////////
        inGameMenuController = new InGameMenuController(protagonist.getInventory(),()->unpause(),exitToTitleScreenEvent-> {
                    Handler.disconnectFromServer();
            stage.setScene(Main.getMainScene());
            SoundController.changeMusic("titleBGM");
        });
        inGameMenuController.AddMenusToRoot(root);

        //////////////////////Load HUD//////////////////////////
        this.hud = protagonist.getNewHud();
        this.root.getChildren().add(hud);
        this.hud.show();

        init(); //Setup game loop
        Handler.setCamera(this.camera);
        Handler.setMap(this.map);
        Handler.timeline.setCycleCount(Animation.INDEFINITE);
        Handler.timeline.play();
        Handler.setGame(this);
    }

    public void addClient(Client client) {
        this.protagonist.addClient(client);
    }

    public void setOnlineCommand(String command) {
        this.onlineCommands = command;
    }


    public void hidePauseMenu () {
        this.inGameMenuController.hidePauseMenu();
    }

    public static int getNextRandomInt() {
        return randomMovement.nextInt(100);
    }

    public void start(){
        this.animationTimer.start();
    }

    public void pause(){
        this.animationTimer.stop();
        Handler.timeline.pause();
    }

    public void unpause(){
        this.stage.setScene(gameScene);
        this.previousTime = System.nanoTime(); // Reset the previous to now, so it doesnt make up for all the ticks that where missed in pause state.
        this.animationTimer.start();
        Handler.timeline.play();
    }

    public KeyInput getKeyInput() {
        return this.keyInput;
    }

    private void init() {
        Stage stage1 = this.stage; //Need to make a local copy to use inside the handle method
//        String stageName = stage1.getTitle(); //To keep the base name
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) { //This gets called 60 times per second
                time += now - previousTime;
                //Every second, display how many ticks have occurred and frames have been rendered.
                if (time >= 1000000000.0f) {
                    String timeRemaining = protagonist.updateTimer();
                    stage1.setTitle("Time Played: " + timeRemaining + " | "+ frames + " FPS | " + updates + " UPS");
//                    stage1.setTitle(stageName + " | " + frames + " FPS | " + updates + " UPS | Time Played: " + timeRemaining);
                    time = 0;
                    frames = 0;
                    updates = 0;
                }

                delta += (now - previousTime) / NS;//Update the actual time that has passed since the last update
                previousTime = now;
                while (delta >= 1) { //If delta is < 1 this means frames have been missed so dont update so many times.
                    // To stick to 60 updates per second despite different hardware.
                    tick(); //Advance all game logic a step
                    delta--;
                    updates++;
                }
                frames++;
                render(); //Draw everything to the screen. This is uncapped and varies based on the hardware.
            }
        };
    }

    private void tick() {
        if (keyInput.getKeyPressDebounced("pause") || keyInput.getKeyPressDebounced("quit")){
            this.pause();
            inGameMenuController.showPauseMenu();
            System.out.println("Toggle game pause");
        }
        if (keyInput.getKeyPressDebounced("inventory")){
            this.pause();
            inGameMenuController.showInventoryMenu();
            System.out.println("Open inventory");
            //System.out.println(this.protagonist.getInventory().getItemCount());
        }
        Handler.tick(this.camera.getX(), this.camera.getY(),this.keyInput, this.onlineCommands);
        if (this.protagonist != null) { //Make sure there is a protagonist to pan towards
            this.camera.tick(this.protagonist, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT,
                    this.map.getCurrentLevelWidth() * PIXEL_UPSCALE, this.map.getCurrentLevelHeight() * PIXEL_UPSCALE);
        }
    }

    private void render() {

        GraphicsContext graphicsContext = this.getGraphicsContext2D();
        //Translate the to where the camera is looking for proper coordinates.
        graphicsContext.translate(-this.camera.getX(), -this.camera.getY());

        Handler.render(graphicsContext, this.camera.getX(), this.camera.getY());

        //Translate back
        graphicsContext.translate(this.camera.getX(), this.camera.getY());

    }

    public void setProtagonist (Protagonist protagonist) {
        this.protagonist = protagonist;
        root.getChildren().add(protagonist.getNewHud());
        Handler.setProtagonist(protagonist);
    }

    public Protagonist getProtagonist () {
        return this.protagonist;
    }

    public void addPlayer(int id) {
        OnlinePlayer temp = new OnlinePlayer((int)this.protagonist.getX()/Game.PIXEL_UPSCALE, (int)this.protagonist.getY()/Game.PIXEL_UPSCALE, PreLoadedImages.protagonistSpriteSheet,
        Level.PROTAGONIST_SPRITE_WIDTH, Level.PROTAGONIST_SPRITE_HEIGHT, (int) (Level.PROTAGONIST_SPRITE_WIDTH * Game.SCALE * Level.PROTAGONIST_SPRITE_SCALE),
        (int) (Level.PROTAGONIST_SPRITE_HEIGHT * Game.SCALE * Level.PROTAGONIST_SPRITE_SCALE), this.map.getCurrentLevelWidth());
        temp.setId(id);
        temp.updateLevelNumber(this.map.getCurrentLevelNumber());
        temp.updateLevelWidth(this.map.getCurrentLevelWidth());
        Handler.addPlayer(temp);
        Handler.updateEnemyTarget();
    }

    public Map getMap() {
        return this.map;
    }

    public static long getRandomSeed() {
        return randomSeed;
    }

    public static void setRandomSeed(long seed) {
        randomSeed = seed;
    }

    public static AnchorPane getRoot() {
        return root;
    }
}
