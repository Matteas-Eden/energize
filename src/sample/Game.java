package sample;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

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
    private boolean isPaused = false;
    private boolean pauseButtonDown = false;

    //For handling UI
    private Group root;
    private Menu pauseMenu;
    private Menu inventoryMenu;

    private Camera camera;
    private Protagonist protagonist = null;
    private Map map;
    private Stage stage;
    public static final int SCALE = 1; //To scale the full game
    public static final int PIXEL_UPSCALE = 64 * Game.SCALE; //Place each tile, 1 tile width form the next.
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;
    private static Random random = new Random(1);//used for enemy movement and map generation.

    public Game() {
        //Setup the canvas
        super(Game.SCREEN_WIDTH,Game.SCREEN_HEIGHT);
        stage = Main.getStage();
        stage.setTitle("Tutorial Room");
        root = new Group();
        root.getChildren().add(this);
        Scene scene = new Scene(root, SCREEN_WIDTH,SCREEN_HEIGHT, false);
        scene.getStylesheets().add(Main.class.getResource("/css/globalStyle.css").toExternalForm());


        /*===========================================\
        * pause Menu
        */

        pauseMenu = new Menu("pauseMenu",300,300,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);
        inventoryMenu = new Menu("inventoryMenu",500,500,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        //Pause menu controls
        Label pauseTitle = new Label("pause Menu");
        pauseTitle.setId("pauseMenuTitle");

        Button resumeButton = new Button("Resume");
        resumeButton.setOnMouseClicked(mouseEvent -> {
            pauseMenu.hide();
            unpause();
        });
        resumeButton.setPrefSize(250,50);
        Button returnToMainMenuButton = new Button("Quit to Title Screen");
        returnToMainMenuButton.setOnMouseClicked(mouseEvent -> {
            stage.setScene(Main.getMainScene()); //Buggy
        });
        returnToMainMenuButton.setPrefSize(250,50);
        Button exitGameButton = new Button("Quit to Desktop");
        exitGameButton.setOnMouseClicked(mouseEvent -> System.exit(0));
        exitGameButton.setPrefSize(250,50);

        //Setting up VBox
        VBox pauseGameVBox = new VBox(10,pauseTitle,resumeButton,returnToMainMenuButton,exitGameButton);
        pauseGameVBox.setAlignment(Pos.CENTER);

        pauseMenu.getChildren().add(pauseGameVBox);

        //pauseMenu = createPauseMenu();
        root.getChildren().add(pauseMenu);
        root.getChildren().add(inventoryMenu);
        pauseMenu.hide();
        /*===================================*/
        stage.setScene(scene);

        init(); //Setup game loop
        stage.show();
        this.keyInput = new KeyInput(scene); //Keyboard inputs
        this.camera = new Camera(0,0);
        this.map = new Map(this);
        this.map.loadLevel(0);
        Handler.setCamera(this.camera);
        Handler.setMap(this.map);
        Handler.timeline.setCycleCount(Animation.INDEFINITE);
        Handler.timeline.play();
    }

    public static int getNextRandomInt(int bounds) {
        return random.nextInt(bounds);
    }

    public void start(){
        this.animationTimer.start();
    }

    private void pause(){
        this.animationTimer.stop();
        Handler.timeline.pause();
    }

    private void unpause(){
        this.animationTimer.start();
        Handler.timeline.play();
    }

    public KeyInput getKeyInput() {
        return this.keyInput;
    }

    private void init() {
        Stage stage1 = this.stage; //Need to make a local copy to use inside the handle method
        String stageName = stage1.getTitle(); //To keep the base name
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) { //This gets called 60 times per second
                time += now - previousTime;
                //Every second, display how many ticks have occurred and frames have been rendered.
                if (time >= 1000000000.0f) {
                    stage1.setTitle(stageName + " | " + frames + " FPS | " + updates + " UPS");
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
            pauseMenu.show();
            //pauseMenu.setVisible(true);
            System.out.println("Toggle game pause");
        }
        if (keyInput.getKeyPressDebounced("inventory")){
            this.pause();
            //ShowInventoryMenu();
            System.out.println("Open inventory");
        }
        Handler.tick(this.camera.getX(), this.camera.getY(),this.keyInput);
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
    }

    public Protagonist getProtagonist () {
        return this.protagonist;
    }

    public Map getMap() {
        return this.map;
    }

    private void ShowInventoryMenu(){;}
    private void HideInventoryMenu(){;}
}
