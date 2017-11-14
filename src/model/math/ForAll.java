package model.math;

import java.util.Vector;

/**
 * Created by Danilo on 09/09/2016.
 * "Pertence"
 */
public class ForAll extends BasicExpression implements Expression {
    private Vector<Character> mIndexes = new Vector<>();
    private char mSet;

    public ForAll(char index, char set) {
        mIndexes.add(index);
        mSet = set;
    }

    public ForAll(Vector<Character> indexes, char set) {
        mIndexes = indexes;
        mSet = set;
    }

    public Vector<Character> getIndexes() {
        return mIndexes;
    }

    public void setIndexes(Vector<Character> indexes) {
        mIndexes = indexes;
    }

    public char getSet() {
        return mSet;
    }

    public void setSet(char set) {
        mSet = set;
    }

    public void addIndex(char index) {
        mIndexes.add(index);
    }

    @Override
    public String getLatexExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\forall ");
        String prefix = "";
        for (Character index : mIndexes) {
            sb.append(prefix);
            prefix = ",";
            sb.append(index + " ");
        }
        sb.append("\\in " + mSet);
        return sb.toString();
    }
}
