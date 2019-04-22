package model;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.Node;

import static sample.FXMLUtils.CreateButton;
import static sample.FXMLUtils.CreateLabel;

public class SaveGameMenu extends PauseMenu {

    private HBox hbox;
    private Label title;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextField txtInput;

    public SaveGameMenu(String ID, int width, int height, int xPos, int yPos) {
        super(ID, width, height, xPos, yPos);
        hbox = new HBox(50);
        //CreateMenuLayout(width,height);
    }

    public String getTextInput(){
        return txtInput.getCharacters().toString();
    }

    private void CreateMenuLayout(int width, int height){

        title = CreateLabel("Save Map","saveMenuTitle",width,height/2, TextAlignment.CENTER,false);

        txtInput = new TextField("Enter Name");
        txtInput.setPrefSize(width,height/4);

        hbox = new HBox(50);
        hbox.setPrefSize(width,height/4);
        hbox.setAlignment(Pos.CENTER);

        confirmBtn = CreateButton("Save","saveButton",width/3,height/4,event->System.out.println("Call save function"));
        cancelBtn = CreateButton("Cancel","cancelButton",width/3,height/4,event->this.hide());

        hbox.getChildren().addAll(confirmBtn,cancelBtn);

        this.vbox.getChildren().addAll(title,txtInput,hbox);
    }

    public void AddNodeToHBox(int pos, Node node){
        this.hbox.getChildren().add(pos, node);
    }

    public HBox getHBox(){return this.hbox;}
}
