package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Coefficient;
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

    public Optional<List<String>> indexDialog(ObservableList<String> indexList, boolean forSum, int nIndexes) {
        mDialog = new Dialog<>();
        if (forSum) {
            mDialog.setTitle("Summation Dialog");
            mDialog.setHeaderText("Choose an index");
        } else {
            mDialog.setTitle("Domain Dialog");
            mDialog.setHeaderText("Choose an index to set the dimension domain");
        }

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

        ArrayList<ComboBox<String>> indexChoices = new ArrayList<>();
        if (forSum) {
            ComboBox<String> singleCB = new ComboBox<>();
            indexChoices.add(singleCB);
            indexChoices.get(0).setPrefWidth(250);
            if (indexList.size() == 0)
                indexChoices.get(0).getItems().add("No index has been created");
            else
                indexChoices.get(0).getItems().addAll(indexList);
            indexChoices.get(0).getSelectionModel().select(0);
            grid.add(new Label("Choose an index:"), 0, 0);
            grid.add(indexChoices.get(0), 1, 0);
        } else {
            for (int i = 0; i < nIndexes; i++) {
                indexChoices.add(new ComboBox<String>());
                indexChoices.get(i).setPrefWidth(250);
                if (indexList.size() == 0)
                    indexChoices.get(i).getItems().add("No index has been created");
                else
                    indexChoices.get(i).getItems().addAll(indexList);
                indexChoices.get(i).getSelectionModel().select(0);
                grid.add(new Label("Choose an index for dimension " + (i+1) + ":"), 0, i);
                grid.add(indexChoices.get(i), 1, i);
            }
        }
        mDialog.getDialogPane().setContent(grid);

        // Request focus on the startingPoint field by default.
        Platform.runLater(() -> indexChoices.get(0).requestFocus());

        // Convert the mResult to a startingPoint-endingPoint-pair when the ok button is clicked.
        mDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                ArrayList<String> indexesSelected = new ArrayList<>();
                for (String sumIndex : indexList) {
                    for (int i = 0; i < indexChoices.size(); i++) {
                        if (indexChoices.get(i).getValue().charAt(0) != 'N' &&
                                sumIndex.charAt(0) == indexChoices.get(i).getValue().charAt(0)){
                            indexesSelected.add(sumIndex);
                        }
                    }
                }
                return indexesSelected;
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
        Optional<List<String>> result = indexDialog(indexStringList, true, 1);
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

    public boolean callCoefficientDialog(Formula formula, MathElement mathElement,
                                         ObservableList<String> indexStringList, ArrayList<SumIndex> indexList,
                                         int dimension) {
        Optional<List<String>> result = indexDialog(indexStringList, false, dimension);
        resultFromDialogs = false;
        result.ifPresent(indexes -> {
            if (result.get().size() > 0)
                resultFromDialogs = true;
        });
        if (resultFromDialogs) {    // it has at least one element
            for (String index : result.get()) {
                ((Coefficient)mathElement.getExpression()).addIndex(index.charAt(0));
            }
            formula.setLastMathElementModified(mathElement);
            mathElement.updateIcon(formula.getAlignment());
        } else {
            System.out.println("User cancelled the input number");
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
