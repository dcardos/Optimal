package model;

import org.jetbrains.annotations.NotNull;

public class SumIndex implements Comparable<SumIndex>{
    private char mLetter;
    private char mSet;
    private boolean personalized;
    private int[] values;

    public SumIndex(char letter, char set, int startValue, int endValue) {
        mLetter = letter;
        mSet = set;
        values = new int[endValue-startValue+1];
        personalized = false;
        int k = 0;
        for (int i = startValue; i <= endValue; i++)
            values[k++] = i;
    }

    public SumIndex(char letter, char set) {
        mLetter = letter;
        mSet = set;
        personalized = true;
        // TODO: read values from file
    }

    public char getLetter() {
        return mLetter;
    }

    public void setLetter(char letter) {
        mLetter = letter;
    }

    public char getSet() {
        return mSet;
    }

    public void setSet(char set) {
        mSet = set;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public boolean isPersonalized() {
        return personalized;
    }

    public void setPersonalized(boolean personalized) {
        this.personalized = personalized;
    }

    public int getSize() {
        return values.length;
    }

    public int getStartValue() {
        if (!personalized)
            return values[0];
        else
            return -1;
    }

    public int getEndValue() {
        if (!personalized)
            return values[getSize()-1];
        else
            return -1;
    }

    public void setStartAndEndValue(int start, int end) {
        values = new int[end-start+1];
        int k = 0;
        for (int i = start; i <= end; i++)
            values[k++] = i;
        personalized = false;
    }

    @Override
    public int compareTo(@NotNull SumIndex o) {
        return Character.compare(this.getLetter(), o.getLetter());
    }
}
