package controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

/**
 * Created by Danilo on 07/11/2016.
 */
public class Dialogs {
    // Create the custom mDialog.
    private Dialog<Pair<String, String>> mDialog = new Dialog<>();
    private boolean mFlag1;
    private boolean mFlag2;

    public Optional<Pair<String, String>> summationDialog() {
        mDialog.setTitle("Summation Dialog");
        mDialog.setHeaderText("Enter the summation parameters");

        //Set the icon (must be included in the project).
//        File file = new File("C:\\Users\\Danilo\\Documents\\JavaFXProjects\\Optimal\\img");
//        Image image = new Image(file.toURI().toString());
//        ImageView iv = new ImageView(image);
//        mDialog.setGraphic(iv);

        // Set the button types.
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        mDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        // Create the startingPoint and endingPoint labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField startingPoint = new TextField();
        startingPoint.setPromptText("Starting point");
        TextField endingPoint = new TextField();
        endingPoint.setPromptText("Ending point");

        grid.add(new Label("Starting point:"), 0, 0);
        grid.add(startingPoint, 1, 0);
        grid.add(new Label("Ending point:"), 0, 1);
        grid.add(endingPoint, 1, 1);

        // Enable/Disable login button depending on whether a startingPoint was entered.
        Node okBtn = mDialog.getDialogPane().lookupButton(okButton);
        okBtn.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        mFlag1 = true;
        mFlag2 = true;
        startingPoint.textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty() || mFlag2);
            mFlag1 = newValue.trim().isEmpty();
        });
        endingPoint.textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty() || mFlag1);
            mFlag2 = newValue.trim().isEmpty();
        });

        mDialog.getDialogPane().setContent(grid);

        // Request focus on the startingPoint field by default.
        Platform.runLater(() -> startingPoint.requestFocus());

        // Convert the mResult to a startingPoint-endingPoint-pair when the ok button is clicked.
        mDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(startingPoint.getText(), endingPoint.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = mDialog.showAndWait();

        return result;
    }
}
