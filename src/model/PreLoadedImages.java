package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PreLoadedImages {
    //Load all of the images on startup, static and available everywhere.
    public static BufferedImage tutorialRoom;
    public static BufferedImage doorSpriteSheet;
    public static BufferedImage tileSpriteSheet;
    public static BufferedImage gruntSpriteSheet;
    public static BufferedImage protagonistSpriteSheet;
    public static BufferedImage campFireSpriteSheet;
    public static BufferedImage shieldSpriteSheet;
    public static BufferedImage bossSpriteSheet;

    static {
        try {
            tutorialRoom = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/TutorialRoom.png"));
            doorSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/Doors.png"));
            tileSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/Tileset.png"));
            gruntSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/GruntSpriteSheet.png"));
            protagonistSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/ProtagonistSheet.png"));
            campFireSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/CampFire.png"));
            shieldSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/Shield.png"));
            bossSpriteSheet = ImageIO.read(PreLoadedImages.class.getResourceAsStream("/Images/BossSpriteSheetx3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
