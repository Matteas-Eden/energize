package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    @FXML private void NewGameClicked() throws IOException {
        changeStageName("New Game");
        mainMenuPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(mainMenuController.class.getResourceAsStream("/fxmls/newGame.fxml")));
    }

    @FXML private void LoadGameClicked() throws IOException {
        changeStageName("Load Game");
        mainMenuPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(mainMenuController.class.getResourceAsStream("/fxmls/loadGame.fxml")));
    }

    @FXML private void HighScoreClicked() throws IOException {
        changeStageName("High Score");
        mainMenuPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(mainMenuController.class.getResourceAsStream("/fxmls/highScore.fxml")));
    }

    @FXML private void OptionsClicked() throws IOException {
        changeStageName("Options");
        mainMenuPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(mainMenuController.class.getResourceAsStream("/fxmls/options.fxml")));
    }


    @FXML private void CreditsClicked() throws IOException {
        changeStageName("Credits");
        mainMenuPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(mainMenuController.class.getResourceAsStream("/fxmls/credits.fxml")));
    }

    @FXML private void QuitClicked() {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        stage.close();
    }

    private void changeStageName(String newStageName) {
        Stage stage = (Stage) mainMenuPane.getScene().getWindow();
        stage.setTitle(newStageName);
    }
}





