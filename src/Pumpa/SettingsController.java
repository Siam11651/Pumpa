package Pumpa;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    @FXML
    ChoiceBox<String> fx_choice_box_font_name;
    @FXML
    TextField fx_text_field_font_size;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        for(String fontFamily : Font.getFamilies())
        {
            fx_choice_box_font_name.getItems().add(fontFamily);
        }

        fx_choice_box_font_name.setValue(Main.fontName);
        fx_text_field_font_size.setText(String.valueOf(Main.fontSize));

        fx_text_field_font_size.textProperty().addListener((ObservableValue<? extends String> observableValue,
                                                            String oldValue, String newValue)->
        {
            for(int i = 0; i < newValue.length(); i++)
            {
                if(!('0' <= newValue.charAt(i) && newValue.charAt(i) <= '9'))
                {
                    String suff = "", preff = "";

                    if(i != 0)
                    {
                        suff = newValue.substring(0, i);
                    }

                    if(i != newValue.length() - 1)
                    {
                        preff = newValue.substring(i + 1);
                    }

                    newValue = suff + preff;

                    fx_text_field_font_size.setText(oldValue);

                    break;
                }
            }

            int size;

            try
            {
                size = Integer.parseInt(newValue);
            }
            catch(Exception exception)
            {
                size = Integer.parseInt(oldValue);
            }

            if(size > 48)
            {
                fx_text_field_font_size.setText("48");
            }
            else if(size < 1)
            {
                fx_text_field_font_size.setText("1");
            }
        });
    }

    boolean ApplySettings()
    {
        if(fx_text_field_font_size.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);

            alert.setTitle("Invalid Input");
            alert.setHeaderText("Do not keep the field empty");
            alert.show();

            return false;
        }
        else
        {
            Main.fontName = fx_choice_box_font_name.getValue();
            Main.fontSize = Integer.parseInt(fx_text_field_font_size.getText());

            return true;
        }
    }

    public void Action_settings_apply(ActionEvent actionEvent)
    {
        ApplySettings();
    }

    public void Action_settings_cancel(ActionEvent actionEvent)
    {
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void Action_settings_ok(ActionEvent actionEvent)
    {
        if(ApplySettings())
        {
            ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
        }
    }

    public void Action_font_size(ActionEvent actionEvent)
    {
        if(fx_text_field_font_size.getText().isEmpty())
        {
            String text = String.valueOf(Main.fontSize);

            fx_text_field_font_size.setText(text);
            fx_text_field_font_size.positionCaret(text.length());
        }
    }
}
