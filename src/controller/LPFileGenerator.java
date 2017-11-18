package controller;

import javafx.stage.FileChooser;
import model.Coefficient;
import model.MathElement;
import model.SumIndex;
import model.math.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LPFileGenerator {
    private final FileChooser mFileChooser = new FileChooser();

    public void saveLPFile(MainWindowController mwc) {
        mFileChooser.setTitle("Save lp file");
        mFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("LP", "*.lp")
        );
        File file = mFileChooser.showSaveDialog(mwc.mMain.mPrimaryStage);
        if (file != null) {
            try {
                PrintWriter outFile = new PrintWriter(file);
                outFile.println("/* Objective function */");
                if (mwc.rdBtnMax.isSelected())
                    outFile.print("max: ");
                else
                    outFile.print("min: ");
                // parser main function
                boolean pastCoefficient = false;
                boolean parenthesis = false;
                ArrayList<Character> summationIndexes = new ArrayList<>();
                // first check - summation indexes and parenthesis
                for (MathElement mathElement : mwc.formulas.first().getMathElements()) {
                    if (mathElement.getExpression() instanceof Summation) {
                        if (!parenthesis) {
                            summationIndexes.add(((Summation) mathElement.getExpression()).
                                    getStartingPoint().getLatexExpression().charAt(0));
                        } else {
                            System.out.println("Summation cannot be inside parenthesis");
                        }
                    }
                    if (mathElement.getExpression() instanceof OpenParenthesis) {
                        if (!parenthesis) {
                            parenthesis = true;
                        } else {
                            System.out.println("Cannot open another parenthesis");
                        }
                    }
                    if (mathElement.getExpression() instanceof CloseParenthesis && !parenthesis) {
                        System.out.println("Closing parenthesis without opening one!");
                    }
                    if (parenthesis) {
                        // what is allowed after opening a parenthesis
                        if (!(mathElement.getExpression() instanceof Coefficient) &&
                                !(mathElement.getExpression() instanceof Sum) &&
                                !(mathElement.getExpression() instanceof Subtraction) &&
                                !(mathElement.getExpression() instanceof Product) &&
                                !(mathElement.getExpression() instanceof Division) &&
                                !(mathElement.getExpression() instanceof CloseParenthesis) &&
                                !(mathElement.getExpression() instanceof OpenParenthesis)) { // allow itself
                            System.out.println("These math elements are not allowed inside parenthesis");
                        } else if (mathElement.getExpression() instanceof CloseParenthesis) {
                            parenthesis = false;
                        }
                    }
                    if (mathElement.getExpression() instanceof CloseParenthesis &&
                            (mwc.formulas.first().getMathElements().higher(mathElement) == null ||
                            !(mwc.formulas.first().getMathElements().higher(mathElement).getExpression()
                                    instanceof Coefficient))) {
                        System.out.println("After closing parenthesis a variable must be inside");
                    }

                }
                if (parenthesis) {
                    System.out.println("Opening parenthesis without closing it");
                }
                for (MathElement mathElement : mwc.formulas.first().getMathElements()) {
                    if (mathElement.getExpression() instanceof Coefficient) {
                        if (!pastCoefficient) { // ignore the followed coefficient
                            // TODO: refactor coefficient to have a vector of SumIndex
                            ArrayList<SumIndex> sumIndexes = new ArrayList<>();
                            for (Character indexLetter : ((Coefficient) mathElement.getExpression()).getIndexes()) {
                                sumIndexes.add(mwc.getIndexData(indexLetter));
                            }
                            if (sumIndexes.size() != ((Coefficient) mathElement.getExpression()).getDimension()) {
                                System.out.println("Dimension has some index that could not be retrieved");
                            }
                            if (mwc.formulas.first().isMainFunction()) {    // all indexes must be in summation
                                for (Character coefIndex : ((Coefficient) mathElement.getExpression()).getIndexes()) {
                                    if (!summationIndexes.contains(coefIndex)) {
                                        System.out.println("There are variables or coefficients indexes not in summation");
                                    }
                                }
                            }
                            if (mwc.formulas.first().getMathElements().higher(mathElement) == null ||
                                !(mwc.formulas.first().getMathElements().higher(mathElement).getExpression()
                                        instanceof Coefficient)) {
                                System.out.println("Coefficient has to be followed by a variable");
                            }
                            char varLetter = ((Coefficient) mwc.formulas.first().getMathElements().higher(mathElement).getExpression()).getLetter();
                            if (sumIndexes.size() == 1) {
                                int[] sequence = sumIndexes.get(0).getValues();
                                // Finding the coefficient data - TODO: why is not in coefficient from model?
                                char coefLetter = ((Coefficient) mathElement.getExpression()).getLetter();
                                Coefficient coef = mwc.getCoefVarData(coefLetter);
                                assert (coef != null);
                                double[] values = coef.getData().get(0);
                                for (int i = 0; i < sumIndexes.get(0).getSize(); i++) {
                                    if (sequence[i] > values.length - 1) {
                                        System.out.println("Index values does not mach coefficient data");
                                    } else {
                                        outFile.print(values[sequence[i]] + " ");
                                        outFile.print(String.valueOf(varLetter));
                                        outFile.print(sequence[i]);
                                        if (i != sumIndexes.get(0).getSize()-1) {
                                            outFile.print(" + ");
                                        }
                                    }
                                }
                                outFile.println();
                            }
                        }
                        pastCoefficient = !pastCoefficient;
                    }
                }
                outFile.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
