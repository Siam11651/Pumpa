package Pumpa;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class NoteViewController implements Initializable
{
    boolean saved;

    @FXML
    ImageView fx_image_view_back, fx_image_view_save;

    @FXML
    Label fx_label_current_note;

    @FXML
    TextArea fx_text_area_note;

    @FXML
    Button fx_button_save_note, fx_button_back;

    public boolean GetSaved()
    {
        return saved;
    }

    public String GetTextAreaText()
    {
        return fx_text_area_note.getText();
    }

    public void Action_save_note(ActionEvent event)
    {
        saved = true;

        fx_button_save_note.setVisible(false);

        FileOutputStream noteOutputStream = null;

        try
        {
            noteOutputStream = new FileOutputStream("data/notes/" + Main.currentNote + ".pp");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(noteOutputStream != null)
        {
            try
            {
                noteOutputStream.write(fx_text_area_note.getText().getBytes(), 0,
                        fx_text_area_note.getText().length());
                noteOutputStream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void Action_back(ActionEvent actionEvent)
    {
        Main.currentNote = "";
        Main.master.getScene().setRoot(Main.noteList);
        Main.rootName = RootName.NOTE_LIST;
    }

    public void Init()
    {
        FileInputStream noteInputStream = null;

        try
        {
            noteInputStream = new FileInputStream("data/notes/" + Main.currentNote + ".pp");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        String noteContent = null;

        if(noteInputStream != null)
        {
            try
            {
                noteContent = new String(noteInputStream.readAllBytes());
                noteInputStream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        if(noteContent != null)
        {
            fx_label_current_note.setText(Main.currentNote);
            fx_text_area_note.setText(noteContent);
        }

        saved = true;

        fx_button_save_note.setVisible(false);
        fx_text_area_note.setFont(new Font(Main.fontName, Main.fontSize));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        FileInputStream imageInputStream = null;

        try
        {
            imageInputStream = new FileInputStream("resources/icons/save_16x16.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            Image imageFile = new Image(imageInputStream);
            fx_image_view_save.setImage(imageFile);

            try
            {
                imageInputStream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            imageInputStream = new FileInputStream("resources/icons/back_24x24.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            Image imageFile = new Image(imageInputStream);
            fx_image_view_back.setImage(imageFile);

            try
            {
                imageInputStream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        fx_text_area_note.textProperty().addListener((ObservableValue<? extends String> observableValue,
                                                      String oldValue, String newValue) ->
        {
            if(!oldValue.equals(newValue))
            {
                saved = false;

                fx_button_save_note.setVisible(true);
            }
        });

        fx_button_back.setTooltip(new Tooltip("Back"));
        fx_button_save_note.setTooltip(new Tooltip("Save"));
    }
}
