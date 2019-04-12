package model;

import sample.Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

//These are the different things that can be on the map
enum TileType {
    FLOOR,
    WALL,
    CAMP_FIRE,
    PROTAGONIST,
    DOOR_ENTER,
    DOOR_EXIT,
    ENEMY,
    NULLTILE,
    ITEM,
    GRUNT,
    BOMBER,
    ARCHER
}

public class Level {
    private ArrayList<ArrayList<TileType>> tiles = new ArrayList<>();
    private int levelNumber; //Used for doors, to go to the correct room
    private int levelWidth;
    private int levelHeight;
    Game game; //To add the protagonist to.
    //Add everything else to the handler

    //////////////Macros, Actual size of different sprites///////////
    private final int FLOOR_SPRITE_WIDTH = 32;
    private final int FLOOR_SPRITE_HEIGHT = 32;

    private final int PROTAGONIST_SPRITE_WIDTH = 400;
    private final int PROTAGONIST_SPRITE_HEIGHT = 296;
    //Protagonist is downscaled from x8 spreadsheet to wanted size.
    private final double PROTAGONIST_SPRITE_SCALE = 0.35;

    private final int CAMP_FIRE_SPRITE_WIDTH = 64;
    private final int CAMP_FIRE_SPRITE_HEIGHT = 64;

    private final int DOOR_SPRITE_WIDTH = 72;
    private final int DOOR_SPRITE_HEIGHT = 96;

    private final int GRUNT_SPRITE_WIDTH = 129;
    private final int GRUNT_SPRITE_HEIGHT = 111;



    public Level(Game game, BufferedImage image, int levelNumber) { //Makes a level from an image
        this.game = game;
        ProcessImage(image);
        this.levelNumber = levelNumber;
    }

    public Level(Game game, int levelNumber, DoorLocation entrance) { //Makes a random level

        this.game = game;
        this.levelNumber = levelNumber;
        //Random size between (20-35)^2
        this.levelWidth = Game.getNextRandomInt(15) + 20;
        this.levelHeight = Game.getNextRandomInt(15) + 20;
        //Initalize with all walls then make path
        for (int row = 0; row < this.levelHeight; row ++) {
            ArrayList<TileType> column = new ArrayList<>();
            for (int col = 0; col < this.levelWidth; col++) {
                if (row == 0 || row == this.levelHeight-1 || col == 0 || col == this.levelWidth-1) {
                    column.add(TileType.NULLTILE);
                } else {
                    column.add(TileType.WALL);
                }
            }
            this.tiles.add(column);
        }
    }


    public ArrayList<ArrayList<TileType>> getTiles() {
        return this.tiles;
    }

    public int getLevelNumber() {
        return this.levelNumber;
    }

    public int getLevelWidth() {
        return this.levelWidth;
    }

    public int getLevelHeight() {
        return this.levelHeight;
    }

    private void ProcessImage(BufferedImage image) {
        this.levelWidth = image.getWidth();
        this.levelHeight = image.getHeight();
        for (int row = 0; row < this.levelHeight; row++) {
            ArrayList<TileType> column = new ArrayList<>();
            for (int col = 0; col < this.levelWidth; col++) {
                int pixel = image.getRGB(col, row);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 128 && green == 128 && blue == 128) { //Grey = Floor
                    column.add(TileType.FLOOR);
                } else if (red == 0 && green == 0 && blue == 0) { //Black = Wall
                    column.add(TileType.WALL);
                } else if (red == 0 && green == 0 && blue == 255) { //Blue = Protagonist
                    column.add(TileType.PROTAGONIST);
                } else if (red == 0 && green == 255 && blue == 0) { //Green = Door
                    column.add(TileType.DOOR_EXIT);
                } else if (red == 255 && green == 165 && blue == 0) { //Orange = Campfire / random chance of some other background but not solid.
                    column.add(TileType.CAMP_FIRE);
                } else if (red == 128 && green == 0 && blue == 128) { //Purple = Item
                    column.add(TileType.ITEM);
                } else if (red == 255 && green == 0 && blue == 1) { // Red = Enemy, (Blue = 1) = Grunt
                    column.add(TileType.GRUNT);
                } else if (red == 255 && green == 0 && blue == 2) { // Red = Enemy, (Blue = 2) = Bomber
                    column.add(TileType.BOMBER);
                } else if (red == 255 && green == 0 && blue == 3) { // Red = Enemy, (Blue = 3) = Archer
                    column.add(TileType.ARCHER);
                } else {
                    column.add(TileType.NULLTILE);
                }
            }
            this.tiles.add(column);
        }
    }

    public void loadLevel() {
        Handler.clearAllObjects();

        for (int row = 0; row < this.levelHeight; row++) {
            for (int col = 0; col < this.levelWidth; col++) {
                TileType tile = this.tiles.get(row).get(col);
                //May be able to move sprite width into respective class's later. Keep here for testing
                switch (tile) {
                    case CAMP_FIRE:
                        Handler.addWall(col + row * this.levelWidth, new AnimationWall(col, row, PreLoadedImages.campFireSpriteSheet, CAMP_FIRE_SPRITE_WIDTH,
                                CAMP_FIRE_SPRITE_HEIGHT, CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, CAMP_FIRE_SPRITE_HEIGHT * Game.SCALE, 4,0,0));
                        break;

                    case WALL:
                        Handler.addWall(col + row * this.levelWidth, new Wall(col,row, PreLoadedImages.campFireSpriteSheet, CAMP_FIRE_SPRITE_WIDTH,
                                CAMP_FIRE_SPRITE_HEIGHT,CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, CAMP_FIRE_SPRITE_HEIGHT * Game.SCALE));
//                        continue; //Continue if wall is a solid sprite, otherwise consider break to draw tile underneath.
                        break;
                    case PROTAGONIST:
                        Protagonist tempProtagonist = new Protagonist(col, row, PreLoadedImages.protagonistSpriteSheet, PROTAGONIST_SPRITE_WIDTH,
                                PROTAGONIST_SPRITE_HEIGHT, (int) (PROTAGONIST_SPRITE_WIDTH * Game.SCALE * PROTAGONIST_SPRITE_SCALE),
                                (int) (PROTAGONIST_SPRITE_HEIGHT * Game.SCALE * PROTAGONIST_SPRITE_SCALE), this.levelWidth);
                        Handler.addPlayer(tempProtagonist);
                        game.setProtagonist(tempProtagonist);
                        break;

                    case DOOR_EXIT:
                        Handler.addDoor(new Door(col, row, PreLoadedImages.doorSpriteSheet, DOOR_SPRITE_WIDTH, DOOR_SPRITE_HEIGHT, Game.PIXEL_UPSCALE, (int) (PROTAGONIST_SPRITE_HEIGHT * Game.SCALE * PROTAGONIST_SPRITE_SCALE), this.levelNumber, 1));
                        break;

                    case FLOOR: //This is just here so if we add tiles with different textures, we can differentiate and create floor with different spreadsheet row/col
                        break;

                    case ITEM:
                        Handler.addWall(col + row * this.levelWidth, new NullTile(col, row, CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, false));
                        continue;

                    case GRUNT:
                        Handler.addEnemy(new Grunt(col,row,PreLoadedImages.gruntSpriteSheet, GRUNT_SPRITE_WIDTH, GRUNT_SPRITE_HEIGHT, GRUNT_SPRITE_WIDTH * Game.SCALE,
                                GRUNT_SPRITE_HEIGHT * Game.SCALE, game.getProtagonist(), this.levelWidth));
                        break;

                    default:
                        Handler.addWall(col + row * this.levelWidth, new NullTile(col, row, CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, CAMP_FIRE_SPRITE_WIDTH * Game.SCALE, true));
                        continue;
                }
                //Move this into its own switch section above and add a tile to handler in the constructor of the other tiles that require a tile eg protagonist, door, enemy
                Handler.addFloor(new Floor(col, row, PreLoadedImages.floorSpriteSheet, FLOOR_SPRITE_WIDTH, FLOOR_SPRITE_HEIGHT, Game.PIXEL_UPSCALE, Game.PIXEL_UPSCALE, 13, 0));
            }
        }
        Handler.updateEnemyTarget(game.getProtagonist()); //As enemies can be added before protagonist making their target null. So add at the end.
    }


}