package controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Formula;
import model.MathElement;
import model.math.Constant;
import model.math.Summation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Danilo on 07/11/2016.
 */
public class Dialogs {
    // Create the custom mDialog.
    private Dialog<List<String>> mDialog;
    private TextInputDialog mTextInputDialog;
    private boolean mFlag1;
    private boolean mFlag2;
    private boolean resultFromDialogs;

    public Optional<String> latexEntryDialog() {
        mTextInputDialog = new TextInputDialog("Enter LateX formula:");

        mTextInputDialog.setTitle("LateX Formula");
        mTextInputDialog.setHeaderText("Please, enter the lateX syntax of your formula");
        mTextInputDialog.setContentText("Enter LateX formula:");

        Optional<String> result = mTextInputDialog.showAndWait();
        return result;
    }

    public Optional<List<String>> summationDialog() {
        mDialog = new Dialog<>();
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
        // for choosing letter from a combo box
        List<String> choices = new ArrayList<>();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char k : alphabet) choices.add(String.valueOf(k));
        ComboBox<String> letterChoice = new ComboBox<>();
        letterChoice.getItems().addAll(choices);
        letterChoice.getSelectionModel().select("i");


        grid.add(new Label("Starting point:"), 0, 0);
        grid.add(startingPoint, 1, 0);
        grid.add(new Label("Ending point:"), 0, 1);
        grid.add(endingPoint, 1, 1);
        grid.add(new Label("Choose index letter:"), 0, 2);
        grid.add(letterChoice, 1, 2);

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
                List<String> resultStructure = new ArrayList<String>();
                resultStructure.add(startingPoint.getText());
                resultStructure.add(endingPoint.getText());
                resultStructure.add(letterChoice.getValue());
                return resultStructure;
            }
            return null;
        });

        Optional<List<String>> result = mDialog.showAndWait();

        return result;
    }

    public Optional<String> constantDialog() {
        mTextInputDialog = new TextInputDialog("Enter the number:");

        mTextInputDialog.setTitle("Number entry");
        mTextInputDialog.setHeaderText("Please, enter the desired number");
        mTextInputDialog.setContentText("number here");

        return mTextInputDialog.showAndWait();
    }

    public boolean callSummationDialog(Formula formula, MathElement mathElement) {
        Optional<List<String>> result = summationDialog();
        resultFromDialogs = false;
        result.ifPresent(indexes -> {
            resultFromDialogs = true;
        });
        if (resultFromDialogs) {
            List<String> inputs = result.get();
            ((Summation) mathElement.getExpression())
                    .setStartingPointFromPrimitives(inputs.get(2).trim().charAt(0), Integer.valueOf(inputs.get(0)));
            ((Summation) mathElement.getExpression())
                    .setStoppingPointFromInt(Integer.valueOf(inputs.get(1)));
            formula.setLastMathElementModified(mathElement);
            mathElement.updateIcon(formula.getAlignment());
        } else {
            System.out.println("User cancelled the input summation");
        }
        return resultFromDialogs;
    }

    public boolean callConstantDialog(Formula formula, MathElement mathElement) {
        Optional<String> result = constantDialog();
        resultFromDialogs = false;
        result.ifPresent(indexes -> {
            resultFromDialogs = true;
        });
        if (resultFromDialogs) {
            ((Constant)mathElement.getExpression()).setFloat(Float.valueOf(result.get()));
            formula.setLastMathElementModified(mathElement);
            mathElement.updateIcon(formula.getAlignment());
        } else {
            System.out.println("User cancelled the input number");
        }
        return resultFromDialogs;
    }
}
