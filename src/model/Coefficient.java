package model;

import org.jetbrains.annotations.NotNull;

public class Coefficient implements Comparable<Coefficient>{
    private char letter;
    private int dimension;
    private double[] data;
    private MathElement mMathElement;

    public Coefficient(char letter, int dimension) {
        this.letter = letter;
        this.dimension = dimension;
        this.mMathElement = new MathElement(new model.math.Coefficient(letter));
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
        mMathElement = new MathElement(new model.math.Coefficient(letter));
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public MathElement getMathElement() {
        return mMathElement;
    }

    @Override
    public int compareTo(@NotNull Coefficient o) {
        return Character.compare(this.getLetter(), o.getLetter());
    }
}
