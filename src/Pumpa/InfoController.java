package Pumpa;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoController implements Initializable
{
    @FXML
    ImageView fx_image_icon;

    @FXML
    Label fx_label_version;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        fx_image_icon.setImage(Main.icon);
        fx_label_version.setText("v" + Main.VERSION);
    }
}
