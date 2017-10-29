package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataWindowController {

    private MainWindowController mMain;
    private final ObservableList<Data> observableData = FXCollections.observableArrayList();

    @FXML private TextField textDimension;
    @FXML private TextField textRowsNumber;
    @FXML private TextField textColumnsNumber;
    @FXML private Label labelDimension;
    @FXML private Label labelRowsNumber;
    @FXML private Label labelColumnsNumber;
    @FXML private TableView<Data> tableData;
    @FXML private TableColumn tableColumnIndex;
    @FXML private TableColumn tableColumnValue;
    @FXML private Button buttonFillValues;
    @FXML private Button buttonImportFile;
    @FXML private Button buttonCancel;
    @FXML private Button buttonDone;

    public static final String INDEXKEY = "I";
    public static final String VALUEKEY = "V";

    public void setMain(MainWindowController main) throws Exception {
        this.mMain = main;

        disableFields(true);
        buttonCancel.setDisable(true);
        buttonDone.setDisable(true);
//        tableData.setDisable(true);

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
                        buttonFillValues.setDisable(false);
                    } else if (value == 2) {
                        disableFields(false);
                    } else if (value > 2) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Attention");
                        alert.setHeaderText(null);
                        alert.setContentText("Since dimension is greater than 2 you have to import from file!");
                        alert.showAndWait();
                        disableFields(true);
                    } else {
                        disableFields(true);
                    }
                } else {
                    disableFields(true);
                }
            }
        });
        // restricting the other text fields
        textRowsNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d+")) {
                    if (newValue.length() > 0)
                        textRowsNumber.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });
        textColumnsNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d+")) {
                    if (newValue.length() > 0)
                        textColumnsNumber.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });

        // Table and data
        tableData.setEditable(true);
        // for editing
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        // setting columns data and editing properties for "value"
        tableColumnIndex.setCellValueFactory(
                new PropertyValueFactory<Data, String>("mIndex"));

        tableColumnValue.setCellValueFactory(
                new PropertyValueFactory<Data, Double>("mValue"));
        tableColumnValue.setCellFactory(cellFactory);
        tableColumnValue.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Data, Double>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Data, Double> t) {
                        ((Data) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setMValue(t.getNewValue());
                    }
                }
        );

        tableData.setItems(observableData);

    }

    private void disableFields(boolean arg) {
        textRowsNumber.setDisable(arg);
        labelRowsNumber.setDisable(arg);
        textColumnsNumber.setDisable(arg);
        labelColumnsNumber.setDisable(arg);
        buttonFillValues.setDisable(arg);
    }

    public void fillDataCliked() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        if (textRowsNumber.getText().length() < 1) {
            alert.setContentText("Number of rows must be filled!");
            alert.showAndWait();
        } else if (!textColumnsNumber.isDisabled() && textColumnsNumber.getText().length() < 1) {
            alert.setContentText("Number of columns must be filled!");
            alert.showAndWait();
        } else if (textRowsNumber.getText().length() > 2) {
            alert.setContentText("Number of rows must be less than 99 otherwise use a file to import the values.");
            alert.showAndWait();
        } else if (!textColumnsNumber.isDisabled() && textRowsNumber.getText().length() > 2) {
            alert.setContentText("Number of columns must be less than 99 otherwise use a file to import the values.");
            alert.showAndWait();
        } else {
            Map<Integer, String> subScripts = new HashMap<>();
            subScripts.put(0, "\u2080");
            subScripts.put(1, "\u2081");
            subScripts.put(2, "\u2082");
            subScripts.put(3, "\u2083");
            subScripts.put(4, "\u2084");
            subScripts.put(5, "\u2085");
            subScripts.put(6, "\u2086");
            subScripts.put(7, "\u2087");
            subScripts.put(8, "\u2088");
            subScripts.put(9, "\u2089");
            disableFields(true);
            buttonImportFile.setDisable(true);
            buttonDone.setDisable(false);
            buttonCancel.setDisable(false);
            if (textDimension.getText().equals("1")) {
                for (int i = 0; i <= Integer.parseInt(textRowsNumber.getText()); i++) {
                    if (i < 10)
                        observableData.add(new Data(mMain.mCoefficientBeingUsed.getLetter()
                                + subScripts.get(i), 0.0));
                    else
                        observableData.add(new Data(mMain.mCoefficientBeingUsed.getLetter()
                                + subScripts.get(i/10) + subScripts.get(i%10), 0.0));
                }
            } else if (textDimension.getText().equals("2")) {
                for (int i = 0; i <= Integer.parseInt(textRowsNumber.getText()); i++) {
                    for (int j = 0; j <= Integer.parseInt(textColumnsNumber.getText()); j++) {
                        StringBuffer index = new StringBuffer();
                        index.append("\u208d");
                        if (i < 10)
                            index.append(subScripts.get(i));
                        else
                            index.append(subScripts.get(i/10) + subScripts.get(i%10));
                        index.append("\u208e \u208d");
                        if (j < 10)
                            index.append(subScripts.get(j));
                        else
                            index.append(subScripts.get(j/10) + subScripts.get(j%10));
                        index.append("\u208e");
                        observableData.add(new Data(mMain.mCoefficientBeingUsed.getLetter() +
                                index.toString(), 0.0));
                    }
                }
            }
            textDimension.setDisable(true);
        }
    }

    public void cancelClicked() {
        observableData.clear();
        disableFields(true);
        buttonCancel.setDisable(true);
        buttonDone.setDisable(true);
        buttonImportFile.setDisable(false);
        textColumnsNumber.setText("");
        textRowsNumber.setText("");
        textDimension.setText("");
        textDimension.setDisable(false);
    }

    public void done() {
        mMain.mCoefficientBeingUsed.setDimension(Integer.parseInt(textDimension.getText()));
        ArrayList<double[]> dataParsed = new ArrayList<>();
        int rowsNumber = Integer.parseInt(textRowsNumber.getText());
        double[] rowValues = new double[rowsNumber+1];
        if (Integer.parseInt(textDimension.getText()) == 1) {
            for (int i = 0; i <= rowsNumber; i++)
                rowValues[i] = observableData.get(i).getMValue();
            dataParsed.add(rowValues);
        } else {
            int columnsNumber = Integer.parseInt(textColumnsNumber.getText());
            int index = 0;
            for (int i = 0; i <= rowsNumber; i++) {
                double[] columnValues = new double[columnsNumber+1];
                for (int j = 0; j <= columnsNumber; j++) {
                    columnValues[j] = observableData.get(index++).getMValue();
                }
                dataParsed.add(columnValues);
            }
        }
        mMain.mCoefficientBeingUsed.setData(dataParsed);
        // get a handle to the stage
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


    /** classes used internally here **/
    public class Data {
        private SimpleStringProperty mIndex;
        private SimpleDoubleProperty mValue;

        public Data(String index, Double value) {
            mIndex = new SimpleStringProperty(index);
            mValue = new SimpleDoubleProperty(value);
        }

        public String getMIndex() {
            return mIndex.get();
        }

        public void setMIndex(String index) {
            mIndex.set(index);
        }

        public Double getMValue() {
            return mValue.get();
        }

        public void setMValue(Double value) {
            mValue.set(value);
        }
    }

    class EditingCell extends TableCell<Data, Double> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(String.valueOf(getItem()));
            setGraphic(null);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                                    Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(Double.parseDouble(textField.getText()));
                    }
                }
            });

            // for tab porpourses
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(Double.valueOf(textField.getText()));
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    } else if (t.getCode() == KeyCode.TAB) {
                        commitEdit(Double.valueOf(textField.getText()));
                        getTableView().edit(getTableRow().getIndex()+1, getTableColumn());
                    }
                }
            });

            // restricting what user can type
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("[-+]$|[^.]*[0-9]\\.$") && !newValue.matches("^[-+]?[0-9]\\d*(\\.\\d+)?$")) {
                        if (newValue.length() > 0)
                            textField.setText(newValue.substring(0, newValue.length() - 1));
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
