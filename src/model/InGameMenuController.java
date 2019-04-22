package model;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;

import static sample.FXMLUtils.*;
import static sample.Game.SCREEN_HEIGHT;
import static sample.Game.SCREEN_WIDTH;

public class InGameMenuController {

    //Sizing macros
    private static final int PAUSE_MENU_WIDTH = 300;
    private static final int PAUSE_MENU_HEIGHT = 500;
    private static final int PAUSE_MENU_BTN_WIDTH = 250;
    private static final int PAUSE_MENU_BTN_HEIGHT = 50;
    private static final int PAUSE_MENU_TITLE_HEIGHT = 130;

    private static final int INVENTORY_MENU_WIDTH = 500;
    private static final int INVENTORY_MENU_HEIGHT = 500;

    private static final int SAVE_MENU_WIDTH = 300;
    private static final int SAVE_MENU_HEIGHT = 150;

    private static final int OPTIONS_MENU_WIDTH = 300;
    private static final int OPTIONS_MENU_HEIGHT = 300;

    private static final int CONFIRMATION_MENU_WIDTH = 250;
    private static final int CONFIRMATION_MENU_HEIGHT = 150;

    //==Event macros==//
    //--Pause Menu--
    private EventHandler resumeEvent;
    private EventHandler inventoryMenuEvent = mouseEvent ->{
        this.pauseMenu.hide();
        this.inventoryMenu.show();
    };
    private EventHandler saveMenuEvent = mouseEvent->{
        System.out.println("Shows save game menu");
        this.pauseMenu.hide();
        this.saveGameMenu.show();
    };
    private EventHandler optionsMenuEvent = mouseEvent->{
        System.out.println("Shows options menu");
        this.pauseMenu.hide();
        this.optionsMenu.show();
    };
    private EventHandler quitToTitleEvent = mouseEvent->this.exitToTitleConfirmation.show();
    private EventHandler quitToDesktopEvent = mouseEvent->this.exitToDesktopConfirmation.show();
    //--Inventory Menu--//
    private EventHandler closeInventoryMenuEvent = mouseEvent->{
        this.inventoryMenu.hide();
        this.pauseMenu.show();
    };
    //--Save Menu--//
    private EventHandler saveGameEvent = mouseEvent -> System.out.println("Saves the current map(s)");
    private EventHandler closeSaveMenuEvent = mouseEvent->{
        this.saveGameMenu.hide();
        this.pauseMenu.show();
    };
    //--Options Menu--//
    private EventHandler closeOptionsMenuEvent = mouseEvent->{
        this.optionsMenu.hide();
        this.pauseMenu.show();
    };
    //--Confirmation Menu(s)--//
    private EventHandler returnToTitleEvent;
    private EventHandler returnToDesktopEvent = event -> System.exit(0);

    //Menus
    private PauseMenu pauseMenu;
    private InventoryMenu inventoryMenu;
    private ConfirmationMenu exitToTitleConfirmation;
    private ConfirmationMenu exitToDesktopConfirmation;
    private SaveGameMenu saveGameMenu;
    private OptionsMenu optionsMenu;

    public InGameMenuController(Runnable unpauseGame, EventHandler returnToTitleScreen) {

        this.resumeEvent = event -> {this.pauseMenu.hide();unpauseGame.run();};
        this.returnToTitleEvent = returnToTitleScreen;

        //Generate menu layouts
        this.pauseMenu = CreatePauseMenu(pauseMenu);
        //this.inventoryMenu = CreateInventoryMenu(inventoryMenu);
        this.saveGameMenu = CreateSaveGameMenu(saveGameMenu);
        //this.optionsMenu = CreateOptionsMenu(optionsMenu);

        this.inventoryMenu = new InventoryMenu("inventoryMenu",INVENTORY_MENU_WIDTH,INVENTORY_MENU_HEIGHT,
                SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        //this.saveGameMenu = new SaveGameMenu("saveMenu",SAVE_MENU_WIDTH,SAVE_MENU_HEIGHT,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        this.optionsMenu =  new OptionsMenu("optionsMenu",OPTIONS_MENU_WIDTH,OPTIONS_MENU_HEIGHT,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        this.exitToTitleConfirmation = CreateConfirmationMenu("Are you sure?", "confirmationMenu",
                returnToTitleEvent);
        this.exitToDesktopConfirmation = CreateConfirmationMenu("Are you sure about that?",
                "confirmationMenu", event->System.exit(0));

        //Hide all menus
        this.pauseMenu.hide();
        this.exitToTitleConfirmation.hide();
        this.exitToDesktopConfirmation.hide();
    }

    private void ShowMenu(String menuID){
        //Implement and make public
    }

    private void HideMenu(String menuID){
        //Implement and make public
    }

    public void showPauseMenu(){
        pauseMenu.show();
    }

    public void hidePauseMenu(){
        pauseMenu.hide();
        exitToTitleConfirmation.hide();
    }

    public void showInventoryMenu(){
        pauseMenu.hide();
        inventoryMenu.show();
    }

    public void AddMenusToRoot(Group root){
        root.getChildren().add(pauseMenu);
        root.getChildren().add(inventoryMenu);
        root.getChildren().add(saveGameMenu);
        root.getChildren().add(optionsMenu);
        root.getChildren().add(exitToTitleConfirmation);
        root.getChildren().add(exitToDesktopConfirmation);
    }

    private PauseMenu CreatePauseMenu(PauseMenu pauseMenu){

        int nodePos = 1;

        pauseMenu = new PauseMenu("pauseMenu",PAUSE_MENU_WIDTH,PAUSE_MENU_HEIGHT,
                SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        Label titleLabel = CreateLabel("Pause menu","pauseMenuTitle",PAUSE_MENU_WIDTH,PAUSE_MENU_TITLE_HEIGHT,
                TextAlignment.CENTER,true);
        pauseMenu.SetLabelAsTitle(titleLabel);

        Button resumeButton = CreateButton("Resume","resumeButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT,resumeEvent);
        Button inventoryButton = CreateButton("Inventory","inventoryButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT, inventoryMenuEvent);
        Button saveButton = CreateButton("Save Game","saveButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT, saveMenuEvent);
        Button optionsButton = CreateButton("Options","optionsButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT, optionsMenuEvent);
        Button exitToTitleButton = CreateButton("Quit to Main Menu","quitToTitleButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT,quitToTitleEvent);
        Button exitToDesktopButton = CreateButton("Quit to Desktop","quitToDesktopButton",
                PAUSE_MENU_BTN_WIDTH,PAUSE_MENU_BTN_HEIGHT,quitToDesktopEvent);

        pauseMenu.AddNodeToVBox(nodePos++,resumeButton);
        pauseMenu.AddNodeToVBox(nodePos++,inventoryButton);
        pauseMenu.AddNodeToVBox(nodePos++,saveButton);
        pauseMenu.AddNodeToVBox(nodePos++,optionsButton);
        pauseMenu.AddNodeToVBox(nodePos++,exitToTitleButton);
        pauseMenu.AddNodeToVBox(nodePos++,exitToDesktopButton);

        return pauseMenu;
    }

    private ConfirmationMenu CreateConfirmationMenu(String title, String ID,
                                                    EventHandler confirmationAction){
        ConfirmationMenu confirmationMenu = new ConfirmationMenu(title,ID,
                CONFIRMATION_MENU_WIDTH,CONFIRMATION_MENU_HEIGHT,SCREEN_WIDTH/2,SCREEN_HEIGHT/2,confirmationAction);
        return confirmationMenu;
    }

    private SaveGameMenu CreateSaveGameMenu(SaveGameMenu saveGameMenu){

        int vboxNodePos = 1;
        int hboxNodePos = 0;

        saveGameMenu = new SaveGameMenu("saveMenu",SAVE_MENU_WIDTH,SAVE_MENU_HEIGHT,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        Label saveTitle = CreateLabel("Save Game","saveMenuTitle",
                SAVE_MENU_WIDTH,SAVE_MENU_HEIGHT/2,TextAlignment.CENTER,false);
        saveGameMenu.SetLabelAsTitle(saveTitle);

        Button saveButton = CreateButton("Save","saveButton",
                SAVE_MENU_WIDTH/3,SAVE_MENU_HEIGHT/4, saveGameEvent);
        Button cancelButton = CreateButton("Cancel","cancelButton",
                SAVE_MENU_WIDTH/3,SAVE_MENU_HEIGHT/4,closeSaveMenuEvent);

        saveGameMenu.AddNodeToHBox(hboxNodePos++,saveButton);
        saveGameMenu.AddNodeToHBox(hboxNodePos++,cancelButton);

        TextField saveNameInput = CreateTextField("Enter Name","saveNameTextField",SAVE_MENU_WIDTH,SAVE_MENU_HEIGHT/4);

        saveGameMenu.AddNodeToVBox(vboxNodePos++,saveNameInput);
        saveGameMenu.AddNodeToVBox(vboxNodePos++,saveGameMenu.getHBox());

        return saveGameMenu;
    }

    private InventoryMenu CreateInventoryMenu(InventoryMenu inventoryMenu){
        inventoryMenu = new InventoryMenu("inventoryMenu",
                INVENTORY_MENU_WIDTH,INVENTORY_MENU_HEIGHT,SCREEN_WIDTH/2,SCREEN_HEIGHT/2);

        return inventoryMenu;
    }
}
