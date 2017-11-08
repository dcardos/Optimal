package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Formula;
import model.MathElement;
import model.SumIndex;
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

    public Optional<List<String>> summationDialog(ObservableList<String> indexList) {
        mDialog = new Dialog<>();
        mDialog.setTitle("Summation Dialog");
        mDialog.setHeaderText("Choose an index");

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

        ComboBox<String> indexChoice = new ComboBox<>();
        indexChoice.setPrefWidth(250);
        if (indexList.size() == 0)
            indexChoice.getItems().add("No index has been created");
        else
            indexChoice.getItems().addAll(indexList);
        indexChoice.getSelectionModel().select(0);


        grid.add(new Label("Choose an index:"), 0, 0);
        grid.add(indexChoice, 1, 0);

        // Enable/Disable ok button depending on whether a startingPoint was entered.
//        Node okBtn = mDialog.getDialogPane().lookupButton(okButton);
//        okBtn.setDisable(true);

        mDialog.getDialogPane().setContent(grid);

        // Request focus on the startingPoint field by default.
        Platform.runLater(() -> indexChoice.requestFocus());

        // Convert the mResult to a startingPoint-endingPoint-pair when the ok button is clicked.
        mDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                List<String> indexSelected = new ArrayList<>();
                for (String sumIndex : indexList) {
                    if (indexChoice.getValue().charAt(0) != 'N' &&
                            sumIndex.charAt(0) == indexChoice.getValue().charAt(0)) {
                        indexSelected.add(indexChoice.getValue());
                    }
                }
                return indexSelected;
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

    public boolean callSummationDialog(Formula formula, MathElement mathElement,
                                       ObservableList<String> indexStringList, ArrayList<SumIndex> indexList) {
        Optional<List<String>> result = summationDialog(indexStringList);
        resultFromDialogs = false;
        result.ifPresent(indexes -> {
            if (result.get().size() > 0)
                resultFromDialogs = true;
        });
        if (resultFromDialogs) {    // it has at least one result
            String input = result.get().get(0);
            SumIndex indexSelected = null;
            for (SumIndex sumIndex : indexList) {
                if (input.charAt(0) == sumIndex.getLetter()) {
                    indexSelected = sumIndex;
                    break;
                }
            }
            if (!indexSelected.isPersonalized()) {
                ((Summation) mathElement.getExpression())
                        .setStartingPointFromPrimitives(indexSelected.getLetter(), indexSelected.getStartValue());
                ((Summation) mathElement.getExpression())
                        .setStoppingPointFromInt(Integer.valueOf(indexSelected.getEndValue()));
                formula.setLastMathElementModified(mathElement);
                mathElement.updateIcon(formula.getAlignment());
            }
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
