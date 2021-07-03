package Pumpa;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class NoteListController implements Initializable
{
    final int NEW_NOTE_NAME_MAX_CHAR = 30;
    Image icon;
    boolean selected;

    @FXML
    ImageView fx_image_view_add, fx_image_view_settings, fx_image_view_info;

    @FXML
    private TextField fx_new_note_name;

    @FXML
    private Button fx_add_button, fx_button_settings, fx_button_info;

    @FXML
    ListView<HBox> fx_list_view_note;

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

    void OpenNote(HBox item)
    {
        Main.rootName = RootName.NOTE_VIEW;

        for(int i = 0; i < item.getChildren().size(); i++)
        {
            if(item.getChildren().get(i) instanceof Label)
            {
                Main.currentNote = ((Label)item.getChildren().get(i)).getText();

                break;
            }
        }

        ((NoteViewController)Main.noteViewFXML.getController()).Init();
        Main.master.getScene().setRoot(Main.noteView);
        Deselect();
    }

    void Select()
    {
        if(!selected)
        {
            selected = true;

            for(int i = 0; i < fx_list_view_note.getItems().size(); i++)
            {
                HBox item = fx_list_view_note.getItems().get(i);

                item.getChildren().add(0, new CheckBox());
            }
        }
    }

    void Deselect()
    {
        if(selected)
        {
            selected = false;

            for(int i = 0; i < fx_list_view_note.getItems().size(); i++)
            {
                HBox item = fx_list_view_note.getItems().get(i);

                item.getChildren().remove(0, 1);
            }
        }
    }

    void Rename(String oldName, String newName)
    {
        if(!newName.equals(oldName))
        {
            if(!Main.fileNames.contains(newName))
            {
                File oldNoteFile = new File("data/notes/" + oldName + ".pp");
                File newNoteFile = new File("data/notes/" + newName + ".pp");

                oldNoteFile.renameTo(newNoteFile);

                for(int i = 0; i < Main.fileNames.size(); i++)
                {
                    if(Main.fileNames.get(i).equals(oldName))
                    {
                        Main.fileNames.remove(i);
                        Main.fileNames.add(i, newName);

                        break;
                    }
                }

                RefreshList();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Note already exists");
                alert.setHeaderText("Set a new name which does not exist");
                alert.show();
            }
        }
    }

    void DeleteNote(HBox item)
    {
        for(int i = 0; i < fx_list_view_note.getItems().size(); i++)
        {
            if(item == fx_list_view_note.getItems().get(i))
            {
                String noteName = "";

                for(int j = 0; j < item.getChildren().size(); j++)
                {
                    if(item.getChildren().get(j) instanceof Label)
                    {
                        noteName = ((Label) item.getChildren().get(j)).getText();

                        break;
                    }
                }

                File noteFile = new File("data/notes/" + noteName + ".pp");

                noteFile.delete();
                fx_list_view_note.getItems().remove(i, i + 1);
                Main.fileNames.remove(i);

                break;
            }
        }
    }

    void DeleteMultipleNotes()
    {
        for(int i = 0; i < fx_list_view_note.getItems().size(); i++)
        {
            HBox item = fx_list_view_note.getItems().get(i);

            for(int j = 0; j < item.getChildren().size(); j++)
            {
                if(item.getChildren().get(j) instanceof CheckBox)
                {
                    CheckBox checkBox = (CheckBox) item.getChildren().get(j);

                    if(checkBox.isSelected())
                    {
                        DeleteNote(item);

                        i--;
                    }

                    break;
                }
            }
        }

        Deselect();
    }

    void NoteListItemActionHandler(MouseEvent mouseEvent)
    {
        if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() >= 2)
        {
            OpenNote((HBox)mouseEvent.getSource());
        }
        else if(mouseEvent.getButton() == MouseButton.SECONDARY)
        {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem menuItemOpen = new MenuItem("Open");
            MenuItem menuItemSelect = new MenuItem((selected) ? "Deselect" : "Select");
            MenuItem menuItemRename = new MenuItem("Rename");
            MenuItem menuItemDelete = new MenuItem("Delete");

            menuItemOpen.setOnAction((ActionEvent actionEvent)->
            {
                OpenNote((HBox)mouseEvent.getSource());
            });

            menuItemSelect.setOnAction((ActionEvent actionEvent)->
            {
                if(selected)
                {
                    Deselect();
                }
                else
                {
                    Select();
                }
            });

            menuItemRename.setOnAction((ActionEvent actionEvent)->
            {
                HBox item = (HBox)mouseEvent.getSource();

                for(int i = 0; i < item.getChildren().size(); i++)
                {
                    if(item.getChildren().get(i) instanceof Label)
                    {
                        Label itemLabel = (Label)item.getChildren().get(i);
                        String oldName = itemLabel.getText();
                        TextInputDialog textInputDialog = new TextInputDialog(oldName);

                        textInputDialog.setTitle("Rename Note");
                        textInputDialog.setHeaderText("Set new name of note");
                        ((Stage)textInputDialog.getDialogPane().getScene().getWindow()).getIcons().add(Main.icon);

                        Optional<String> result = textInputDialog.showAndWait();

                        if(result.isPresent())
                        {
                            Rename(oldName, result.get());
                        }

                        break;
                    }
                }
            });

            menuItemDelete.setOnAction((ActionEvent actionEvent)->
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);

                if(selected)
                {
                    alert.setTitle("Delete Selected Notes");
                    alert.setHeaderText("Are you sure you want to delete the selected notes?");
                }
                else
                {
                    alert.setTitle("Delete Note");
                    alert.setHeaderText("Are you sure you want to delete the note?");
                }

                ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(Main.icon);

                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && result.get() == ButtonType.YES)
                {
                    if(selected)
                    {
                        DeleteMultipleNotes();
                    }
                    else
                    {
                        DeleteNote((HBox)mouseEvent.getSource());
                    }
                }
            });

            contextMenu.getItems().addAll(menuItemOpen, menuItemSelect, menuItemRename, menuItemDelete);

            fx_list_view_note.setContextMenu(contextMenu);
        }
    }

    void AddNoteHBox(String noteName)
    {
        HBox hBox = new HBox();
        Label label = new Label(noteName);
        Font font = new Font(label.getFont().getName(), 18);

        label.setFont(font);
        hBox.setOnMouseClicked(this::NoteListItemActionHandler);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(label);
        fx_list_view_note.getItems().add(hBox);
    }

    void AddNote(String noteName)
    {
        Main.fileNames.add(noteName);
        AddNoteHBox(noteName);

        File newNote = new File("data/notes/" + noteName + ".pp");

        try
        {
            newNote.getParentFile().mkdirs();
            newNote.createNewFile();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    void RefreshList()
    {
        selected = false;

        fx_list_view_note.getItems().clear();

        for(int i = 0; i < Main.fileNames.size(); i++)
        {
            AddNoteHBox(Main.fileNames.get(i));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        selected = false;
        FileInputStream imageInputStream = null;

        try
        {
            imageInputStream = new FileInputStream("resources/icons/pumpa_48x48.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            icon = new Image(imageInputStream);

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
            imageInputStream = new FileInputStream("resources/icons/add_16x16.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            Image imageFile = new Image(imageInputStream);
            fx_image_view_add.setImage(imageFile);

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
            imageInputStream = new FileInputStream("resources/icons/settings_16x16.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            Image imageFile = new Image(imageInputStream);
            fx_image_view_settings.setImage(imageFile);

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
            imageInputStream = new FileInputStream("resources/icons/info_16x16.png");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(imageInputStream != null)
        {
            Image imageFile = new Image(imageInputStream);
            fx_image_view_info.setImage(imageFile);

            try
            {
                imageInputStream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        Main.fileNames = new ArrayList<>();
        File filesList = new File("data/files.list");
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

        fx_new_note_name.textProperty().addListener((ObservableValue<? extends String> observableValue,
                                                     String s, String t1) ->
        {
            if(t1.length() > NEW_NOTE_NAME_MAX_CHAR)
            {
                fx_new_note_name.setText(t1.substring(0, NEW_NOTE_NAME_MAX_CHAR));
            }
        });

        fx_new_note_name.setTooltip(new Tooltip("Set new note name"));
        fx_add_button.setTooltip(new Tooltip("Add new note"));
        fx_button_settings.setTooltip(new Tooltip("Settings"));
    }

    public void Action_settings(ActionEvent actionEvent) throws IOException
    {
        FXMLLoader settingsFXMLLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Settings.fxml")));
        Parent settingsParent = settingsFXMLLoader.load();

        Stage settingsStage = new Stage();

        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setResizable(false);
        settingsStage.setTitle("Settings");
        settingsStage.getIcons().add(Main.icon);
        settingsStage.setScene(new Scene(settingsParent));
        settingsStage.show();
    }

    public void Action_info(ActionEvent actionEvent) throws IOException
    {
        FXMLLoader infoFXMLLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Info.fxml")));
        Parent infoParent = infoFXMLLoader.load();

        Stage infoStage = new Stage();

        infoStage.initModality(Modality.APPLICATION_MODAL);
        infoStage.setResizable(false);
        infoStage.setTitle("Info");
        infoStage.getIcons().add(Main.icon);
        infoStage.setScene(new Scene(infoParent));
        infoStage.show();
    }
}
