package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage mPrimaryStage;

    public Stage getPrimaryStage() {
        return mPrimaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.mPrimaryStage = primaryStage;
        mainWindow();       // calling mainWindow
    }

    public void mainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/mainWindowViewV2.fxml"));
        AnchorPane pane = loader.load();

        MainWindowController mainWindowController = loader.getController();
        try {
            mainWindowController.setMain(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(pane);      // takes a pane as an argument
        //scene.getStylesheets().add(getClass().getResource("../view/styles.css").toExternalForm());
        mPrimaryStage.setScene(scene);       // set scene to the stage
        mPrimaryStage.setResizable(false);
        mPrimaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
