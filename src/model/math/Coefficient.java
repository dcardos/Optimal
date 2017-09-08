package model.math;

import java.util.Vector;

/**
 * Created by Danilo on 09/09/2016.
 */
public class Coefficient extends Element implements Expression {
    private char mLetter;
    private Vector<String> mIndexes;

    public Coefficient(char letter) {
        this.mIndexes = new Vector<>();
        this.mLetter = letter;
    }

    public char getLetter() {
        return mLetter;
    }

    public void setLetter(char letter) {
        mLetter = letter;
    }

    public Vector<String> getIndexes() {
        return mIndexes;
    }

    public void setIndexes(Vector<String> indexes) {
        mIndexes = indexes;
    }

    public void addIndex(String n) {
        mIndexes.add(n);
    }

    public boolean removeIndex(String n) {
        return mIndexes.remove(n);
    }

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        latex.append(mLetter);

        if (mIndexes.isEmpty())
            return latex.toString();

        latex.append("_{");

        for (String e : mIndexes) {
            latex.append(e);
        }

        latex.append("}");

        return latex.toString();
    }
}
