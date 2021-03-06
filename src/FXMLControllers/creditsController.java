package FXMLControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.SoundController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class creditsController implements Initializable {

    @FXML private AnchorPane creditsPane;
    @FXML private ImageView logoImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set image for imageView
        logoImage.setImage(new Image(this.getClass().getResourceAsStream("/Images/NocturnalTutelageStudiosLogo.png")));
    }
    public void creditsBackButtonPressed() throws IOException {
        changeStageName("Main Menu");
        creditsPane.getChildren().setAll((AnchorPane) new FXMLLoader().load(MainMenuController.class.getResourceAsStream("/fxmls/mainMenu.fxml")));
    }


    private void changeStageName(String newStageName) {
        Stage stage = (Stage) creditsPane.getScene().getWindow();
        stage.setTitle(newStageName);
    }

}
