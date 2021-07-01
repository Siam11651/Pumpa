package Pumpa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application
{
    public static ArrayList<String> fileNames;
    public static String currentNote;
    public static Stage master;
    public static FXMLLoader noteViewFXML;
    public static Parent noteList, noteView;
    public static RootName rootName;
    Image icon;

    void WindowCloseEvent(WindowEvent windowEvent)
    {
        if(rootName == RootName.NOTE_VIEW && !((NoteViewController)noteViewFXML.getController()).GetSaved())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "",
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            alert.setTitle("Unsaved note");
            alert.setHeaderText("Do you want to save before closing?");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);

            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent())
            {
                if(result.get() == ButtonType.YES)
                {
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
                        String content = ((NoteViewController)Main.noteViewFXML.getController()).GetTextAreaText();

                        try
                        {
                            noteOutputStream.write(content.getBytes(), 0, content.length());
                            noteOutputStream.close();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if(result.get() == ButtonType.CANCEL)
                {
                    windowEvent.consume();
                }
            }
        }

        try
        {
            FileWriter fileListWriter = new FileWriter("data/files.list");

            for(int i = 0; i < fileNames.size(); i++)
            {
                fileListWriter.append(fileNames.get(i));

                if(i < fileNames.size() - 1)
                {
                    fileListWriter.append("\n");
                }
            }

            fileListWriter.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FileInputStream iconInputStream = new FileInputStream("resources/icons/pumpa_48x48.png");
        icon = new Image(iconInputStream);

        iconInputStream.close();

        File filesList = new File("data/files.list");

        if(!filesList.exists())
        {
            filesList.getParentFile().mkdirs();
            filesList.createNewFile();
        }

        rootName = RootName.NOTE_LIST;
        master = primaryStage;
        noteViewFXML = new FXMLLoader(Objects.requireNonNull(getClass().getResource("NoteView.fxml")));
        noteList = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("NoteList.fxml")));
        noteView = noteViewFXML.load();

        master.setTitle("Pumpa");
        master.getIcons().add(icon);
        master.setScene(new Scene(noteList));
        master.setMaximized(true);
        master.setMinHeight(480);
        master.setMinWidth(640);
        master.setOnCloseRequest(this::WindowCloseEvent);
        master.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
