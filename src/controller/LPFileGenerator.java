package controller;

import javafx.stage.FileChooser;
import model.*;
import model.math.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

public class LPFileGenerator {
    private final FileChooser mFileChooser = new FileChooser();
    private MainWindowController mwc;

    public LPFileGenerator(MainWindowController mwc) {
        this.mwc = mwc;
    }

    public void saveLPFile() {
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
                int r = 1;
                for (Formula formula : mwc.formulas) {
                    if (!formula.isMainFunction()) {
                        outFile.print("R" + r++ + ": ");
                    }
                    formulaParser(formula, outFile);
                    outFile.print(";");
                    if (formula.isMainFunction()) {
                        outFile.println();
                        outFile.println();
                        outFile.print("/* subject to */");
                    }
                    outFile.println();
                }
                // All variables will be used for domain constraints (they might not have been used)
                // for lower bound
                outFile.println();
                int k = 1;
                for (Variable variable : mwc.mVariables) {
                    if (variable.getLowerBound() != null) {
                        SumIndex indexData = mwc.getIndexData(variable.getIndexes().firstElement());
                        for (int i = 0; i < indexData.getSize(); i++) {
                            outFile.print("D" + k++ + ": ");
                            outFile.println(String.valueOf(variable.getLetter()) + indexData.getValues()[i] +
                                    " >= " + variable.getLowerBound() + ";");
                        }
                    }
                    if (variable.getLowerBound() == null && variable.isNonNegative()) {
                        outFile.println();
                        SumIndex indexData = mwc.getIndexData(variable.getIndexes().firstElement());
                        for (int i = 0; i < indexData.getSize(); i++) {
                            outFile.print("D" + k++ + ": ");
                            outFile.println(String.valueOf(variable.getLetter()) + indexData.getValues()[i] + " >= 0;");
                        }
                    }
                    if (variable.getUpperBound() != null) {
                        SumIndex indexData = mwc.getIndexData(variable.getIndexes().firstElement());
                        for (int i = 0; i < indexData.getSize(); i++) {
                            outFile.print("D" + k++ + ": ");
                            outFile.println(String.valueOf(variable.getLetter()) + indexData.getValues()[i] +
                                    " <= " + variable.getUpperBound());
                        }
                    }
                }

                outFile.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private ArrayList<SumIndex> getIndexesCoefData(Vector<Character> indexLeters) {
        // TODO: refactor coefficient to have a vector of SumIndex
        ArrayList<SumIndex> sumIndexes = new ArrayList<>();
        for (Character indexLetter : indexLeters) {
            sumIndexes.add(mwc.getIndexData(indexLetter));
        }
        if (sumIndexes.size() != indexLeters.size()) {
            System.out.println("Dimension has some index that could not be retrieved");
            return null;
        }
        return sumIndexes;
    }

    private double[] coefDataReady(ArrayList<SumIndex> indexesData, MathElement mathElement) {
        if (indexesData.size() == 1) {
            int[] sequence = indexesData.get(0).getValues();
            // Finding the coefficient data - TODO: why is not in coefficient from model?
            char coefLetter = ((Coefficient) mathElement.getExpression()).getLetter();
            Coefficient coef = mwc.getCoefVarData(coefLetter);
            if (coef == null) {
                System.out.println("ERR, could not find coefficient, probably a variable!");
                return null;
            }
            if (coef.getData() == null) {
                System.out.println("Illegal element, no data in coefficient");
                return null;
            }
            double[] values = coef.getData().get(0);
            double[] coefValueSet = new double[indexesData.get(0).getSize()];
            for (int i = 0; i < indexesData.get(0).getSize(); i++) {
                if (sequence[i] > values.length - 1) {
                    System.out.println("Index values does not mach coefficient data");
                } else {
                    coefValueSet[i] = values[sequence[i]];
                }
            }
            return coefValueSet;
        }
        return null;
    }

    private void formulaParser(Formula formula, PrintWriter out) {
        // parser main function
        boolean pastCoefficient = false;
        boolean parenthesis = false;
        boolean afterComparison = false;
        ArrayList<Character> summationIndexes = new ArrayList<>();

        // first check - summation indexes, parenthesis and end of restriction
        for (MathElement mathElement : formula.getMathElements()) {
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
                        !(mathElement.getExpression() instanceof Constant) &&
                        !(mathElement.getExpression() instanceof CloseParenthesis) &&
                        !(mathElement.getExpression() instanceof OpenParenthesis)) { // allow itself
                    System.out.println("These math elements are not allowed inside parenthesis");
                } else if (mathElement.getExpression() instanceof CloseParenthesis) {
                    parenthesis = false;
                }
            }
            if (mathElement.getExpression() instanceof CloseParenthesis &&
                    (formula.getMathElements().higher(mathElement) == null ||
                            !(formula.getMathElements().higher(mathElement).getExpression()
                                    instanceof Coefficient))) {
                System.out.println("After closing parenthesis a variable must be inside");
            }
            if (mathElement.getExpression() instanceof LessOrEqual ||
                    mathElement.getExpression() instanceof GreaterOrEqual) {
                if (formula.getMathElements().higher(mathElement) == null ||
                        !(formula.getMathElements().higher(mathElement).getExpression() instanceof Coefficient)) {
                    System.out.println("After comparison a coefficient has to be in it");
                }
            }
        }

        if (parenthesis) {
            System.out.println("Opening parenthesis without closing it");
        }
        // Now getting coefficient values
        ArrayList<double[]> coefValues = new ArrayList<>();
        ArrayList<String> operation = new ArrayList<>();
        char varLetter;
        for (MathElement mathElement : formula.getMathElements()) {
            if (mathElement.getExpression() instanceof OpenParenthesis) {
                if (formula.getMathElements().higher(mathElement).getExpression()
                        instanceof CloseParenthesis) {
                    System.out.println("Nothing inside parenthesis");
                }
                parenthesis = true;
                MathElement futureME = formula.getMathElements().higher(mathElement);
                do {    // process ahead all coefficients and find its arrays of values
                    if (futureME.getExpression() instanceof Coefficient) {
                        ArrayList<SumIndex> sumIndexes = getIndexesCoefData(((Coefficient)
                                futureME.getExpression()).getIndexes());
                        // Sum Indexes has to be the same for all coefficients and variable after!
                        coefValues.add(coefDataReady(sumIndexes, futureME));
                    } else if (futureME.getExpression() instanceof Sum) {
                        operation.add("+");
                    } else if (futureME.getExpression() instanceof Subtraction) {
                        operation.add("-");
                    }
                    futureME = formula.getMathElements().higher(futureME);
                } while (!(futureME.getExpression() instanceof CloseParenthesis));
            } else if (mathElement.getExpression() instanceof CloseParenthesis) {
                parenthesis = false;
                pastCoefficient = true;
            } else if (mathElement.getExpression() instanceof Coefficient) {
                if (!parenthesis) {  // all data inside parentheses has been collected
                    if (!pastCoefficient) { // it is not a variable, it is coefficient outside parenthesis
                        ArrayList<SumIndex> sumIndexes = getIndexesCoefData(((Coefficient)
                                mathElement.getExpression()).getIndexes());
                        // ONLY if is main function
                        if (formula.isMainFunction()) {    // all indexes must be in summation
                            for (Character coefIndex : ((Coefficient) mathElement.getExpression()).getIndexes()) {
                                if (!summationIndexes.contains(coefIndex)) {
                                    System.out.println("There are variables indexes not in summation");
                                }
                            }
                        }
                        // Only if there is no parenthesis (parenthesis is false) of after close parenthesis
                        if (formula.getMathElements().higher(mathElement) == null ||
                                !(formula.getMathElements().higher(mathElement).getExpression()
                                        instanceof Coefficient)) {
                            System.out.println("Coefficient has to be followed by a variable");
                        }
                        // setting values
                        coefValues.add(coefDataReady(sumIndexes, mathElement));
                        pastCoefficient = true;
                        // TODO: implement the b_k form at the end
//                        if (afterComparison) {
//                            if (coefValues.size() != 1) {
//                                System.out.println("Just one coefficient data should be available!");
//                            } else {
//                                out.print(coefValues.get(0)[formulaNumber]);
//                                break; // nothing more to print in this line
//                            }
//                        }
                    } else { // it is a variable
                        ArrayList<SumIndex> sumIndexes = getIndexesCoefData(((Coefficient)
                                mathElement.getExpression()).getIndexes());
                        varLetter = ((Coefficient) mathElement.getExpression()).getLetter();
                        if (operation.size() == 0) {    // no calculation to be made, no parenthesis before
                            for (int i = 0; i < coefValues.get(0).length; i++) {
                                if (i > 0) {
                                    out.print(Math.abs(coefValues.get(0)[i]) + " ");
                                } else {
                                    out.print(coefValues.get(0)[i] + " ");
                                }
                                out.print(String.valueOf(varLetter));
                                out.print(sumIndexes.get(0).getValues()[i]);
                                if (i != coefValues.get(0).length - 1) {
                                    if (coefValues.get(0)[i+1] < 0) {
                                        out.print(" - ");
                                    } else {
                                        out.print(" + ");
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < coefValues.get(0).length; i++) {
                                double value = coefValues.get(0)[i];
                                int k = 1;
                                for (int j = 0; j < operation.size(); j++) {
                                    if (operation.get(j).equals("+")) {
                                        value = value + coefValues.get(k++)[i];
                                    } else {
                                        value = value - coefValues.get(k++)[i];
                                    }
                                }
                                if (i > 0) {
                                    out.print(Math.abs(value) + " ");
                                } else {
                                    out.print(value + " ");
                                }
                                out.print(String.valueOf(varLetter));
                                out.print(sumIndexes.get(0).getValues()[i]);
                                if (i != coefValues.get(0).length - 1) {
                                    if (coefValues.get(0)[i+1] < 0) {
                                        out.print(" - ");
                                    } else {
                                        out.print(" + ");
                                    }
                                }
                            }
                        }
                        coefValues.clear();
                        operation.clear();
                        pastCoefficient = false;
                    }
                }
            } else if (mathElement.getExpression() instanceof LessOrEqual) {
                out.print(" <= ");
                afterComparison = true;
            } else if (mathElement.getExpression() instanceof GreaterOrEqual) {
                out.print(" >= ");
                afterComparison = true;
            } else if (mathElement.getExpression() instanceof Equal) {
                out.print(" = ");
                afterComparison = true;
            } else if (mathElement.getExpression() instanceof Constant) {
                if (afterComparison) {
                    out.print(((Constant) mathElement.getExpression()).getFloat());
                    break; // nothing more to print in this line
                }
            } else if (!parenthesis && mathElement.getExpression() instanceof Sum) {
                out.print(" + ");
            }
        }
    }
}
