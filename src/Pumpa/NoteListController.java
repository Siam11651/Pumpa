package Pumpa;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class NoteListController implements Initializable
{
    final int NEW_NOTE_NAME_MAX_CHAR = 30;

    @FXML
    private TextField fx_new_note_name;

    @FXML
    private Button fx_add_button;

    @FXML
    private VBox fx_note_list;

    @FXML
    void Action_add(ActionEvent event)
    {
        if(!fx_new_note_name.getText().isEmpty())
        {
            String name = fx_new_note_name.getText();

            if(!Main.fileNames.contains(name))
            {
                AddNote(fx_new_note_name.getText());
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setResizable(false);
                alert.setTitle("Failed to add new note");
                alert.setHeaderText("Note already exists.");
                alert.show();
            }

            fx_new_note_name.clear();
        }
    }

    void NoteButtonActionHandler(ActionEvent event)
    {
        Main.rootName = RootName.NOTE_VIEW;
        Main.currentNote = ((Button)event.getSource()).getText();

        ((NoteViewController)Main.noteViewFXML.getController()).Init();
        Main.master.getScene().setRoot(Main.noteView);
    }

    void AddNote(String noteName)
    {
        Main.fileNames.add(noteName);

        int size = Main.fileNames.size();
        Button button = new Button(Main.fileNames.get(size - 1));

        button.setOnAction(this::NoteButtonActionHandler);

        fx_note_list.getChildren().add(button);
        File newNote = new File("resources/notes/" + noteName + ".pp");

        try
        {
            newNote.createNewFile();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    void RefreshList()
    {
        fx_note_list.getChildren().clear();

        for(int i = 0; i < Main.fileNames.size(); i++)
        {
            Button button = new Button(Main.fileNames.get(i));

            button.setOnAction(this::NoteButtonActionHandler);

            fx_note_list.getChildren().add(button);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Main.fileNames = new ArrayList<>();
        File filesList = new File("resources/files.list");
        Scanner filesListScanner = null;

        try
        {
            filesListScanner = new Scanner(filesList);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        while(filesListScanner != null &&filesListScanner.hasNextLine() )
        {
            String line = filesListScanner.nextLine();

            if(!line.isEmpty())
            {
                Main.fileNames.add(line);
            }
        }

        if(filesListScanner != null)
        {
            filesListScanner.close();
        }

        RefreshList();

        fx_new_note_name.textProperty().addListener((ObservableValue<? extends String> observableValue, String s, String t1) ->
        {
            if(t1.length() > NEW_NOTE_NAME_MAX_CHAR)
            {
                fx_new_note_name.setText(t1.substring(0, NEW_NOTE_NAME_MAX_CHAR));
            }
        });
    }
}
