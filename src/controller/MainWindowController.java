package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import model.Formula;
import model.MathElement;
import model.Variable;
import model.math.*;
import org.jetbrains.annotations.Contract;
import org.jfree.fx.FXGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Vector;

public class MainWindowController {

    private Main mMain;
    private FXGraphics2D g2;

    private TreeSet<Formula> formulas;
    private Formula lastModifiedFormula;
    private MathElement beingDragged;
    private final ArrayList<Variable> mVariables = new ArrayList<>();
    private final ArrayList<model.Coefficient> mCoefficients = new ArrayList<>();

    private final ToggleGroup mMaxMin = new ToggleGroup();

    private boolean newVariableFlag;
    private boolean newCoefficientFlag;
    private boolean editVariableFlag;
    private boolean editCoefficientFlag;
    private int indexVarOrCoef;
    private final ObservableList<String> observableVariableList = FXCollections.observableArrayList();
    private final ObservableList<String> observableCoefficientList = FXCollections.observableArrayList();
    private final ObservableList<String> observableDomainList = FXCollections.observableArrayList(
            "\u2115  Natural", "\u2124  Integer", "\u211a  Rational", "\u211d  Real");
    private final ObservableList<String> observableDimensionList = FXCollections.observableArrayList(
            "1", "2", "3", "4", "5");
    private final ObservableList<String> letters = FXCollections.observableArrayList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");


    @FXML private Label lblPrompt;
    @FXML private Label lblLetter;
    @FXML private Label lblDomain;
    @FXML private Label lblDimension;
    @FXML private Label lblUpperBound;
    @FXML private Label lblLowerBound;
    @FXML private Canvas canvas;
    @FXML private AnchorPane innerAnchorPane;
    @FXML private AnchorPane anchorPaneModel;
    @FXML private RadioButton rdBtnMax;
    @FXML private RadioButton rdBtnMin;
    @FXML private Label lblConstraints;
    @FXML private TabPane tabPane;
    @FXML private Tab tabVar;
    @FXML private Tab tabData;
    @FXML private Tab tabModel;
    @FXML private ScrollPane scrollPane;
    @FXML private Button buttonNewConstraint;
    // variable tab
    @FXML private Button buttonNewVariable;
    @FXML private Button buttonCancelVariable;
    @FXML private Button buttonEditVariable;
    @FXML private Button buttonNewCoefficient;
    @FXML private ComboBox cbDomain;
    @FXML private CheckBox checkNonNegative;
    @FXML private ComboBox cbLetter;
    @FXML private ComboBox cbDimension;
    @FXML private TextField textUpperBound;
    @FXML private TextField textLowerBound;
    // Accordion and TitledPanes
    @FXML private Accordion accordionBase;
    @FXML private TitledPane tpVariables;
    @FXML private TitledPane tpCoefficients;
    @FXML private TitledPane tpSets;
    @FXML private TitledPane tpData;
    @FXML private ListView lvVariables;
    @FXML private ListView lvCoefficients;


    public void setMain(Main main) throws Exception {
        mMain = main;

        // Initializing variables and fields
        newVariableFlag = false;
        newCoefficientFlag = false;
        editCoefficientFlag = false;
        editVariableFlag = false;
        buttonCancelVariable.setDisable(true);
        buttonEditVariable.setDisable(true);
        disableVariableFields(true, false);
        lvVariables.setItems(observableVariableList);
        lvCoefficients.setItems(observableCoefficientList);
        cbDomain.setItems(observableDomainList);
        cbDimension.setItems(observableDimensionList);
        cbLetter.setItems(letters);
        resetVarCoefFields();
        lblPrompt.setText("");

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

        // Adding listeners to the listView on titled pane
        lvVariables.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                indexVarOrCoef = lvVariables.getSelectionModel().getSelectedIndex();
                if (indexVarOrCoef > -1) {
                    editVariableFlag = true;
                    editCoefficientFlag = false;
                    cbDomain.setValue(mVariables.get(indexVarOrCoef).getDomain());
                    checkNonNegative.setSelected(mVariables.get(indexVarOrCoef).isNonNegative());
                    cbLetter.setValue(mVariables.get(indexVarOrCoef).getLetter());
                    cbDimension.setValue(mVariables.get(indexVarOrCoef).getDimension());
                    String bound;
                    if (mVariables.get(indexVarOrCoef).getUpperBound() != null) {
                        bound = String.valueOf(mVariables.get(indexVarOrCoef).getUpperBound());
                        if (bound.matches(".*\\.0*$"))   // is an integer?
                            bound = bound.replaceAll("\\.0*$", "");
                        textUpperBound.setText(bound);
                    } else
                        textUpperBound.clear();
                    if (mVariables.get(indexVarOrCoef).getLowerBound() != null) {
                        bound = String.valueOf(mVariables.get(indexVarOrCoef).getLowerBound());
                        if (bound.matches(".*\\.0*$"))   // is an integer?
                            bound = bound.replaceAll("\\.0*$", "");
                        textLowerBound.setText(bound);
                    } else
                        textLowerBound.clear();
                }
            }
        });
        lvCoefficients.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                indexVarOrCoef = lvCoefficients.getSelectionModel().getSelectedIndex();
                if (indexVarOrCoef > -1) {
                    editCoefficientFlag = true;
                    editVariableFlag = false;
                    cbLetter.setValue(mCoefficients.get(indexVarOrCoef).getLetter());
                    cbDimension.setValue(mCoefficients.get(indexVarOrCoef).getDimension());
                    textUpperBound.clear();
                    textLowerBound.clear();
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

        // Initializing Model Tab with ME
        Vector<Pair> imageEs = makeModelGridElements();

        for (Pair<ImageView, Expression> pair : imageEs) {
            ImageView imageView = pair.getKey();
            imageView.setOnDragDetected(event -> {
            /* drag was detected, start a drag-and-drop gesture*/
            /* allow any transfer mode */
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
            /* Put a String on a drag board */
                beingDragged = new MathElement(pair.getValue());
                ClipboardContent content = new ClipboardContent();
                content.putString("Is valid");
                db.setContent(content);
                event.consume();
            });
        }

        // context pop menu
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem remove = new MenuItem("Remove");
        MenuItem cancel = new MenuItem("Cancel");
        contextMenu.getItems().addAll(cut, copy, paste, remove, cancel);
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
                contextMenu.show(canvas, t.getScreenX(), t.getScreenY());
            // left click : pop up dialog
            } else {
                // To be even more precise about the element height
                if (Formula.isBetween((int) t.getY(),
                        mathElement.getYStart(), mathElement.getYEnd())) {
                    drawMathElement(formula.turnColorBackTo(Color.black), formula.getAlignment());
                    Dialogs dialogs = new Dialogs();
                    if (mathElement.getExpression() instanceof Summation) {
                        dialogs.callSummationDialog(formula, mathElement);
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
                System.out.println("Dragged did not complete");
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
                System.out.println("Dragged completed successfully!");
                success = true;
                if (lastModifiedFormula == null || !lastModifiedFormula.isActiveEditing()) {
                    System.out.println("lastModifiedFormula == null or not in Editing mode");
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
                    if (!dialogs.callSummationDialog(formulas.first(), mathElement))
                        addME = false;
                } else if (beingDragged.getExpression() instanceof Constant) {
                    Constant constant = new Constant();
                    mathElement = new MathElement(beingDragged, constant);
                    if (!dialogs.callConstantDialog(formulas.first(), mathElement))
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
                }
                if (addME) {
                    try {
                        lastModifiedFormula.addMathElement(mathElement, mathElement.getXStart());
                    } catch (NullPointerException e) {
                        System.out.printf("Invalid MathElement or its X start position");
                        e.printStackTrace();
                    }
                }
                beingDragged = null;
                drawFormulas();
            }
            System.out.println(formulas);
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

    }

    private void resetVarCoefFields() {
        cbDomain.setValue(cbDomain.getItems().get(0));
        cbDimension.setValue(cbDimension.getItems().get(0));
        cbLetter.setValue(cbLetter.getItems().get(23));
        textLowerBound.clear();
        textUpperBound.clear();
        checkNonNegative.setSelected(false);
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
            System.out.println("InnerAnchorPane:" + innerAnchorPane.getHeight());
            scrollPane.setVvalue(1.0);  // scrolling to the end [0, 1]
        }
    }

    public void addNewVariable() {
        resetVarCoefFields();
        newVariableFlag = true;
        lblPrompt.setText("New variable parameters");
        buttonEditVariable.setText("Confirm");
        buttonEditVariable.setDisable(false);
        buttonCancelVariable.setDisable(false);
        buttonNewVariable.setDisable(true);
        buttonNewCoefficient.setDisable(true);
        disableVariableFields(false, false);
    }

    public void addNewCoefficient() {
        resetVarCoefFields();
        newCoefficientFlag = true;
        lblPrompt.setText("New Coefficient parameters");
        buttonEditVariable.setText("Confirm");
        buttonEditVariable.setDisable(false);
        buttonCancelVariable.setDisable(false);
        buttonNewVariable.setDisable(true);
        buttonNewCoefficient.setDisable(true);

        disableVariableFields(false, true);
    }

    public void editVarOrCoef() {
        if (newVariableFlag) {  // confirm button after new variable clicked
            if (checkBoundTextFields()) {
                Variable var = new Variable(cbLetter.getValue().toString().trim().charAt(0),
                        Integer.parseInt(cbDimension.getValue().toString()),
                        cbDomain.getValue().toString().trim().charAt(0), checkNonNegative.isSelected());
                if (textUpperBound.getText().length() > 0)
                    var.setUpperBound(Double.parseDouble(textUpperBound.getText()));
                else
                    var.setUpperBound(null);
                if (textLowerBound.getText().length() > 0)
                    var.setLowerBound(Double.parseDouble(textLowerBound.getText()));
                else
                    var.setLowerBound(null);
                mVariables.add(var);
                observableVariableList.add(var.getLetter() + " \u2208 " + var.getDomain() + ", " + var.getDimension() + " dimension");
                accordionBase.setExpandedPane(tpVariables);
                lvVariables.getSelectionModel().selectLast();   // needed?
                lvVariables.scrollTo(lvVariables.getItems().size() - 1);

                disableVariableFields(true, false);
                buttonEditVariable.setText("Edit");
                buttonNewVariable.setDisable(false);
                buttonNewCoefficient.setDisable(false);
                buttonCancelVariable.setDisable(true);
                newVariableFlag = false;
                lblPrompt.setText("Variable " + var.getLetter() + " selected");
                editVariableFlag = true;
                editCoefficientFlag = false;
                indexVarOrCoef = lvVariables.getItems().size() - 1;
            }
        } else if (newCoefficientFlag) {    // confirm button after new coefficient clicked
            model.Coefficient coef = new model.Coefficient(cbLetter.getValue().toString().charAt(0),
                    Integer.parseInt(cbDimension.getValue().toString()));
            mCoefficients.add(coef);

            observableCoefficientList.add(coef.getLetter() + ", " + coef.getDimension() + " dimension");
            accordionBase.setExpandedPane(tpCoefficients);
            lvCoefficients.getSelectionModel().selectLast();
            lvCoefficients.scrollTo(lvCoefficients.getItems().size()-1);

            disableVariableFields(true, true);
            buttonEditVariable.setText("Edit");
            buttonNewVariable.setDisable(false);
            buttonNewCoefficient.setDisable(false);
            buttonCancelVariable.setDisable(true);
            newCoefficientFlag = false;
            lblPrompt.setText("Coefficient " + coef.getLetter() + " selected");
            editCoefficientFlag = true;
            editVariableFlag = false;
            indexVarOrCoef = lvCoefficients.getItems().size() - 1;
        } else if (buttonEditVariable.getText().equalsIgnoreCase("edit")){    // edit button after
            if (editVariableFlag) {     // a variable is clicked
                if (indexVarOrCoef < mVariables.size()) {   // just reassuring index is within bounds
                    lblPrompt.setText("Editing variable " + mVariables.get(indexVarOrCoef).getLetter());
                    buttonEditVariable.setText("Confirm");
                    buttonCancelVariable.setDisable(false);
                    buttonNewVariable.setDisable(true);
                    buttonNewCoefficient.setDisable(true);
                    disableVariableFields(false, false);
                }
            } else if (editCoefficientFlag) {   // a coefficient is clicked
                if (indexVarOrCoef < mCoefficients.size()) {
                    lblPrompt.setText("Editing coefficient " + mCoefficients.get(indexVarOrCoef).getLetter());
                    buttonEditVariable.setText("Confirm");
                    buttonCancelVariable.setDisable(false);
                    buttonNewVariable.setDisable(true);
                    buttonNewCoefficient.setDisable(true);
                    disableVariableFields(false, true);
                }
            }
        } else {    // confirm an edition
            if (editVariableFlag) {     // done editing variable
                if (checkBoundTextFields()) {
                    mVariables.get(indexVarOrCoef).setLetter(cbLetter.getValue().toString().charAt(0));
                    mVariables.get(indexVarOrCoef).setDimension(Integer.parseInt(cbDimension.getValue().toString()));
                    mVariables.get(indexVarOrCoef).setDomain(cbDomain.getValue().toString().trim().charAt(0));
                    mVariables.get(indexVarOrCoef).setNonNegative(checkNonNegative.isSelected());
                    if (textUpperBound.getText().length() > 0)
                        mVariables.get(indexVarOrCoef).setUpperBound(Double.parseDouble(textUpperBound.getText()));
                    if (textLowerBound.getText().length() > 0)
                        mVariables.get(indexVarOrCoef).setLowerBound(Double.parseDouble(textLowerBound.getText()));
                    Variable var = mVariables.get(indexVarOrCoef);
                    observableVariableList.set(indexVarOrCoef,
                            var.getLetter() + " \u2208 " + var.getDomain() + ", " + var.getDimension() + " dimension");
                    disableVariableFields(true, false);
                    buttonEditVariable.setText("Edit");
                    buttonNewVariable.setDisable(false);
                    buttonNewCoefficient.setDisable(false);
                    buttonCancelVariable.setDisable(true);
                    lblPrompt.setText("");
                    editCoefficientFlag = false;
                }
            } else if (editCoefficientFlag) {   // done editing coefficient
                mCoefficients.get(indexVarOrCoef).setLetter(cbLetter.getValue().toString().charAt(0));
                mCoefficients.get(indexVarOrCoef).setDimension(Integer.parseInt(cbDimension.getValue().toString()));
                model.Coefficient coef = mCoefficients.get(indexVarOrCoef);
                observableCoefficientList.set(indexVarOrCoef,
                        coef.getLetter() + ", " + coef.getDimension() + " dimension");
                disableVariableFields(true, true);
                buttonEditVariable.setText("Edit");
                buttonNewVariable.setDisable(false);
                buttonNewCoefficient.setDisable(false);
                buttonCancelVariable.setDisable(true);
                lblPrompt.setText("");
                editVariableFlag = false;
            }
        }
    }

    private boolean checkBoundTextFields() {
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
        } else if ((cbDomain.getValue().toString().charAt(0) == '\u2115' || cbDomain.getValue().toString().charAt(0) == '\u2124')
                && textLowerBound.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textLowerBound.getText());
            alert.setContentText("Check the lower bound value: since " + cbLetter.getValue().toString() +
                    " \u2208 " + cbDomain.getValue().toString().charAt(0) + " the lower limit will be set to " +
                    integer + " instead of " + textLowerBound.getText() + ".");
            alert.showAndWait();
            textLowerBound.setText(String.valueOf(integer));
            return false;
        } else if ((cbDomain.getValue().toString().charAt(0) == '\u2115' || cbDomain.getValue().toString().charAt(0) == '\u2124')
                && textUpperBound.getText().contains(".")) {
            int integer = (int)Double.parseDouble(textUpperBound.getText());
            alert.setContentText("Check the upper bound value: since " + cbLetter.getValue().toString() +
                    " \u2208 " + cbDomain.getValue().toString().charAt(0) + " the upper limit will be set to " +
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

    public void cancelVarOrCoef() {}

    private void disableVariableFields(boolean arg, boolean coef) {
        if (!coef) {    // change these when variable
            cbDomain.setDisable(arg);
            lblDomain.setDisable(arg);
            checkNonNegative.setDisable(arg);
            textUpperBound.setDisable(arg);
            lblUpperBound.setDisable(arg);
            textLowerBound.setDisable(arg);
            lblLowerBound.setDisable(arg);
        }
        cbLetter.setDisable(arg);
        lblLetter.setDisable(arg);
        cbDimension.setDisable(arg);
        lblDimension.setDisable(arg);
        // the opposite of var or coefficient fields
        accordionBase.setDisable(!arg);
        tabData.setDisable(!arg);
        tabModel.setDisable(!arg);
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
            tabPane.getSelectionModel().select(tabModel);
            tabVar.setDisable(true);
            tabData.setDisable(true);
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
            tabData.setDisable(false);
            formulaArg.setActiveEditing(false);
            formulaArg.getVBox().getChildren().remove(buttonStopEditing);
            if (!formulaArg.getMathElements().isEmpty()) {
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
                System.out.println("Latex Entry: " + name);
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
            System.out.println("Cannot add formula! Check y start value");
            throw new Exception();
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
            System.out.println("Impossible to remove Main Function");
            return;
        }
        if (!formulas.remove(formulaArg)) {
            System.out.println("Impossible to remove this formula! Check y start.");
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
        BufferedImage image = prepareToDrawME(mathElement);

        // at this point the image is created, you could also save it with ImageIO
        this.g2.drawImage(image, mathElement.getXStart(), mathElement.getYStart(), null);
    }

    private void clearCanvas() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
    }

    private Vector<Pair> makeModelGridElements() {
        Pair<ImageView, Expression> pair;
        Vector<Pair> imageEs = new Vector<>();
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

        double vAlignment = 50.0;
        double position = 400.0;
        for (MathElement mathElement : mathElements) {
            BufferedImage image = prepareToDrawME(mathElement);
            // writing image buffer to image
            WritableImage wr = null;
            if (image != null) {
                wr = new WritableImage(image.getWidth(), image.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        pw.setArgb(x, y, image.getRGB(x, y));
                    }
                }
            }
            // creating a imageView element
            ImageView imageView = new ImageView();
            imageView.setImage(wr);
            anchorPaneModel.getChildren().add(imageView);
            AnchorPane.setLeftAnchor(imageView, position);
            AnchorPane.setTopAnchor(imageView,vAlignment-(mathElement.getHeight()/2));
            position += mathElement.getWidth() + 1;
            pair = new Pair<>(imageView, mathElement.getExpression());
            imageEs.add(pair);
        }
        return imageEs;
    }

    @Contract("null -> fail")
    private BufferedImage prepareToDrawME(MathElement mathElement) {
        // now create an actual image of the rendered equation
        assert (null != mathElement);
        BufferedImage image = new BufferedImage(mathElement.getWidth(),
                mathElement.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = image.createGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, mathElement.getWidth(), mathElement.getHeight());
        JLabel jl = new JLabel();
        jl.setForeground(mathElement.getColor());
        mathElement.getIcon().paintIcon(jl, gg, 0, 0);
        return image;
    }

    public Formula testFormula() {
        // create a formula
        Constant dois = new Constant();
        Constant um = new Constant();
        Coefficient menosN = new Coefficient();
        Coefficient azao = new Coefficient();

        dois.setFloat(2);
        um.setFloat(1);
        menosN.setLetter("-n");
        Power power = new Power();
        power.setLeftExpression(dois);
        power.setRightExpression(menosN);

        azao.setLetter("A");
        azao.addIndex("n");
        azao.addIndex("j");
        azao.addIndex("2");
        Sum soma = new Sum();
        soma.setLeftExpression(power);
        soma.setRightExpression(azao);

        Equal nIgual1 = new Equal();
        Coefficient ene = new Coefficient();
        Infinity infinito = new Infinity();
        ene.setLetter("n");
        nIgual1.setLeftExpression(ene);
        nIgual1.setRightExpression(um);
        Summation somatorio = new Summation();
        somatorio.setStartingPoint(nIgual1);
        somatorio.setStoppingPoint(infinito);
//        somatorio.setExpression(soma);

        Equal expIgual1 = new Equal();
        expIgual1.setLeftExpression(somatorio);
        expIgual1.setRightExpression(um);

        // formula division
        Formula formula = new Formula(100, 30, true);
        MathElement mathElementS = new MathElement(somatorio);
        formula.addMathElementAtTheEnd(mathElementS);
        MathElement mathElementP = new MathElement(power);
        formula.addMathElementAtTheEnd(mathElementP);
        formula.addMathElementAtTheEnd(new MathElement(new Sum()));
        formula.addMathElementAtTheEnd(new MathElement(azao));

//        formula.removeMathElement(mathElementP);
//        formula.addMathElement(new MathElement(azao), 0);

//        System.out.println(formula);

        return formula;
    }

    private Formula getFormula(int yPosition) {
        for (Formula formula : formulas) {
            if (Formula.isBetween(yPosition, formula.getYStart(), formula.getYEnd()))
                return formula;
        }
        return null;
    }
}