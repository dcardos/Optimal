package model;

import model.math.Expression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class Coefficient implements Expression, Comparable<Coefficient>{
    private char letter;
    private int dimension;
    private ArrayList<double[]> data;
    private Vector<Character> mIndexes;

    public Coefficient(char letter) {
        this.mIndexes = new Vector<>();
        this.letter = letter;
        this.dimension = 1;
        this.data = null;
    }

    public Coefficient(char letter, Vector<Character> indexes) {
        this.letter = letter;
        this.mIndexes = indexes;
        this.dimension = indexes.size();
        this.data = null;
    }

    public Coefficient(char letter, int dimension) {
        this.mIndexes = new Vector<>();
        this.letter = letter;
        this.dimension = dimension;
        this.data = null;
    }

    public Vector<Character> getIndexes() {
        return mIndexes;
    }

    public void setIndexes(Vector<Character> indexes) {
        mIndexes = indexes;
    }

    public void addIndex(Character n) {
        mIndexes.add(n);
    }

    public boolean removeIndex(String n) {
        return mIndexes.remove(n);
    }

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        latex.append(letter);

        if (mIndexes.isEmpty())
            return latex.toString();

        latex.append("_{");

        for (Character e : mIndexes) {
            latex.append(e);
        }

        latex.append("}");

        return latex.toString();
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

    public ArrayList<double[]> getData() {
        return data;
    }

    public void setData(ArrayList<double[]> data) {
        this.data = data;
    }

    @Override
    public int compareTo(@NotNull Coefficient o) {
        return Character.compare(this.getLetter(), o.getLetter());
    }
}
