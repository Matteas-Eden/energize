package model;

import sample.Game;

import java.util.ArrayList;

public class Map {
    //Holds all the levels.
    private ArrayList<Level> levels = new ArrayList<>();
    //May need to put this into a level class so we dont need to get the floor each time.
    private Level currentLevel;
    Game game; //To add the protagonist to.


    public Map(Game game){
        this.game = game;
        this.currentLevel = new Level(game,PreLoadedImages.tutorialRoom,0);
        this.levels.add(this.currentLevel);
        this.currentLevel.loadLevel();
    }

    public Level getLevel(int levelNumber) {
        if (this.levels.size() >= levelNumber) {
            //By only updating currentLevelImage, this will ensure if a non existent level is requested it will still return a valid level
            this.currentLevel = this.levels.get(levelNumber);
        }
        return this.currentLevel;
    }

    public void loadLevel(int levelNumber) {
        if (this.levels.size() >= levelNumber) {
            this.currentLevel = this.levels.get(levelNumber);
        }
        this.currentLevel.loadLevel();
    }

    public int getCurrentLevelNumber() {
        return this.currentLevel.getLevelNumber();
    }

    public int getCurrentLevelHeight() {
        return this.currentLevel.getLevelHeight();
    }

    public int getCurrentLevelWidth() {
        return this.currentLevel.getLevelWidth();
    }
}
