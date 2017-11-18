package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import model.math.*;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class MainWindowController {
    // constants
    private static final double XVARCOEFPOS = 200.0;
    private static final double YVARPOS = 50.0;
    private static final double YCOEFPOS = 80.0;

    public Main mMain;
    private FXGraphics2D g2;

    public TreeSet<Formula> formulas;
    private Formula lastModifiedFormula;
    private MathElement beingDragged;
    private final ArrayList<Variable> mVariables = new ArrayList<>();
    private final ArrayList<model.Coefficient> mCoefficients = new ArrayList<>();
    private final ArrayList<SumIndex> mIndexes = new ArrayList<>();

    private final ToggleGroup mMaxMin = new ToggleGroup();

    private boolean editVariableFlag;
    private boolean editCoefficientFlag;
    private boolean editIndexFlag;
    private int editIndex;
    private final ObservableList<String> observableVariableList = FXCollections.observableArrayList();
    private final ObservableList<String> observableCoefficientList = FXCollections.observableArrayList();
    private final ObservableList<String> observableIndexList = FXCollections.observableArrayList();
    private final ObservableList<String> observableDomainList = FXCollections.observableArrayList(
            "\u2115  Natural", "\u2124  Integer", "\u211d  Real");
    private final ObservableList<String> observableDimensionList = FXCollections.observableArrayList(
            "1", "2", "3");
    private final ObservableList<String> unusedLetters = FXCollections.observableArrayList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private final ObservableList<String> unusedIndexLetters = FXCollections.observableArrayList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private final ObservableList<String> unusedSetLetters = FXCollections.observableArrayList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z");

    public model.Coefficient mCoefficientBeingUsed = null;

    @FXML private Label lblMainFunction;
    @FXML private Label lblPromptVar;
    @FXML private Label lblLetterVar;
    @FXML private Label lblDomainVar;
    @FXML private Label lblDimensionVar;
    @FXML private Label lblUpperBound;
    @FXML private Label lblLowerBound;
    @FXML private Label lblPromptCoef;
    @FXML private Label lblLetterCoef;
    @FXML private Label lblData;
    @FXML private Canvas canvas;
    @FXML private AnchorPane innerAnchorPane;
    @FXML private AnchorPane anchorPaneModel;
    @FXML public RadioButton rdBtnMax;
    @FXML public RadioButton rdBtnMin;
    @FXML private Label lblConstraints;
    @FXML private TabPane tabPane;
    @FXML private Tab tabVar;
    @FXML private Tab tabCoefData;
    @FXML private Tab tabIndexSet;
    @FXML private Tab tabModel;
    @FXML private ScrollPane scrollPane;
    @FXML private Button buttonNewConstraint;
    @FXML private HBox hbBasic;
    @FXML private HBox hbVariables;
    @FXML private HBox hbCoefficients;
    // variable tab
    @FXML private Button buttonNewVariable;
    @FXML private Button buttonCancelVariable;
    @FXML private Button buttonCancelCoef;
    @FXML private Button buttonEditVariable;
    @FXML private Button buttonEditCoef;
    @FXML private Button buttonNewCoefficient;
    @FXML private Button buttonNewData;
    @FXML private Button buttonEditData;
    @FXML private Button buttonExportLP;
    @FXML private ComboBox cbDomainVar;
    @FXML private CheckBox checkNonNegative;
    @FXML private ComboBox cbLetterVar;
    @FXML private ComboBox cbDimensionVar;
    @FXML private ComboBox cbLetterCoef;
    @FXML private TextField textUpperBound;
    @FXML private TextField textLowerBound;
    // Accordion and TitledPanes
    @FXML private Accordion accordionBase;
    @FXML private TitledPane tpVariables;
    @FXML private TitledPane tpCoefficients;
    @FXML private TitledPane tpIndexes;
    @FXML private ListView lvVariables;
    @FXML private ListView lvCoefficients;
    @FXML private ListView lvIndexes;
    // Index tab
    @FXML private Button buttonNewIndex;
    @FXML private Button buttonEditIndex;
    @FXML private Button buttonCancelIndex;
    @FXML private Button buttonReadValuesFile;
    @FXML private Button buttonShowValuesFile;
    @FXML private Label lblPromptIndex;
    @FXML private Label lblLetterIndex;
    @FXML private ComboBox cbLetterIndex;
    @FXML private Label lblPreDefSets;
    @FXML private ComboBox cbSet;
    @FXML private Label lblStartValue;
    @FXML private TextField textStartValue;
    @FXML private Label lblEndValue;
    @FXML private TextField textEndValue;


    public void setMain(Main main) throws Exception {
        mMain = main;

        // Initializing variables and fields
        editCoefficientFlag = false;
        editVariableFlag = false;
        editIndexFlag = false;
        buttonCancelVariable.setDisable(true);
        buttonEditVariable.setDisable(true);
        buttonCancelCoef.setDisable(true);
        buttonEditCoef.setDisable(true);
        buttonEditData.setDisable(true);
        buttonNewData.setDisable(true);
        buttonEditIndex.setDisable(true);
        buttonExportLP.setDisable(true);
        // TODO: read set values from file
        buttonReadValuesFile.setDisable(true);
        buttonShowValuesFile.setDisable(true);
        disableVariableFields(true);
        disableCoefFields(true);
        disableIndexFields(true);
        lvVariables.setItems(observableVariableList);
        lvCoefficients.setItems(observableCoefficientList);
        lvIndexes.setItems(observableIndexList);
        cbDomainVar.setItems(observableDomainList);
        cbDimensionVar.setItems(observableDimensionList);
        cbLetterVar.setItems(unusedLetters);
        cbLetterCoef.setItems(unusedLetters);
        cbSet.setItems(unusedSetLetters);
        cbLetterIndex.setItems(unusedIndexLetters);
        resetVarFields();
        resetCoefFields();
        resetIndexFields();
        cbDomainVar.setValue(cbDomainVar.getItems().get(2));
        cbSet.setValue(cbSet.getItems().get(0));
        lblPromptVar.setText("");
        lblPromptCoef.setText("");
        lblPromptIndex.setText("");

        // Initializing h-boxes
        hbBasic.setAlignment(Pos.CENTER);
        hbVariables.setAlignment(Pos.CENTER);
        hbCoefficients.setAlignment(Pos.CENTER);

        // force the field to be numeric only
        textLowerBound.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[-+]$|[^.]*[0-9]\\.$") && !newValue.matches("^[-+]?[0-9]\\d*(\\.\\d+)?$")) {
                    if (newValue.length() > 0)
                        textLowerBound.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });
        textUpperBound.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[-+]$|[^.]*[0-9]\\.$") && !newValue.matches("^[-+]?[0-9]\\d*(\\.\\d+)?$")) {
                    if (newValue.length() > 0)
                        textUpperBound.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });
        textStartValue.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[-+]$|[^.]*[0-9]\\.$") && !newValue.matches("^[-+]?[0-9]\\d*(\\.\\d+)?$")) {
                    if (newValue.length() > 0)
                        textStartValue.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });
        textEndValue.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[-+]$|[^.]*[0-9]\\.$") && !newValue.matches("^[-+]?[0-9]\\d*(\\.\\d+)?$")) {
                    if (newValue.length() > 0)
                        textEndValue.setText(newValue.substring(0, newValue.length() - 1));
                }
            }
        });

        innerAnchorPane.setStyle("-fx-background-color: #FFFFFF");

        // Initializing fonts for LateX
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmex10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);

        // Initializing constraints label
        AnchorPane.setTopAnchor(lblConstraints, 5+FormulasPositionSet.getMainFormulaYEndPosition() +
                (FormulasPositionSet.mFirstConstraintStartYPosition -
                        FormulasPositionSet.getMainFormulaYEndPosition())/2.0);

        // Initializing button for new formula
        buttonNewConstraint.setDisable(true);

        // Variable and coefficient context pop menu
        final ContextMenu varContextMenu = new ContextMenu();
        final ContextMenu coefContextMenu = new ContextMenu();
        final ContextMenu indexContextMenu = new ContextMenu();
        MenuItem removeVar = new MenuItem("Remove");
        MenuItem removeCoef = new MenuItem("Remove");
        MenuItem removeIndex = new MenuItem("Remove");
        varContextMenu.getItems().add(removeVar);
        coefContextMenu.getItems().add(removeCoef);
        indexContextMenu.getItems().add(removeIndex);
        removeVar.setOnAction(event -> {
            if (editIndex > -1) {  // reassuring index within bounds
                observableVariableList.remove(editIndex);
                Variable removedVar = mVariables.remove(editIndex);
                unusedLetters.add(String.valueOf(removedVar.getLetter()));
                FXCollections.sort(unusedLetters);
                refreshVarsInModel();
                removeVarOrCoefFromFormulas(removedVar.getLetter());
                buttonEditVariable.setDisable(true);
                buttonNewVariable.setDisable(false);
            }
        });
        removeCoef.setOnAction(event -> {
            if (editIndex > -1) {  // reassuring index within bounds
                observableCoefficientList.remove(editIndex);
                model.Coefficient removedCoef = mCoefficients.remove(editIndex);
                unusedLetters.add(String.valueOf(removedCoef.getLetter()));
                FXCollections.sort(unusedLetters);
                refreshCoefsInModel();
                removeVarOrCoefFromFormulas(removedCoef.getLetter());
                buttonEditCoef.setDisable(true);
                buttonNewCoefficient.setDisable(false);
            }
        });
        removeIndex.setOnAction(event -> {
            if (editIndex > -1) {  // reassuring index within bounds
                observableIndexList.remove(editIndex);
                SumIndex removedIndex = mIndexes.remove(editIndex);
                unusedIndexLetters.add(String.valueOf(removedIndex.getLetter()));
                FXCollections.sort(unusedIndexLetters);
                buttonEditIndex.setDisable(true);
                buttonNewIndex.setDisable(false);
                //TODO: reflect remove on model
            }
        });

        // Adding listeners to the listView on titled pane
        lvVariables.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editIndex = lvVariables.getSelectionModel().getSelectedIndex();
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (editIndex > -1) {
                        lblPromptVar.setText("Variable " + mVariables.get(editIndex).getLetter() + " selected");
                        cbDomainVar.setValue(mVariables.get(editIndex).getDomain());
                        checkNonNegative.setSelected(mVariables.get(editIndex).isNonNegative());
                        cbLetterVar.setValue(mVariables.get(editIndex).getLetter());
                        cbDimensionVar.setValue(mVariables.get(editIndex).getDimension());
                        String bound;
                        if (mVariables.get(editIndex).getUpperBound() != null) {
                            bound = String.valueOf(mVariables.get(editIndex).getUpperBound());
                            if (bound.matches(".*\\.0*$"))   // is an integer?
                                bound = bound.replaceAll("\\.0*$", "");
                            textUpperBound.setText(bound);
                        } else
                            textUpperBound.clear();
                        if (mVariables.get(editIndex).getLowerBound() != null) {
                            bound = String.valueOf(mVariables.get(editIndex).getLowerBound());
                            if (bound.matches(".*\\.0*$"))   // is an integer?
                                bound = bound.replaceAll("\\.0*$", "");
                            textLowerBound.setText(bound);
                        } else
                            textLowerBound.clear();
                        buttonEditVariable.setDisable(false);
                    } else
                        buttonEditVariable.setDisable(true);
                } else if (event.getButton() == MouseButton.SECONDARY && editIndex > -1) {
                    varContextMenu.show(lvVariables, event.getScreenX(), event.getScreenY());
                }
            }
        });
        lvCoefficients.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editIndex = lvCoefficients.getSelectionModel().getSelectedIndex();
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (editIndex > -1) {
                        lblPromptCoef.setText("Coefficient " + mCoefficients.get(editIndex).getLetter() + " selected");
                        cbLetterCoef.setValue(mCoefficients.get(editIndex).getLetter());
                        buttonEditCoef.setDisable(false);
                        mCoefficientBeingUsed = mCoefficients.get(editIndex);
                    } else
                        buttonEditCoef.setDisable(true);
                } else if (event.getButton() == MouseButton.SECONDARY && editIndex > -1) {
                    coefContextMenu.show(lvCoefficients, event.getScreenX(), event.getScreenY());
                }
            }
        });
        lvIndexes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editIndex = lvIndexes.getSelectionModel().getSelectedIndex();
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (editIndex > -1) {
                        lblPromptIndex.setText("Index " + mIndexes.get(editIndex).getLetter() + " selected");
                        cbSet.setValue(mIndexes.get(editIndex).getSet());
                        cbLetterIndex.setValue(mIndexes.get(editIndex).getLetter());
                        textStartValue.setText(String.valueOf(mIndexes.get(editIndex).getStartValue()));
                        textEndValue.setText(String.valueOf(mIndexes.get(editIndex).getEndValue()));
                        buttonEditIndex.setDisable(false);
                    } else
                        buttonEditIndex.setDisable(true);
                } else if (event.getButton() == MouseButton.SECONDARY && editIndex > -1) {
                    indexContextMenu.show(lvIndexes, event.getScreenX(), event.getScreenY());
                }
            }
        });

        // Initializing formulas
        this.formulas = new TreeSet<>();
        Formula mainFormula = new Formula(FormulasPositionSet.mMainFormulaStartXPosition,
                FormulasPositionSet.mMainFormulaStartYPosition, true);
        addNewFormula(mainFormula);
        Formula firstConstraint = new Formula(FormulasPositionSet.mConstraintStartXPosition,
                FormulasPositionSet.mFirstConstraintStartYPosition, false);
        addNewFormula(firstConstraint);

        // Initializing canvas and scroll pane
        this.g2 = new FXGraphics2D(canvas.getGraphicsContext2D());
        drawFormulas();
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        // Redraw canvas when size changes.
        canvas.widthProperty().bind(scrollPane.widthProperty());
//        canvas.heightProperty().bind(scrollPane.heightProperty());
        canvas.widthProperty().addListener(evt -> drawFormulas());
        canvas.heightProperty().addListener(evt -> drawFormulas());

        // Initializing radio buttons
        rdBtnMax.setToggleGroup(mMaxMin);
        rdBtnMin.setToggleGroup(mMaxMin);

        // Initializing Model Tab with math elements
        makeModelBasicElements();

        // adding event when tab clicked
        tabVar.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (tabVar.isSelected()) {
                    accordionBase.setExpandedPane(tpVariables);
                }
            }
        });
        tabCoefData.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (tabCoefData.isSelected()) {
                    accordionBase.setExpandedPane(tpCoefficients);
                }
            }
        });
        tabIndexSet.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (tabIndexSet.isSelected()) {
                    accordionBase.setExpandedPane(tpIndexes);
                }
            }
        });

        // canvas context pop menu
        final ContextMenu canvasContextMenu = new ContextMenu();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem remove = new MenuItem("Remove");
        MenuItem cancel = new MenuItem("Cancel");
        canvasContextMenu.getItems().addAll(cut, copy, paste, remove, cancel);
        cut.setOnAction(event -> System.out.println("cut clicked"));
        remove.setOnAction(event -> {
            lastModifiedFormula.removeMathElement(lastModifiedFormula.getLastMathElementModified());
            drawFormulas();
            lastModifiedFormula.setLastMathElementModified(null);
            lastModifiedFormula = null;
            // TODO: what happens when no math elements are left in a constraint?
        });

        // left/right click over canvas
        canvas.setOnMousePressed(t -> {
            Formula formula = getFormula((int)t.getY());
            if (null == formula) return;
            lastModifiedFormula = formula;
            MathElement mathElement = formula.getMathElement(((int)t.getX()));
            if (null == mathElement) return;
            // right click : context menu
            if (t.isSecondaryButtonDown()) {
                formula.setLastMathElementModified(mathElement);
                canvasContextMenu.show(canvas, t.getScreenX(), t.getScreenY());
            // left click : pop up dialog
            } else {
                // To be even more precise about the element height
                if (Formula.isBetween((int) t.getY(),
                        mathElement.getYStart(), mathElement.getYEnd())) {
                    drawMathElement(formula.turnColorBackTo(Color.black), formula.getAlignment());
                    Dialogs dialogs = new Dialogs();
                    if (mathElement.getExpression() instanceof Summation) {
                        dialogs.callSummationDialog(formula, mathElement, observableIndexList, mIndexes);
                    } else if (mathElement.getExpression() instanceof Constant) {
                        dialogs.callConstantDialog(formula, mathElement);
                    }
                    drawFormulas();
                }
            }
        });

        // when you are hovering the mouse over the math elements - turn them red
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, t -> {
            Formula formula = getFormula((int)t.getY());
            if (null == formula) return;
            MathElement mathElement = formula.getMathElement(((int)t.getX()));
            if (null == mathElement) {
                drawMathElement(formula.turnColorBackTo(Color.black), formula.getAlignment());
                return;
            }
            // To be even more precise about the element height
            if (!Formula.isBetween((int)t.getY(),
                    mathElement.getYStart(), mathElement.getYEnd())) {
                drawMathElement(formula.turnColorBackTo(Color.black), formula.getAlignment());
                return;
            }
            // draw only if necessary
            if (mathElement.getColor() != Color.red) {
                if (formula.isRedFlag())
                    drawMathElement(formula.turnColorBackTo(Color.black), formula.getAlignment());
                mathElement.setColor(Color.red);
                formula.setRedFlag(true);
                formula.setLastMathElementModified(mathElement);
                drawMathElement(mathElement, formula.getAlignment());
            }
        });

        // when dragging math elements to the canvas
        canvas.setOnDragOver(event -> {
            /* data is dragged over the target */
            /* accept it only if it is not dragged from the same node
             * and if it has a string data */
            if (event.getGestureSource() != canvas &&
                    event.getDragboard().hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

                /* Add a mock element */
//                    System.out.println("data is dragged over the canvas");
                assert (null != beingDragged);
                beingDragged.setColor(Color.gray);
                Formula formula = getFormula((int)event.getY());
                if (null == formula || !formula.isActiveEditing()) return;
                MathElement mathElement = formula.getMathElement(((int)event.getX()));
                // inside the formula, over a mathElement area
                if (formula.getLastMathElementModified() != beingDragged) { // first addition
                    if (null == mathElement && formula.getMathElements().isEmpty()) {
                        formula.addMathElementAtTheBeginning(beingDragged);
                    } else if (null != mathElement) {
                        if (event.getX() < mathElement.getXCenter())
                            formula.addMathElement(beingDragged, mathElement.getXStart());
                        else
                            formula.addMathElementAtTheEnd(beingDragged);
                    }
                    formula.setLastMathElementModified(beingDragged);
                    drawFormulas();
                } else {
                    if (null == mathElement) {
                        return;
                    }
//                        System.out.println("Current x: " + event.getX() + "ME X Start: " + mathElement.getXStart());
                    if (mathElement.getXStart() < beingDragged.getXStart()
                            && event.getX() <= mathElement.getXCenter()) { // moving left
                        formula.removeMathElement(beingDragged);
                        formula.addMathElement(beingDragged, mathElement.getXStart());
                        drawFormulas();
                    } else if (mathElement.getXStart() >= beingDragged.getXEnd() &&
                            event.getX() > mathElement.getXCenter()) {    // moving right
                        formula.removeMathElement(beingDragged);
                        formula.addMathElement(beingDragged, mathElement.getXEnd());
                        drawFormulas();
                    }
                }
                lastModifiedFormula = formula;
            }
            event.consume();
        });

        canvas.setOnDragExited(event -> {
            if (!event.isDropCompleted() && (null != lastModifiedFormula)) {
//                System.out.println("Dragged did not complete");
                lastModifiedFormula.removeMathElement(beingDragged);
                drawFormulas();
                beingDragged = null;
                lastModifiedFormula = null;
            }
            event.consume();
        });

        canvas.setOnDragDropped(event -> {
            /* data dropped */
            /* if there is a string data on dragboard, read it and use it */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
//                System.out.println("Dragged completed successfully!");
                success = true;
                if (lastModifiedFormula == null || !lastModifiedFormula.isActiveEditing()) {
//                    System.out.println("lastModifiedFormula == null or not in Editing mode");
                    return;
                }
                lastModifiedFormula.removeMathElement(beingDragged);
                MathElement mathElement = null;
                Dialogs dialogs = new Dialogs();
                // TODO: other instances
                boolean addME = true;
                if (beingDragged.getExpression() instanceof Summation) {
                    Summation summation = new Summation();
                    mathElement = new MathElement(beingDragged, summation);
                    if (!dialogs.callSummationDialog(lastModifiedFormula, mathElement, observableIndexList, mIndexes))
                        addME = false;
                } else if (beingDragged.getExpression() instanceof Constant) {
                    Constant constant = new Constant();
                    mathElement = new MathElement(beingDragged, constant);
                    if (!dialogs.callConstantDialog(lastModifiedFormula, mathElement))
                        addME = false;
                } else if (beingDragged.getExpression() instanceof Sum) {
                    Sum sum = new Sum();
                    mathElement = new MathElement(beingDragged, sum);
                } else if (beingDragged.getExpression() instanceof Subtraction) {
                    Subtraction subtraction = new Subtraction();
                    mathElement = new MathElement(beingDragged, subtraction);
                } else if (beingDragged.getExpression() instanceof LessOrEqual) {
                    LessOrEqual lessOrEqual = new LessOrEqual();
                    mathElement = new MathElement(beingDragged, lessOrEqual);
                } else if (beingDragged.getExpression() instanceof GreaterOrEqual) {
                    GreaterOrEqual greaterOrEqual = new GreaterOrEqual();
                    mathElement = new MathElement(beingDragged, greaterOrEqual);
                } else if (beingDragged.getExpression() instanceof Equal) {
                    Equal equal = new Equal();
                    mathElement = new MathElement(beingDragged, equal);
                } else if (beingDragged.getExpression() instanceof OpenParenthesis) {
                    OpenParenthesis openParenthesis = new OpenParenthesis();
                    mathElement = new MathElement(beingDragged, openParenthesis);
                } else if (beingDragged.getExpression() instanceof CloseParenthesis) {
                    CloseParenthesis closeParenthesis = new CloseParenthesis();
                    mathElement = new MathElement(beingDragged, closeParenthesis);
                } else if (beingDragged.getExpression() instanceof Coefficient) {
                    Coefficient coefficient = new Coefficient(((Coefficient) beingDragged.getExpression()).getLetter());
                    mathElement = new MathElement(beingDragged, coefficient);
                    // finding the dimension number
                    int dimension = -1;
                    for (model.Coefficient coefficientModel : mCoefficients) {
                        if (coefficientModel.getLetter() == coefficient.getLetter()) {
                            dimension = coefficientModel.getDimension();
                            break;
                        }
                    }
                    if (dimension == -1) {
                        for (Variable variable : mVariables) {
                            if (variable.getLetter() == coefficient.getLetter()) {
                                dimension = variable.getDimension();
                                break;
                            }
                        }
                    }
                    if (!dialogs.callCoefficientDialog(lastModifiedFormula, mathElement,
                            observableIndexList, mIndexes, dimension))
                        addME = false;
                }
                if (addME && mathElement != null) {
                    try {
                        lastModifiedFormula.addMathElement(mathElement, mathElement.getXStart());
                        boolean coefAfterComparasion = false;
                        if (mathElement.getExpression() instanceof Coefficient) {
                            for (MathElement element : lastModifiedFormula.getMathElements()) {
                                if ((element.getExpression() instanceof LessOrEqual ||
                                        element.getExpression() instanceof GreaterOrEqual) &&
                                        element.getXStart() < mathElement.getXStart()) {
                                    coefAfterComparasion = true;
                                    break;
                                }
                            }
                            if (coefAfterComparasion) {
                                char set = '?';
                                Coefficient modelCoef = (Coefficient)mathElement.getExpression();
                                for (SumIndex sumIndex : mIndexes) {
                                    if (sumIndex.getLetter() == modelCoef.getIndexes().lastElement()) {
                                        set = sumIndex.getSet();
                                        break;  // Attention: the set if regarding the last index of the coefficient
                                        // TODO: check consistency on the Sets!
                                    }
                                }
                                MathElement forAll = new MathElement(new ForAll(
                                        ((Coefficient) mathElement.getExpression()).getIndexes(), set));
                                lastModifiedFormula.addMathElementAtTheEnd(forAll);
                            }
                        }
                    } catch (NullPointerException e) {
//                        System.out.printf("Invalid MathElement or its X start position");
                        e.printStackTrace();
                    }
                }
                beingDragged = null;
                drawFormulas();
            }
//            System.out.println(formulas);
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

    }

    private void removeVarOrCoefFromFormulas(char letter) {
        ArrayList<MathElement> mahElementsToBeRemoved = new ArrayList<>();
        for (Formula formula : formulas) {
            mahElementsToBeRemoved.clear();
            for (MathElement mathElement : formula.getMathElements()) {
                if (mathElement.getExpression() instanceof Coefficient) {
                    if (((Coefficient)mathElement.getExpression()).getLetter() == letter) {
                        mahElementsToBeRemoved.add(mathElement);
                    }
                }
            }
            for (MathElement mathElement : mahElementsToBeRemoved) {
                formula.removeMathElement(mathElement);
            }
        }
        drawFormulas();
        // TODO: delete empty constraints
    }

    private void editLetterFromFormulas(char oldLetter, model.Coefficient newCoef) {
        ArrayList<MathElement> mahElementsToBeRemoved = new ArrayList<>();
        for (Formula formula : formulas) {
            mahElementsToBeRemoved.clear();
            for (MathElement mathElement : formula.getMathElements()) {
                if (mathElement.getExpression() instanceof Coefficient) {
                    if (((Coefficient) mathElement.getExpression()).getLetter() == oldLetter) {
                        mahElementsToBeRemoved.add(mathElement);
                    }
                }
            }
            for (MathElement oldMathElement : mahElementsToBeRemoved) {
                formula.removeMathElement(oldMathElement);
                formula.setActiveEditing(true);
                MathElement newMathElement = new MathElement(newCoef);
                formula.addMathElement(newMathElement, oldMathElement.getXStart());
                formula.setActiveEditing(false);
            }
        }
        drawFormulas();
    }

    public void addNewConstraint() {
        Formula newConstraint = new Formula(FormulasPositionSet.mConstraintStartXPosition,
                FormulasPositionSet.mFirstConstraintStartYPosition +
                        ((formulas.size()-1)*FormulasPositionSet.getTotalConstraintVerticalSpace()),
                false);
        try {
            addNewFormula(newConstraint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.out.println("InnerAnchorPane:" + innerAnchorPane.getHeight());
            scrollPane.setVvalue(1.0);  // scrolling to the end [0, 1]
        }
    }

    private void resetVarFields() {
        cbDomainVar.setValue(cbDomainVar.getItems().get(0));
        cbDimensionVar.setValue(cbDimensionVar.getItems().get(0));
        if (unusedLetters.size() >= 3)
            cbLetterVar.setValue(cbLetterVar.getItems().get(unusedLetters.size()-3));
        else if (unusedLetters.size() > 0)
            cbLetterVar.setValue(cbLetterVar.getItems().get(0));
        textLowerBound.clear();
        textUpperBound.clear();
        lblPromptVar.setText("");
        checkNonNegative.setSelected(false);
    }

    private void resetCoefFields() {
        if (unusedLetters.size() > 0)
            cbLetterCoef.setValue(cbLetterCoef.getItems().get(0));
        lblPromptCoef.setText("");
    }

    public void prepareToAddNewVariable() {
        resetVarFields();
        lblPromptVar.setText("New variable");
        buttonEditVariable.setText("Confirm");
        buttonEditVariable.setDisable(false);
        buttonCancelVariable.setDisable(false);
        buttonNewVariable.setDisable(true);
        editVariableFlag = false;
        disableVariableFields(false);
    }

    public void prepareToAddNewCoefficient() {
        resetCoefFields();
        lblPromptCoef.setText("New Coefficient");
        buttonEditCoef.setText("Confirm");
        buttonEditCoef.setDisable(false);
        buttonCancelCoef.setDisable(false);
        buttonNewCoefficient.setDisable(true);
        buttonNewData.setDisable(false);
        buttonEditData.setDisable(true);
        editCoefficientFlag = false;
        disableCoefFields(false);
    }

    public void addEditVar() {
        boolean valuesChecked = checkBoundVarTextFields();
        if (buttonEditVariable.getText().equalsIgnoreCase("edit")) {    // editing variable
            if (editIndex < mVariables.size()) {   // just reassuring index is within bounds
                lblPromptVar.setText("Editing variable " + mVariables.get(editIndex).getLetter());
                buttonEditVariable.setText("Confirm");
                buttonCancelVariable.setDisable(false);
                buttonNewVariable.setDisable(true);
                editVariableFlag = true;
                disableVariableFields(false);
            }
        } else if (!editVariableFlag && valuesChecked) {    // adding new variable
                Variable var = new Variable(cbLetterVar.getValue().toString().trim().charAt(0),
                        Integer.parseInt(cbDimensionVar.getValue().toString()),
                        cbDomainVar.getValue().toString().trim().charAt(0), checkNonNegative.isSelected());
                // Letter is now used
                unusedLetters.remove(cbLetterVar.getSelectionModel().getSelectedIndex());
                // var has bound?
                if (textUpperBound.getText().length() > 0)
                    var.setUpperBound(Double.parseDouble(textUpperBound.getText()));
                else
                    var.setUpperBound(null);
                if (textLowerBound.getText().length() > 0)
                    var.setLowerBound(Double.parseDouble(textLowerBound.getText()));
                else
                    var.setLowerBound(null);
                // GUI reaction
                mVariables.add(var);
                Collections.sort(mVariables);
                refreshVarsInModel();
                observableVariableList.add(var.getLetter() + " \u2208 " + var.getDomain() + ", " + var.getDimension() + " dimension");
                FXCollections.sort(observableVariableList);
                accordionBase.setExpandedPane(tpVariables);
                lvVariables.scrollTo(lvVariables.getItems().size() - 1);
                // setting buttons and flags
                disableVariableFields(true);
                resetVarFields();
                if (unusedLetters.size() > 0)
                    buttonNewVariable.setDisable(false);
                buttonEditVariable.setText("Edit");
                buttonEditVariable.setDisable(true);
                buttonCancelVariable.setDisable(true);
        } else if (valuesChecked) {    // done editing variable
                    // changing letter
                    String oldLetter = String.valueOf(mVariables.get(editIndex).getLetter());
                    // editing data structure
                    mVariables.get(editIndex).setLetter(cbLetterVar.getValue().toString().charAt(0));
                    if (!oldLetter.equalsIgnoreCase(cbLetterVar.getValue().toString())) {
                        unusedLetters.remove(cbLetterVar.getSelectionModel().getSelectedIndex());
                        unusedLetters.add(oldLetter);
                        FXCollections.sort(unusedLetters);
                    }
                    mVariables.get(editIndex).setDimension(Integer.parseInt(cbDimensionVar.getValue().toString()));
                    mVariables.get(editIndex).setDomain(cbDomainVar.getValue().toString().trim().charAt(0));
                    mVariables.get(editIndex).setNonNegative(checkNonNegative.isSelected());
                    if (textUpperBound.getText().length() > 0)
                        mVariables.get(editIndex).setUpperBound(Double.parseDouble(textUpperBound.getText()));
                    if (textLowerBound.getText().length() > 0)
                        mVariables.get(editIndex).setLowerBound(Double.parseDouble(textLowerBound.getText()));
                    // GUI response
                    Variable var = mVariables.get(editIndex);
                    observableVariableList.set(editIndex,
                            var.getLetter() + " \u2208 " + var.getDomain() + ", " + var.getDimension() + " dimension");
                    Collections.sort(mVariables);
                    FXCollections.sort(observableVariableList);
                    refreshVarsInModel();
                    editLetterFromFormulas(oldLetter.trim().charAt(0), var);
                    disableVariableFields(true);
                    resetVarFields();
                    buttonEditVariable.setText("Edit");
                    buttonEditVariable.setDisable(true);
                    if (unusedLetters.size() > 0)
                        buttonNewVariable.setDisable(false);
                    buttonCancelVariable.setDisable(true);
        }
    }

    public void addEditCoef() {
        if (buttonEditCoef.getText().equalsIgnoreCase("edit")) {    // editing coefficient
            if (editIndex < mCoefficients.size()) {    // just reassuring index is within bounds
                lblPromptCoef.setText("Editing coefficient " + mCoefficients.get(editIndex).getLetter());
                buttonEditCoef.setText("Confirm");
                buttonCancelCoef.setDisable(false);
                buttonNewCoefficient.setDisable(true);
                buttonEditData.setDisable(false);
                editCoefficientFlag = true;
                disableCoefFields(false);
            }
        } else if (!editCoefficientFlag) {  // adding new coefficient
            if (mCoefficientBeingUsed == null || mCoefficientBeingUsed.getData() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("It is necessary to set values for the coefficients first!");
                alert.showAndWait();
            } else {
                model.Coefficient coef = new model.Coefficient(cbLetterCoef.getValue().toString().charAt(0),
                        mCoefficientBeingUsed.getDimension());
                coef.setData(mCoefficientBeingUsed.getData());
                // Letter is now used
                unusedLetters.remove(cbLetterCoef.getSelectionModel().getSelectedIndex());
                // GUI reaction
                mCoefficients.add(coef);
                Collections.sort(mCoefficients);
                refreshCoefsInModel();
                observableCoefficientList.add(coef.getLetter() + ", " + coef.getDimension() + " dimension");
                FXCollections.sort(observableCoefficientList);
                accordionBase.setExpandedPane(tpCoefficients);
                lvCoefficients.scrollTo(lvCoefficients.getItems().size() - 1);
                // setting buttons and flags
                disableCoefFields(true);
                resetCoefFields();
                if (unusedLetters.size() > 0)
                    buttonNewCoefficient.setDisable(false);
                buttonEditCoef.setText("Edit");
                buttonEditCoef.setDisable(true);
                buttonCancelCoef.setDisable(true);
                buttonNewData.setDisable(true);
                mCoefficientBeingUsed = null;
            }
        } else if (editCoefficientFlag) {   // done editing coefficient
            // changing letter
            String oldLetter = String.valueOf(mCoefficients.get(editIndex).getLetter());
            // editing data structure
            mCoefficients.get(editIndex).setLetter(cbLetterCoef.getValue().toString().charAt(0));
            if (!oldLetter.equalsIgnoreCase(cbLetterCoef.getValue().toString())) {
                unusedLetters.remove(cbLetterCoef.getSelectionModel().getSelectedIndex());
                unusedLetters.add(oldLetter);
                FXCollections.sort(unusedLetters);
            }
            mCoefficients.get(editIndex).setDimension(mCoefficientBeingUsed.getDimension());
            mCoefficients.get(editIndex).setData(mCoefficientBeingUsed.getData());
            // GUI response
            model.Coefficient coef = mCoefficients.get(editIndex);
            observableCoefficientList.set(editIndex,
                    coef.getLetter() + ", " + coef.getDimension() + " dimension");
            Collections.sort(mCoefficients);
            FXCollections.sort(observableCoefficientList);
            refreshCoefsInModel();
            editLetterFromFormulas(oldLetter.trim().charAt(0), coef);
            disableCoefFields(true);
            resetCoefFields();
            buttonEditCoef.setText("Edit");
            buttonEditCoef.setDisable(true);
            if (unusedLetters.size() > 0)
                buttonNewCoefficient.setDisable(false);
            buttonCancelCoef.setDisable(true);
            buttonEditData.setDisable(true);
            mCoefficientBeingUsed = null;
        }
    }

    public void addEditIndex() {
        boolean valuesChecked = checkBoundIndexTextFields();
        if (buttonEditIndex.getText().equalsIgnoreCase("edit")) {    // editing index
            if (editIndex < mIndexes.size()) {   // just reassuring index is within bounds
                lblPromptIndex.setText("Editing index " + mIndexes.get(editIndex).getLetter());
                buttonEditIndex.setText("Confirm");
                buttonNewIndex.setDisable(true);
                editIndexFlag = true;
                disableIndexFields(false);
            }
        } else if (!editIndexFlag && valuesChecked) {    // adding new index
            SumIndex sumIndex = new SumIndex(cbLetterIndex.getValue().toString().trim().charAt(0),
                    cbSet.getValue().toString().trim().charAt(0),
                    Integer.parseInt(textStartValue.getText()), Integer.parseInt(textEndValue.getText()));
            // Letter is now used
            unusedIndexLetters.remove(cbLetterIndex.getSelectionModel().getSelectedIndex());
            // GUI reaction
            mIndexes.add(sumIndex);
            Collections.sort(mIndexes);
            observableIndexList.add(sumIndex.getLetter() + " \u2208 " + sumIndex.getSet() + " | " +
                    sumIndex.getStartValue() + " \u2264 " + sumIndex.getLetter() + " \u2264 " + sumIndex.getEndValue());
            FXCollections.sort(observableIndexList);
            accordionBase.setExpandedPane(tpIndexes);
            lvIndexes.scrollTo(lvIndexes.getItems().size() - 1);
            // setting buttons and flags
            disableIndexFields(true);
            resetIndexFields();
            if (unusedIndexLetters.size() > 0)
                buttonNewIndex.setDisable(false);
            buttonEditIndex.setText("Edit");
            buttonEditIndex.setDisable(true);
        } else if (valuesChecked) {    // done editing variable
            // changing letter
            String oldLetter = String.valueOf(mIndexes.get(editIndex).getLetter());
            String oldSet = String.valueOf(mIndexes.get(editIndex).getSet());
            // editing data structure
            mIndexes.get(editIndex).setLetter(cbLetterIndex.getValue().toString().charAt(0));
            if (!oldLetter.equalsIgnoreCase(cbLetterIndex.getValue().toString())) {
                unusedIndexLetters.remove(cbLetterIndex.getSelectionModel().getSelectedIndex());
                unusedIndexLetters.add(oldLetter);
                FXCollections.sort(unusedIndexLetters);
            }
            mIndexes.get(editIndex).setSet(cbSet.getValue().toString().trim().charAt(0));
            mIndexes.get(editIndex).setStartAndEndValue(Integer.parseInt(textStartValue.getText()),
                    Integer.parseInt(textEndValue.getText()));
            // GUI response
            SumIndex sumIndex = mIndexes.get(editIndex);
            observableIndexList.set(editIndex, sumIndex.getLetter() + " \u2208 " +
                    sumIndex.getSet() + " | " + sumIndex.getStartValue() + " \u2264 " +
                    sumIndex.getLetter() + " \u2264 " + sumIndex.getEndValue());
            Collections.sort(mIndexes);
            FXCollections.sort(observableIndexList);
            // TODO: reflect edit in Model
            disableIndexFields(true);
            resetIndexFields();
            buttonEditIndex.setText("Edit");
            buttonEditIndex.setDisable(true);
            if (unusedIndexLetters.size() > 0)
                buttonNewIndex.setDisable(false);
        }
    }

    private boolean checkBoundVarTextFields() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Something is wrong");
        alert.setHeaderText(null);
        if (textLowerBound.getText().matches("[-+]$|.*\\.$")) {
            alert.setContentText("Invalid lower bound value.");
            alert.showAndWait();
            return false;
        } else if (textUpperBound.getText().matches("[-+]$|.*\\.$")) {
            alert.setContentText("Invalid upper bound value.");
            alert.showAndWait();
            return false;
        } else if (checkNonNegative.isSelected() && textUpperBound.getText().length() > 0 &&
                Double.valueOf(textUpperBound.getText()) <= 0) {
            alert.setContentText("Invalid upper bound value: since the variable is non-negative, " +
                    "the upper limit has to be at least more than 0.");
            alert.showAndWait();
            return false;
        } else if (checkNonNegative.isSelected() && textLowerBound.getText().length() > 0 &&
                Double.valueOf(textLowerBound.getText()) < 0) {
            alert.setContentText("Check the lower bound value: since the variable is non-negative, " +
                    "the lower limit will be set to 0 instead of " + textLowerBound.getText() + ".");
            alert.showAndWait();
            textLowerBound.setText("0");
            return false;
        } else if ((cbDomainVar.getValue().toString().charAt(0) == '\u2115' || cbDomainVar.getValue().toString().charAt(0) == '\u2124')
                && textLowerBound.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textLowerBound.getText());
            alert.setContentText("Check the lower bound value: since " + cbLetterVar.getValue().toString() +
                    " \u2208 " + cbDomainVar.getValue().toString().charAt(0) + " the lower limit will be set to " +
                    integer + " instead of " + textLowerBound.getText() + ".");
            alert.showAndWait();
            textLowerBound.setText(String.valueOf(integer));
            return false;
        } else if ((cbDomainVar.getValue().toString().charAt(0) == '\u2115' || cbDomainVar.getValue().toString().charAt(0) == '\u2124')
                && textUpperBound.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textUpperBound.getText());
            alert.setContentText("Check the upper bound value: since " + cbLetterVar.getValue().toString() +
                    " \u2208 " + cbDomainVar.getValue().toString().charAt(0) + " the upper limit will be set to " +
                    integer + " instead of " + textUpperBound.getText() + ".");
            alert.showAndWait();
            textUpperBound.setText(String.valueOf(integer));
            return false;
        } else if (textLowerBound.getText().length() > 0 && textUpperBound.getText().length() > 0 &&
                Double.parseDouble(textLowerBound.getText()) >= Double.parseDouble(textUpperBound.getText())) {
            alert.setContentText("Lower bound has to be set to a value lower than the upper bound.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean checkBoundIndexTextFields() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Something is wrong");
        alert.setHeaderText(null);
        if (textStartValue.getText().length() == 0 || textEndValue.getText().length() == 0) {
            alert.setContentText("Start and End values must be both filled.");
            alert.showAndWait();
            return false;
        } else if (textStartValue.getText().matches("[-+]$|.*\\.$")) {
            alert.setContentText("Invalid start value.");
            alert.showAndWait();
            return false;
        } else if (textEndValue.getText().matches("[-+]$|.*\\.$")) {
            alert.setContentText("Invalid end value.");
            alert.showAndWait();
            return false;
        } else if ((cbSet.getValue().toString().charAt(0) == '\u2115' || cbSet.getValue().toString().charAt(0) == '\u2124')
                && textStartValue.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textStartValue.getText());
            alert.setContentText("Check the start value: since " + cbLetterIndex.getValue().toString() +
                    " \u2208 " + cbSet.getValue().toString().charAt(0) + " the lower limit will be set to " +
                    integer + " instead of " + textStartValue.getText() + ".");
            alert.showAndWait();
            textStartValue.setText(String.valueOf(integer));
            return false;
        } else if ((cbSet.getValue().toString().charAt(0) == '\u2115' || cbSet.getValue().toString().charAt(0) == '\u2124')
                && textEndValue.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textEndValue.getText());
            alert.setContentText("Check the end value: since " + cbLetterIndex.getValue().toString() +
                    " \u2208 " + cbSet.getValue().toString().charAt(0) + " the upper limit will be set to " +
                    integer + " instead of " + textEndValue.getText() + ".");
            alert.showAndWait();
            textEndValue.setText(String.valueOf(integer));
            return false;
        } else if (textStartValue.getText().length() > 0 && textEndValue.getText().length() > 0 &&
                Double.parseDouble(textStartValue.getText()) >= Double.parseDouble(textEndValue.getText())) {
            alert.setContentText("Start value has to be set to a value lower than the end value.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void cancelVar() {
        disableVariableFields(true);
        resetVarFields();
        if (unusedLetters.size() > 0)
            buttonNewVariable.setDisable(false);
        buttonEditVariable.setText("Edit");
        buttonEditVariable.setDisable(true);
        buttonCancelVariable.setDisable(true);
    }

    public void cancelCoef() {
        disableCoefFields(true);
        resetCoefFields();
        if (unusedLetters.size() > 0)
            buttonNewCoefficient.setDisable(false);
        buttonEditCoef.setText("Edit");
        buttonEditCoef.setDisable(true);
        buttonCancelCoef.setDisable(true);
        buttonNewData.setDisable(true);
        buttonEditData.setDisable(true);
        mCoefficientBeingUsed = null;
    }

    public void cancelIndexClicked() {
        resetIndexFields();
        disableIndexFields(true);
        if (unusedIndexLetters.size() > 0)
            buttonNewIndex.setDisable(false);
        buttonEditIndex.setText("Edit");
        buttonEditIndex.setDisable(true);
    }

    private void resetIndexFields() {
        textStartValue.setText("");
        textEndValue.setText("");
        if (unusedIndexLetters.size() > 7)
            cbLetterIndex.setValue(cbLetterIndex.getItems().get(8));
    }

    private void disableVariableFields(boolean arg) {
        cbDomainVar.setDisable(arg);
        lblDomainVar.setDisable(arg);
        checkNonNegative.setDisable(arg);
        textUpperBound.setDisable(arg);
        lblUpperBound.setDisable(arg);
        textLowerBound.setDisable(arg);
        lblLowerBound.setDisable(arg);
        cbLetterVar.setDisable(arg);
        lblLetterVar.setDisable(arg);
        cbDimensionVar.setDisable(arg);
        lblDimensionVar.setDisable(arg);
        // the opposite of var fields
        accordionBase.setDisable(!arg);
        tabCoefData.setDisable(!arg);
        tabIndexSet.setDisable(!arg);
        tabModel.setDisable(!arg);
        scrollPane.setDisable(!arg);
    }

    private void disableCoefFields(boolean arg) {
        lblLetterCoef.setDisable(arg);
        cbLetterCoef.setDisable(arg);
        lblData.setDisable(arg);
        // the opposite of var fields
        accordionBase.setDisable(!arg);
        tabVar.setDisable(!arg);
        tabIndexSet.setDisable(!arg);
        tabModel.setDisable(!arg);
        scrollPane.setDisable(!arg);
    }

    private void disableIndexFields(boolean arg) {
        buttonCancelIndex.setDisable(arg);
        lblLetterIndex.setDisable(arg);
        cbLetterIndex.setDisable(arg);
        lblPreDefSets.setDisable(arg);
        cbSet.setDisable(arg);
        lblStartValue.setDisable(arg);
        textStartValue.setDisable(arg);
        lblEndValue.setDisable(arg);
        textEndValue.setDisable(arg);
        // the opposite of var fields
        accordionBase.setDisable(!arg);
        tabVar.setDisable(!arg);
        tabCoefData.setDisable(!arg);
        tabModel.setDisable(!arg);
        scrollPane.setDisable(!arg);
    }

    public void closeWindow() {
        mMain.getPrimaryStage().close();
    }

    private void addNewFormula(Formula formulaArg) throws Exception {
        // Setting initial buttons
        javafx.scene.control.Button buttonDesignFormula = new javafx.scene.control.Button("Design Formula");
        javafx.scene.control.Button buttonLateX = new Button("LateX Formula");
        javafx.scene.control.Button buttonStopEditing = new Button("Stop editing");
        javafx.scene.control.Button buttonDelete = new Button("Delete");    // just on constraints
        formulaArg.getVBox().setAlignment(Pos.CENTER);
        formulaArg.getVBox().setSpacing(5);
        formulaArg.getVBox().getChildren().addAll(buttonDesignFormula, buttonLateX, buttonDelete);
//        if ((formulaArg.getMathElements().isEmpty()) &&
//                (formulaArg.getYStart() <= FormulasPositionSet.mFirstConstraintStartYPosition))
            buttonDelete.setDisable(true);

        // Button's action
        buttonDesignFormula.setOnMouseClicked(event -> {
            // Disabling other nodes
            buttonExportLP.setDisable(true);
            tabPane.getSelectionModel().select(tabModel);
            tabVar.setDisable(true);
            tabCoefData.setDisable(true);
            tabIndexSet.setDisable(true);
            accordionBase.setDisable(true);
            lblMainFunction.setDisable(true);
            rdBtnMin.setDisable(true);
            rdBtnMax.setDisable(true);
            lblConstraints.setDisable(true);
            formulaArg.setActiveEditing(true);
            formulaArg.getVBox().getChildren().removeAll(buttonDesignFormula, buttonLateX, buttonDelete);
            formulaArg.getVBox().getChildren().add(buttonStopEditing);
            AnchorPane.setTopAnchor(formulaArg.getVBox(), (double)formulaArg.getAlignment()-15);
            // Disabling all the other buttons
            for (Formula formula : formulas){
                if (formula.getId() != formulaArg.getId())
                    formula.getVBox().setDisable(true);
            }
            // Setting drag and drop area
            if (formulaArg.getMathElements().isEmpty()) {
                canvas.getGraphicsContext2D().setTextAlign(TextAlignment.LEFT);
                canvas.getGraphicsContext2D().setFont(new javafx.scene.text.Font("System", 15));
                canvas.getGraphicsContext2D().fillText("Drag and drop math elements here",
                        FormulasPositionSet.mMainFormulaStartXPosition, formulaArg.getAlignment());
            }
        });

        buttonStopEditing.setOnMouseClicked(event -> {
            // enabling all the other buttons
            for (Formula formula : formulas){
                if (formula.getId() != formulaArg.getId())
                    formula.getVBox().setDisable(false);
            }
            tabVar.setDisable(false);
            tabCoefData.setDisable(false);
            tabIndexSet.setDisable(false);
            accordionBase.setDisable(false);
            lblMainFunction.setDisable(false);
            rdBtnMin.setDisable(false);
            rdBtnMax.setDisable(false);
            lblConstraints.setDisable(false);
            formulaArg.setActiveEditing(false);
            formulaArg.getVBox().getChildren().remove(buttonStopEditing);
            if (!formulaArg.getMathElements().isEmpty()) {
                buttonExportLP.setDisable(false);
                buttonDesignFormula.setText("Edit Formula");
                buttonLateX.setText("Edit Latex Formula");
                if (!formulaArg.isMainFunction())
                    buttonNewConstraint.setDisable(false);
                buttonDelete.setDisable(false);
            } else {
                buttonDelete.setDisable(true);
                // Just to clear sentence to drag math models
                drawFormulas();
            }

            formulaArg.getVBox().getChildren().addAll(buttonDesignFormula, buttonLateX, buttonDelete);
            AnchorPane.setTopAnchor(formulaArg.getVBox(), (double)formulaArg.getYStart());
        });

        buttonLateX.setOnMouseClicked(e -> {
            Dialogs dialogs = new Dialogs();
            Optional<String> result = dialogs.latexEntryDialog();
            result.ifPresent(name -> {
                // TODO: parse latex entry and put into the formula
//                System.out.println("Latex Entry: " + name);
            });
        });

        buttonDelete.setOnMouseClicked(event -> {
            if (formulaArg.isMainFunction()) {
                formulaArg.getMathElements().removeAll(formulaArg.getMathElements());
                buttonDesignFormula.setText("Design Formula");
                buttonLateX.setText("Latex Formula");
                drawFormulas();
                buttonDelete.setDisable(true);
                return;
            }
            formulaArg.getVBox().getChildren().removeAll(buttonDelete, buttonDesignFormula, buttonLateX);
            // Repositioning buttons
            for (Formula formula : formulas) {
                if (formula.getYStart() > formulaArg.getYStart())
                    AnchorPane.setTopAnchor(formula.getVBox(), (double)formula.getYStart() -
                        FormulasPositionSet.getTotalConstraintVerticalSpace());
            }
            removeConstraint(formulaArg);
        });

        // Positioning formula buttons
        if( !formulas.add(formulaArg) ) {
            throw new Exception("Cannot add formula! Check y start value");
        }
        innerAnchorPane.getChildren().add(formulaArg.getVBox());
        AnchorPane.setRightAnchor(formulaArg.getVBox(), 30.0);
        AnchorPane.setTopAnchor(formulaArg.getVBox(), (double)formulaArg.getYStart());

        // adjusting button new constraint
        if (!formulaArg.isMainFunction()) {
            buttonNewConstraint.setDisable(true);
        }
        // resizing height of canvas if needed
        resizeCanvas();
    }

    private void removeConstraint(Formula formulaArg) {
        if (formulaArg.isMainFunction()) {
//            System.out.println("Impossible to remove Main Function");
            return;
        }
        if (!formulas.remove(formulaArg)) {
//            System.out.println("Impossible to remove this formula! Check y start.");
            return;
        }
        for (Formula formula : formulas){
            if (formula.getYStart() > formulaArg.getYStart()){
                formula.setYStart(formula.getYStart()-FormulasPositionSet.getTotalConstraintVerticalSpace());
            }
        }
        if (formulas.size() <= 1){  // Always have at least one constraint
            Formula newConstraint = new Formula(FormulasPositionSet.mConstraintStartXPosition,
                    FormulasPositionSet.mFirstConstraintStartYPosition +
                            ((formulas.size()-1)*FormulasPositionSet.getTotalConstraintVerticalSpace()),
                    false);
            try {
                addNewFormula(newConstraint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        drawFormulas();
        resizeCanvas();
    }

    private void resizeCanvas() {
        int roomAfter = 100;
        int actualHeightSizeNeeded = roomAfter + formulas.last().getAlignment() +
                FormulasPositionSet.getTotalConstraintVerticalSpace();
        canvas.setHeight(actualHeightSizeNeeded + roomAfter);
        innerAnchorPane.setPrefHeight(canvas.getHeight());
//        System.out.println("After resize - innerAnchorPane = " + innerAnchorPane.getHeight());
//        System.out.println("Canvas height = " + canvas.getHeight());
    }

    private void drawFormulas() {
        clearCanvas();

        // Horizontal line separator
        double yPos = FormulasPositionSet.getMainFormulaYEndPosition() +
                (FormulasPositionSet.mFirstConstraintStartYPosition -
                FormulasPositionSet.getMainFormulaYEndPosition())/2.0;
        canvas.getGraphicsContext2D().strokeLine(0, yPos, canvas.getWidth(), yPos);

        if (formulas == null)
            return;

        int i = 0;
        for (Formula formula : formulas) {
            formula.correctIndexes();
            for (MathElement mathElement : formula.getMathElements()) {
                // now create an actual image of the rendered equation
                drawMathElement(mathElement, formula.getAlignment());
            }
            if (i>0 && !formula.getMathElements().isEmpty()) {
                canvas.getGraphicsContext2D().setTextAlign(TextAlignment.LEFT);
                canvas.getGraphicsContext2D().setFont(new javafx.scene.text.Font("System", 15));
                canvas.getGraphicsContext2D().fillText(i+".",
                        FormulasPositionSet.mConstraintStartXPosition-50, formula.getAlignment());
            }
            i++;
        }
    }

    private void drawMathElement(MathElement mathElement, int alignment) {
        if (mathElement == null) return;
        mathElement.updateIcon(alignment);
        BufferedImage image = mathElement.prepareToDrawME();

        // at this point the image is created, you could also save it with ImageIO
        this.g2.drawImage(image, mathElement.getXStart(), mathElement.getYStart(), null);
    }

    private void clearCanvas() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
    }

    private void makeModelBasicElements() {
        Vector<MathElement> mathElements = new Vector<>();

        Summation somatorio = new Summation();
        Constant constant = new Constant();
        constant.setFloat(5);
        Sum sum = new Sum();
        Subtraction subtraction = new Subtraction();
        Equal equal = new Equal();
        LessOrEqual lessOrEqual = new LessOrEqual();
        GreaterOrEqual greaterOrEqual = new GreaterOrEqual();
        OpenParenthesis openParenthesis = new OpenParenthesis();
        CloseParenthesis closeParenthesis = new CloseParenthesis();

        MathElement mathElementA = new MathElement(somatorio);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(constant);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(sum);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(subtraction);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(equal);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(lessOrEqual);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(greaterOrEqual);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(openParenthesis);
        mathElements.add(mathElementA);
        mathElementA = new MathElement(closeParenthesis);
        mathElements.add(mathElementA);

        double vAlignment = 20.0;
        double hPosition = 200.0;
        for (MathElement mathElement : mathElements) {
            addMathElementToModel(mathElement, hbBasic);
            hPosition += mathElement.getWidth() + 1;
        }
    }

    private void addMathElementToModel(MathElement mathElement, HBox hBox) {
        hBox.getChildren().add(mathElement.getImageView());

        // programing drag and drop settings
        mathElement.getImageView().setOnDragDetected(event -> {
                /* drag was detected, start a drag-and-drop gesture*/
                /* allow any transfer mode */
            Dragboard db = mathElement.getImageView().startDragAndDrop(TransferMode.ANY);
                /* Put a String on a drag board */
            beingDragged = new MathElement(mathElement.getExpression());
            ClipboardContent content = new ClipboardContent();
            content.putString("Is valid");
            db.setContent(content);
            event.consume();
        });
    }

    private void refreshVarsInModel() {
        hbVariables.getChildren().clear();
        MathElement meVariable = null;
        for (Variable variable : mVariables) {
            meVariable = new MathElement(variable);
            addMathElementToModel(meVariable, hbVariables);
        }
    }

    private void refreshCoefsInModel() {
        hbCoefficients.getChildren().clear();
        MathElement meCoefficient = null;
        for (model.Coefficient coefficient : mCoefficients) {
            meCoefficient = new MathElement(coefficient);
            addMathElementToModel(meCoefficient, hbCoefficients);
        }
    }

    private Formula getFormula(int yPosition) {
        for (Formula formula : formulas) {
            if (Formula.isBetween(yPosition, formula.getYStart(), formula.getYEnd()))
                return formula;
        }
        return null;
    }

    public void openDataWindow() {
        mCoefficientBeingUsed = new Coefficient(cbLetterCoef.getValue().toString().charAt(0));
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/dataWindowView.fxml"));
        AnchorPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        DataWindowController dataWindowController = loader.getController();
        try {
            dataWindowController.setMain(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(pane);      // takes a pane as an argument
        //scene.getStylesheets().add(getClass().getResource("../view/styles.css").toExternalForm());
        Stage dataStage = new Stage();
        dataStage.initModality(Modality.APPLICATION_MODAL); // cannot interact with this window
        dataStage.setTitle("New data");
        dataStage.setScene(scene);       // set scene to the stage
        dataStage.setResizable(false);
        dataStage.show();
    }

    public void editDataClicked() {
        // TODO: edit Data feature. Un-parse double? Add observableList to Coefficient class?
    }

    public void newIndexClicked() {
        disableIndexFields(false);
        resetIndexFields();
        lblPromptIndex.setText("New index");
        buttonEditIndex.setText("Confirm");
        buttonEditIndex.setDisable(false);
        buttonNewIndex.setDisable(true);
        editIndexFlag = false;
    }

    public void exportLPClicked() {
        LPFileGenerator lpFileGenerator = new LPFileGenerator();
        lpFileGenerator.saveLPFile(this);
    }

    public Coefficient getCoefVarData(char letter) {
        for (Coefficient coefficient : mCoefficients) {
            if (coefficient.getLetter() == letter) {
                return coefficient;
            }
        }
        return null;
    }

    public SumIndex getIndexData (char letter) {
        for (SumIndex sumIndex : mIndexes) {
            if (sumIndex.getLetter() == letter) {
                return sumIndex;
            }
        }
        return null;
    }
}