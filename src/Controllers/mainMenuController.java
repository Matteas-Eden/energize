package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class mainMenuController implements Initializable {

    @FXML private AnchorPane mainMenuPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML private void newGameButtonPressed() throws IOException {
        changeStageName("New Game");
        mainMenuPane.getChildren().setAll((AnchorPane) FXMLLoader.load(getClass().getResource("../fxmls/newGame.fxml")));
    }


    @FXML private void loadGameButtonPressed() throws IOException {
        changeStageName("Load Game");
        mainMenuPane.getChildren().setAll((AnchorPane) FXMLLoader.load(getClass().getResource("../fxmls/loadGame.fxml")));
    }

    @FXML private void highScoreButtonPressed() throws IOException {
        changeStageName("High Score");
        mainMenuPane.getChildren().setAll((AnchorPane) FXMLLoader.load(getClass().getResource("../fxmls/highScore.fxml")));
    }

    @FXML private void optionsButtonPressed() throws IOException {
        changeStageName("Options");
        mainMenuPane.getChildren().setAll((AnchorPane) FXMLLoader.load(getClass().getResource("../fxmls/options.fxml")));
    }


    @FXML private void creditsButtonPressed() throws IOException {
        changeStageName("Credits");
        mainMenuPane.getChildren().setAll((AnchorPane) FXMLLoader.load(getClass().getResource("../fxmls/credits.fxml")));
    }

    @FXML private void quitButtonPressed() {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        stage.close();
    }



    private void changeStageName(String newStageName) {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        stage.setTitle(newStageName);
    }
}





