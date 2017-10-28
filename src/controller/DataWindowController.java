package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

public class DataWindowController {

    private MainWindowController mMain;
    private final ObservableList<Data> observableData = FXCollections.observableArrayList(new Data("(0,0)", 0.0));

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
        int dimension;

        disableFields(true);
        buttonCancel.setDisable(true);
        buttonDone.setDisable(true);
        tableData.setDisable(true);

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
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        tableColumnIndex.setCellValueFactory(
                new PropertyValueFactory<Data, String>("mIndex"));
        tableColumnIndex.setCellFactory(cellFactory);
        tableColumnIndex.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Data, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Data, String> t) {
                        ((Data) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setIndex(t.getNewValue());
                    }
                }
        );
        tableColumnValue.setCellValueFactory(
                new PropertyValueFactory<Data, String>("mValue"));
        tableColumnValue.setCellFactory(cellFactory);
        tableColumnValue.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Data, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Data, String> t) {
                        ((Data) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setValue(Double.parseDouble(t.getNewValue()));
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

        ObservableList<Map> data = FXCollections.observableArrayList();
        Map<String, String> index = new HashMap<>();
        Map<String, Double> value = new HashMap<>();
        index.put(INDEXKEY, "(0,0)");
        value.put(VALUEKEY, 0.0);
        data.add(index);
        data.add(value);


    }

    public class Data {
        private SimpleStringProperty mIndex;
        private SimpleStringProperty mValue;

        public Data(String index, Double value) {
            mIndex = new SimpleStringProperty(index);
            mValue = new SimpleStringProperty(String.valueOf(value));
        }

        public String getIndex() {
            return mIndex.get();
        }

        public void setIndex(String index) {
            mIndex.set(index);
        }

        public String getValue() {
            return mValue.get();
        }

        public void setValue(Double value) {
            mValue.set(String.valueOf(value));
        }
    }

    class EditingCell extends TableCell<Data, String> {

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

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
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
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
