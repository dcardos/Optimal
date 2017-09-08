package model;

import org.jetbrains.annotations.NotNull;

public class Coefficient implements Comparable<Coefficient>{
    private char letter;
    private int dimension;
    private double[] data;

    public Coefficient(char letter, int dimension) {
        this.letter = letter;
        this.dimension = dimension;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
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

    @Override
    public int compareTo(@NotNull Coefficient o) {
        return Character.compare(this.getLetter(), o.getLetter());
    }
}
