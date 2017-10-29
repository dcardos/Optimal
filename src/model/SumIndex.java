package model;

import org.jetbrains.annotations.NotNull;

public class SumIndex implements Comparable<SumIndex>{
    private char mLetter;
    private char mSet;
    private Double mStartValue;
    private Double mEndValue;

    public SumIndex(char letter, char set, Double startValue, Double endValue) {
        mLetter = letter;
        mSet = set;
        mStartValue = startValue;
        mEndValue = endValue;
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

    public Double getStartValue() {
        return mStartValue;
    }

    public void setStartValue(Double startValue) {
        mStartValue = startValue;
    }

    public Double getEndValue() {
        return mEndValue;
    }

    public void setEndValue(Double endValue) {
        mEndValue = endValue;
    }

    @Override
    public int compareTo(@NotNull SumIndex o) {
        return Character.compare(this.getLetter(), o.getLetter());
    }
}
