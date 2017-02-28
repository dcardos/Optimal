package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import model.Formula;
import model.MathElement;
import model.math.*;
import org.jetbrains.annotations.Contract;
import org.jfree.fx.FXGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Vector;

public class MainWindowController {

    private Main mMain;
    private FXGraphics2D g2;

    private TreeSet<Formula> formulas;
    private Formula lastModifiedFormula;
    private MathElement beingDragged;
    boolean resultFromDialogs;

    @FXML private Label labelEditVariable;

    @FXML private Canvas canvas;

    @FXML private AnchorPane innerAnchorPane;

    @FXML private AnchorPane anchorPaneModel;

    public void setMain(Main main) {
        mMain = main;
        labelEditVariable.setText("Editing Sum: \u2211");
        innerAnchorPane.setStyle("-fx-background-color: #ffe1c5");

        // Initializing fonts for LateX
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmex10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);

        // Initializing formulas
        this.formulas = new TreeSet<>();

        // Initializing canvas
        initializeCanvas();

        // Initializing Model Tab with ME
        Vector<Pair> imageEs = makeModelGridElements();

        // formula buttons
        HBox hbButtons = new HBox();
        hbButtons.setSpacing(10.0);
        Button buttonRemove = new Button("X");
        Button buttonLateX = new Button("Entry Formula");
        hbButtons.getChildren().addAll(buttonLateX, buttonRemove);
        //buttonRemove.setLayoutX(460);
        //buttonRemove.setLayoutY(5);
        innerAnchorPane.getChildren().add(hbButtons);
        innerAnchorPane.setRightAnchor(hbButtons, 30.0);
        innerAnchorPane.setTopAnchor(hbButtons, Double.valueOf(formulas.first().getAlignment()) - 15);

        buttonLateX.setOnAction(e -> fillFormulaFromLatexEntry());

        for (Pair<ImageView, Expression> pair : imageEs) {
            ImageView imageView = pair.getKey();
            imageView.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                /* drag was detected, start a drag-and-drop gesture*/
                /* allow any transfer mode */
                    Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
                /* Put a String on a drag board */
                    beingDragged = new MathElement(pair.getValue());
                    ClipboardContent content = new ClipboardContent();
                    content.putString("Is valid");
                    db.setContent(content);
                    event.consume();
                }
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
        cut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("cut clicked");
            }
        });
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastModifiedFormula.removeMathElement(lastModifiedFormula.getLastMathElementModified());
                drawFormula(lastModifiedFormula);
                lastModifiedFormula.setLastMathElementModified(null);
                lastModifiedFormula = null;
            }
        });

        // left/right click over canvas
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
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
                        drawFormula(formula);
                    }
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
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
            }
        });

        canvas.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
                    if (null == formula) return;
                    MathElement mathElement = formula.getMathElement(((int)event.getX()));
                    if (null == mathElement) {
                        return;
                    }
                    // inside the formula, over a mathElement area
                    if (formula.getLastMathElementModified() != beingDragged) { // first addition
                        formula.addMathElement(beingDragged, mathElement.getXStart());
                        formula.setLastMathElementModified(beingDragged);
                        drawFormula(formula);
                    } else {
//                        System.out.println("Current x: " + event.getX() + "ME X Start: " + mathElement.getXStart());
                        if (mathElement.getXStart() < beingDragged.getXStart()
                                && event.getX() <= mathElement.getXCenter()) { // moving left
                            formula.removeMathElement(beingDragged);
                            formula.addMathElement(beingDragged, mathElement.getXStart());
                            drawFormula(formula);
                        } else if (mathElement.getXStart() >= beingDragged.getXEnd() &&
                                event.getX() > mathElement.getXCenter()) {    // moving right
                            formula.removeMathElement(beingDragged);
                            formula.addMathElement(beingDragged, mathElement.getXEnd());
                            drawFormula(formula);
                        }
                    }
                }
                event.consume();
            }
        });

        canvas.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // TODO: fix that for any formula
                if (!event.isDropCompleted()) {
                    System.out.println("Dragged did not completed");
                    formulas.first().removeMathElement(beingDragged);
                    drawFormula(formulas.first());
                    beingDragged = null;
                }
                event.consume();
            }
        });

        canvas.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    System.out.println("Dragged completed successfully!");
                    success = true;
                    // TODO: fix for any formula
                    formulas.first().removeMathElement(beingDragged);
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
                    }
                    if (addME)
                        formulas.first().addMathElement(mathElement, mathElement.getXStart());
                    beingDragged = null;
                    drawFormula(formulas.first());
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });

    }

    public void closeWindow() {
        mMain.getPrimaryStage().close();
    }

    private void fillFormulaFromLatexEntry() {
        Formula formula = getFormula(50);
        if (null == formula) return;
        Dialogs dialogs = new Dialogs();
        Optional<String> result = dialogs.latexEntryDialog();
        result.ifPresent(name -> {
            // TODO: parse latex entry and put into the formula
            System.out.println("Latex Entry: " + name);
        });
    }



    private void initializeCanvas() {
        this.g2 = new FXGraphics2D(canvas.getGraphicsContext2D());
        formulas.add(testFormula());
        drawFormula(formulas.first());

        // Redraw canvas when size changes.
//        canvas.widthProperty().addListener(evt -> drawFormula());
//        canvas.heightProperty().addListener(evt -> drawFormula());
    }

    private void drawFormula(Formula formula) {
        clearCanvas();

        // Setting text
        int textXPos = 10;
        canvas.getGraphicsContext2D().setTextAlign(TextAlignment.LEFT);
        canvas.getGraphicsContext2D().setFont(new javafx.scene.text.Font("Calibri", 18));
        canvas.getGraphicsContext2D().fillText("Main Function", textXPos, formulas.first().getYStart() - 10);
        canvas.getGraphicsContext2D().strokeLine(10, formulas.first().getYEnd() + 10,
                canvas.getWidth() - 10, formulas.first().getYEnd() + 10);
        canvas.getGraphicsContext2D().fillText("Constraints", textXPos, formulas.first().getYEnd() + 30);

        formula.correctIndexes();
        for (MathElement mathElement : formula.getMathElements()) {
            // now create an actual image of the rendered equation
            drawMathElement(mathElement, formula.getAlignment());
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
            anchorPaneModel.setLeftAnchor(imageView, position);
            anchorPaneModel.setTopAnchor(imageView,vAlignment-(mathElement.getHeight()/2));
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
        Formula formula = new Formula(0, 40, true);
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