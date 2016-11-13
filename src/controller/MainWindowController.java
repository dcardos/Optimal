package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    private MathElement beingDragged;

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

        TextInputDialog dialog = new TextInputDialog("0");

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        Formula formula = getFormula((int)t.getY());
                        if (null == formula) return;
                        MathElement mathElement = formula.getMathElement(((int)t.getX()));
                        if (null == mathElement) return;
                        // To be even more precise about the element height
                        if (Formula.isBetween((int)t.getY(),
                                mathElement.getYStart(), mathElement.getYEnd())) {
                            drawMathElement(formula.turnColorBackTo(Color.black));
                            if (mathElement.getExpression() instanceof Summation) {
                                Dialogs dialogs = new Dialogs();
                                Optional<Pair<String, String>> result = dialogs.summationDialog();
                                result.ifPresent(indexes -> {
                                    System.out.println("SP=" + indexes.getKey() + ", EP=" + indexes.getValue());
                                    ((Summation) mathElement.getExpression())
                                        .setStartingPointFromPrimitives("i", Integer.valueOf(indexes.getKey()));
                                    ((Summation) mathElement.getExpression())
                                            .setStoppingPointFromInt(Integer.valueOf(indexes.getValue()));
                                    drawMathElement(mathElement);
                                    formula.setLastMathElementModified(mathElement);
                                    System.out.println(formula);
                                });
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
                    drawMathElement(formula.turnColorBackTo(Color.black));
                    return;
                }
                // To be even more precise about the element height
                if (!Formula.isBetween((int)t.getY(),
                        mathElement.getYStart(), mathElement.getYEnd())) {
                    drawMathElement(formula.turnColorBackTo(Color.black));
                    return;
                }
                // draw only if necessary
                if (mathElement.getColor() != Color.red) {
                    if (formula.isRedFlag())
                        drawMathElement(formula.turnColorBackTo(Color.black));
                    mathElement.setColor(Color.red);
                    formula.setRedFlag(true);
                    formula.setLastMathElementModified(mathElement);
                    drawMathElement(mathElement);
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
                    // To be even more precise about the element height
//                    if (!Formula.isBetween((int)event.getY(),
//                            mathElement.getYStart(), mathElement.getYEnd())) {
//                        drawMathElement(formula.turnColorBackTo(Color.black));
//                        return;
//                    }
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
                    formulas.first().getMathElement(beingDragged.getXStart()).setColor(Color.black);
                    drawFormula(formulas.first());
                    beingDragged = null;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });


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

    }

    public void closeWindow() {
        mMain.getPrimaryStage().close();
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
//        To clear canvas:
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

        for (MathElement mathElement : formula.getMathElements()) {
            // now create an actual image of the rendered equation
            drawMathElement(mathElement);
        }
    }

    private void drawMathElement(MathElement mathElement) {
        if (mathElement == null) return;
        mathElement.updateIcon();
//        To clear canvas:
//        double width = canvas.getWidth();
//        double height = canvas.getHeight();
//        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

        BufferedImage image = prepareToDrawME(mathElement);

        // at this point the image is created, you could also save it with ImageIO
        this.g2.drawImage(image, mathElement.getXStart(), mathElement.getYStart(), null);
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