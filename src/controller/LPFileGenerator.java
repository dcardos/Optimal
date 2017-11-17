package controller;

import javafx.stage.FileChooser;
import model.Coefficient;
import model.MathElement;
import model.SumIndex;

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
                for (MathElement mathElement : mwc.formulas.first().getMathElements()) {
                    if (mathElement.getExpression() instanceof Coefficient) {
                        if (!pastCoefficient) { // ignore the followed coefficient
                            // TODO: refactor coefficient to have a vector of SumIndex
                            ArrayList<SumIndex> sumIndexes = new ArrayList<>();
                            for (Character indexLetter : ((Coefficient) mathElement.getExpression()).getIndexes()) {
                                for (SumIndex sumIndex : mwc.mIndexes) {
                                    if (sumIndex.getLetter() == indexLetter) {
                                        sumIndexes.add(sumIndex);
                                        break;
                                    }
                                }
                            }
                            assert (sumIndexes.size() == ((Coefficient) mathElement.getExpression()).getDimension());
                            assert (mwc.formulas.first().getMathElements().higher(mathElement) != null);
                            assert (mwc.formulas.first().getMathElements().higher(mathElement).getExpression() instanceof Coefficient);
                            char varLetter = ((Coefficient) mwc.formulas.first().getMathElements().higher(mathElement).getExpression()).getLetter();
                            if (sumIndexes.size() == 1) {
                                int[] sequence = sumIndexes.get(0).getValues();
                                // Finding the coefficient data - TODO: why is not on coefficient from model?
                                char coefLetter = ((Coefficient) mathElement.getExpression()).getLetter();
                                Coefficient coef = null;
                                for (Coefficient coefficient : mwc.mCoefficients) {
                                    if (coefficient.getLetter() == coefLetter) {
                                        coef = coefficient;
                                        break;
                                    }
                                }
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
