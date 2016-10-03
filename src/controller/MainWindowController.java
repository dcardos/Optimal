package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Formula;
import model.MathElement;
import model.math.*;
import org.jfree.fx.FXGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.Vector;

public class MainWindowController {

    private Main mMain;
    private FXGraphics2D g2;
    private Vector<Formula> formulas;

    @FXML
    private Label labelEditVariable;

    @FXML private Canvas canvas;

    //@FXML private Pane pane;

    @FXML private AnchorPane innerAnchorPane;

    public static boolean isBetween(double x, double lower, double upper) {
        return (lower <= x) && (x <= upper);
    }

    public void setMain(Main main) {
        mMain = main;
        labelEditVariable.setText("Editing Sum: \u2211");
        innerAnchorPane.setStyle("-fx-background-color: #ffe1c5");

        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmex10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
        javafx.scene.text.Font.loadFont(Main.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);

        //canvas = new FXGraphics2DDemo3.MyCanvas();

        this.formulas = new Vector<>();
        initializeCanvas();

        TextInputDialog dialog = new TextInputDialog("0");

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        System.out.println("X: " + t.getX() + "Y: " + t.getY());
                        if (t.getClickCount() > 1) { // double click or more
                            boolean popUpFlag = false;
                            if (isBetween(t.getX(), 20, 50)) {
                                dialog.setTitle("Summation");
                                dialog.setHeaderText("Summation (versão simplona)");
                                dialog.setContentText("Start on N = ");
                                popUpFlag = true;
                            }
                            if (isBetween(t.getX(), 60, 90)) {
                                dialog.setTitle("Power");
                                dialog.setHeaderText("Power (versão simplona)");
                                dialog.setContentText("Change base to: ");
                                popUpFlag = true;
                            }
                            if (isBetween(t.getX(), 100, 130)) {
                                dialog.setTitle("Coefficient");
                                dialog.setHeaderText("Coefficient (versão simplona)");
                                dialog.setContentText("Change letter to: ");
                                popUpFlag = true;
                            }
                            if (popUpFlag) {
                                popUpFlag = false;
                                Optional<String> result = dialog.showAndWait();
                                result.ifPresent(response -> System.out.println("Changed to: " + response));
                            }
                        }
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                //gc.clearRect(t.getX() - 2, t.getY() - 2, 5, 5);
//                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                gc.setFill(Color.GREEN);
//                gc.setStroke(Color.BLUE);
                gc.strokeLine(20, 80, 50, 80);
                if (isBetween(t.getX(), 20, 50)) {

                    //System.out.println("Mouse moved");
                }
            }
        });

        Button b1 = new Button("X");
        //b1.setLayoutX(460);
        //b1.setLayoutY(5);
        innerAnchorPane.getChildren().add(b1);
        innerAnchorPane.setRightAnchor(b1, 50.0);
        innerAnchorPane.setTopAnchor(b1, 5.0);

//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        drawShapes(gc);
    }

    public void closeWindow() {
        mMain.getPrimaryStage().close();
    }

    private void initializeCanvas() {
        this.g2 = new FXGraphics2D(canvas.getGraphicsContext2D());

        drawFormula(testFormula());

        // Redraw canvas when size changes.
//        canvas.widthProperty().addListener(evt -> drawFormula());
//        canvas.heightProperty().addListener(evt -> drawFormula());
    }

    private void drawFormula(Formula formula) {
//        To clear canvas:
//        double width = canvas.getWidth();
//        double height = canvas.getHeight();
//        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

        for (MathElement mathElement : formula.getMathElements()) {
            // now create an actual image of the rendered equation
            BufferedImage image = new BufferedImage(mathElement.getWidth(),
                    mathElement.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gg = image.createGraphics();
            gg.setColor(Color.WHITE);
            gg.fillRect(0, 0, mathElement.getWidth(), mathElement.getHeight());
            JLabel jl = new JLabel();
            jl.setForeground(new Color(0, 0, 0));
            mathElement.getIcon().paintIcon(jl, gg, 0, 0);
            // at this point the image is created, you could also save it with ImageIO
            this.g2.drawImage(image, mathElement.getX(), mathElement.getY(), null);
        }
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

        System.out.println(formula);

        return formula;
    }


}