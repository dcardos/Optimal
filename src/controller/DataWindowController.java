package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DataWindowController {
    private MainWindowController mMain;
    @FXML private TextField textDimension;
    @FXML private TextField textRowsNumber;
    @FXML private TextField textColumnsNumber;
    @FXML private Label labelDimension;
    @FXML private Label labelRowsNumber;
    @FXML private Label labelColumnsNumber;
    @FXML private TableView tableData;
    @FXML private Button buttonFillValues;
    @FXML private Button buttonImportFile;
    @FXML private Button buttonCancel;
    @FXML private Button buttonDone;

    public void setMain(MainWindowController main) throws Exception {
        this.mMain = main;
        int dimension;

        textRowsNumber.setDisable(true);
        textColumnsNumber.setDisable(true);

        textDimension.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d")) {
                    if (newValue.length() > 0)
                        textDimension.setText(newValue.substring(0, newValue.length() - 1));
                }
                if (textDimension.getText().length() > 0) {
                    int value = Integer.parseInt(textDimension.getText());
                    if (value == 1) {
                        textRowsNumber.setDisable(false);
                        labelRowsNumber.setDisable(false);
                        textColumnsNumber.setDisable(true);
                        labelColumnsNumber.setDisable(true);
                    } else if (value == 2) {
                        textRowsNumber.setDisable(false);
                        labelRowsNumber.setDisable(false);
                        textColumnsNumber.setDisable(false);
                        labelColumnsNumber.setDisable(false);
                    } else if (value > 2) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Attention");
                        alert.setHeaderText(null);
                        alert.setContentText("Since dimension is greater than 2 you have to import from file!");
                        alert.showAndWait();
                    } else {
                        textRowsNumber.setDisable(true);
                        labelRowsNumber.setDisable(true);
                        textColumnsNumber.setDisable(true);
                        labelColumnsNumber.setDisable(true);
                    }
                } else {
                    textRowsNumber.setDisable(true);
                    labelRowsNumber.setDisable(true);
                    textColumnsNumber.setDisable(true);
                    labelColumnsNumber.setDisable(true);
                }
            }
        });
    }
}
