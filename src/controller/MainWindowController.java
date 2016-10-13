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
import model.Formula;
import model.MathElement;
import model.math.*;
import org.jfree.fx.FXGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.TreeSet;

public class MainWindowController {

    private Main mMain;
    private FXGraphics2D g2;
    private TreeSet<Formula> formulas;

    private MathElement beingDragged;

    @FXML private Label labelEditVariable;

    @FXML private Canvas canvas;

    @FXML private ImageView imageView;

    @FXML private AnchorPane innerAnchorPane;

    public void setMain(Main main) {
        mMain = main;
        labelEditVariable.setText("Editing Sum: \u2211");
        innerAnchorPane.setStyle("-fx-background-color: #ffe1c5");

        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmex10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);

        //canvas = new FXGraphics2DDemo3.MyCanvas();

        this.formulas = new TreeSet<>();
        initializeCanvas();


        /* For imageView
        Beginning
         */
        Summation somatorio = new Summation();
        MathElement mathElement = new MathElement(somatorio);
        BufferedImage image = new BufferedImage(mathElement.getWidth(),
                mathElement.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = image.createGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, mathElement.getWidth(), mathElement.getHeight());
        JLabel jl = new JLabel();
        jl.setForeground(mathElement.getColor());
        mathElement.getIcon().paintIcon(jl, gg, 0, 0);
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
        imageView.setImage(wr);
        /* For imageView
        Ending
         */



        TextInputDialog dialog = new TextInputDialog("0");

//        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
//                new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent t) {
//                        System.out.println("X: " + t.getX() + "Y: " + t.getY());
//                        if (t.getClickCount() > 1) { // double click or more
//                            boolean popUpFlag = false;
//                            if (isBetween(t.getX(), 20, 50)) {
//                                dialog.setTitle("Summation");
//                                dialog.setHeaderText("Summation (versão simplona)");
//                                dialog.setContentText("Start on N = ");
//                                popUpFlag = true;
//                            }
//                            if (isBetween(t.getX(), 60, 90)) {
//                                dialog.setTitle("Power");
//                                dialog.setHeaderText("Power (versão simplona)");
//                                dialog.setContentText("Change base to: ");
//                                popUpFlag = true;
//                            }
//                            if (isBetween(t.getX(), 100, 130)) {
//                                dialog.setTitle("Coefficient");
//                                dialog.setHeaderText("Coefficient (versão simplona)");
//                                dialog.setContentText("Change letter to: ");
//                                popUpFlag = true;
//                            }
//                            if (popUpFlag) {
//                                popUpFlag = false;
//                                Optional<String> result = dialog.showAndWait();
//                                result.ifPresent(response -> System.out.println("Changed to: " + response));
//                            }
//                        }
//                    }
//                });

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

        imageView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* drag was detected, start a drag-and-drop gesture*/
                /* allow any transfer mode */
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
                /* Put a String on a dragboard */
                beingDragged = new MathElement(new Summation());
                ClipboardContent content = new ClipboardContent();
                content.putString("Is valid");
                db.setContent(content);

                event.consume();
            }
        });

        // TODO: apply transparency to the element: idea - use a grey color instead of black
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
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });


        Button b1 = new Button("X");
        //b1.setLayoutX(460);
        //b1.setLayoutY(5);
        innerAnchorPane.getChildren().add(b1);
        innerAnchorPane.setRightAnchor(b1, 50.0);
        innerAnchorPane.setTopAnchor(b1, 5.0);

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
//        To clear canvas:
//        double width = canvas.getWidth();
//        double height = canvas.getHeight();
//        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);


        // now create an actual image of the rendered equation
        BufferedImage image = new BufferedImage(mathElement.getWidth(),
                mathElement.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = image.createGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, mathElement.getWidth(), mathElement.getHeight());
        JLabel jl = new JLabel();
        jl.setForeground(mathElement.getColor());
        mathElement.getIcon().paintIcon(jl, gg, 0, 0);
        // at this point the image is created, you could also save it with ImageIO
        this.g2.drawImage(image, mathElement.getXStart(), mathElement.getYStart(), null);

    }

    /* there a button which action is this function */
    public void generateFormula() {

//        if (formulaPositions.isEmpty())
//            formulaPositions.add(2);
//        else
//            formulaPositions.add(formulaPositions.lastElement()+formulaHeight);
//
//        drawFormula(formulaPositions.lastElement());
//        /* adding buttons next to the formula */
//        Button b1 = new Button(String.valueOf(formulaPositions.size()));
//        b1.setLayoutX(460);
//        b1.setLayoutY(formulaPositions.lastElement()*20 - 15);
//        pane.getChildren().add(b1);
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
        Formula formula = new Formula(0, 0, false);
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