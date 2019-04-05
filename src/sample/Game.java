package sample;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;

public class Game extends Canvas {

    private AnimationTimer animationTimer;
    private long previousTime = System.nanoTime();
    private long time = 0; //For fps counter
    private int frames = 0;//For fps counter
    private KeyInput keyInput;
    private final double width = Main.getStage().getWidth();
    private final double height = Main.getStage().getHeight();
    private double delta = 0;
    private final double NS = 1000000000 / 60.0;
    private Camera camera;
    private Protagonist protagonist = null;
    private PreLoadedImages preLoadedImages = new PreLoadedImages();
    private Map map= new Map(this.preLoadedImages);
    private static final String sp = File.separator; //Used to read/write to file


    public Game() {
        super(Main.getStage().getWidth(),Main.getStage().getHeight());
        Stage stage = Main.getStage();
        
        stage.setTitle("Tutorial Room");
        Group root = new Group();
        root.getChildren().add(this);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        init();
        stage.show();
        this.keyInput = new KeyInput(scene);
        this.camera = new Camera(0,0);
        loadLevel(this.map.getLevel(0));

    }


    public void stop() {
        this.animationTimer.stop();
    }

    public void start() {
        this.animationTimer.start();
    }



    private void init() {
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
//                Tick/sec counter (Uncomment frames++
//                time += now - previousTime;
//                if (time >= 1000000000.0f) {
//                    System.out.println(frames);
//                    time = 0;
//                    frames = 0;
//                }

                delta += (now - previousTime) / NS;
                previousTime = now;
                while (delta >= 1) {
                    tick();
                    delta--;
//                    frames++;
                }
                render();

            }
        };
    }


    private void tick() {
        Handler.tick();
        if (this.protagonist != null) {
            this.camera.tick(this.protagonist, this.width, this.height, this.map.getCurrentLevelWidth(), this.map.getCurrentLevelHeight());
        }

    }

    private void render() {

        GraphicsContext graphicsContext = this.getGraphicsContext2D();

        graphicsContext.translate(-this.camera.getX(), -this.camera.getY());
        // Draw the floor first, so they dont cover objects
        for (Floor floor: map.getCurrentFloors()) {
            floor.render(graphicsContext);
        }

        Handler.render(graphicsContext);


        graphicsContext.translate(this.camera.getX(), this.camera.getY());

        //Then objects on top
    }

    private void loadLevel(BufferedImage level) {
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                int pixel = level.getRGB(x,y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 0 && green == 0 && blue == 0) { //Black = Wall
                    Handler.addObject(new Wall(x,y, true, this.preLoadedImages.getWallSpriteSheet()));
                } else if (red == 0 && green == 0 && blue == 255) { //Blue = Protagonist
                    Protagonist tempProtagonist = new Protagonist(x,y, true,
                            this.preLoadedImages.getProtagonistSpriteSheet(), this.keyInput);
                    Handler.addObject(tempProtagonist);
                    this.protagonist = tempProtagonist;
                }

                this.map.addFloor(new Floor(x,y,this.preLoadedImages.getFloorSpriteSheet()));
            }
        }
    }

    public Map getMap() {
        return this.map;
    }

    public PreLoadedImages getPreLoadedImages() {
        return this.preLoadedImages;
    }

    private void saveDataToFile(String dataToWrite) {
        //Cant write to file that is inside jar, so find where the jar is, make a text file there, then save things like settings and high scores
        String jarPath = "";
        try {
            jarPath = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
            System.out.println(jarPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String completePath = jarPath.substring(0, jarPath.lastIndexOf("/")) + sp + "File_Name";
        File f = new File(completePath);
        try {
            if (!f.exists() && !f.createNewFile()) {
                System.out.println("File doesnt exist and creating file with path: " + completePath + " failed. ");
            } else {
                System.out.println("Input data exists, or file with path " + completePath + " created successfully. ");
                System.out.println("Absolute Path: "  +f.getAbsolutePath());
                System.out.println("Path: " + f.getPath());
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
                out.writeObject(dataToWrite);
                out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String recoverInputFromFile() {
        System.out.println("Reading data...");
        String completePath = "";
        String jarPath = "";
        String readData = "";
        try {
            jarPath = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
            System.out.println(jarPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        completePath = jarPath.substring(0, jarPath.lastIndexOf("/")) + sp + "File_Name";
        File f = new File(completePath);

        if (f.exists()) {
            System.out.println("File exists. ");
            System.out.println("Absolute Path: "  +f.getAbsolutePath());
            System.out.println("Path: " + f.getPath());
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                readData = (String)in.readObject();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File doesnt exist, or path " + completePath + " is wrong. ");
        }
        return readData;
    }


}
