package controller;

public class FormulasPositionSet {
    static final int mMainFormulaStartXPosition = 120;
    static final int mMainFormulaStartYPosition = 30;
    static final int mConstraintStartXPosition = 120;
    static final int mFirstConstraintStartYPosition = 160;
    static final int mVerticalSpaceBetweenConstraints = 30;
    public static final int mDefaultHeight = 80;

    public static int getMainFormulaVerticalAlignment() {
        return mDefaultHeight/2 + mMainFormulaStartYPosition;
    }

    public static int getMainFormulaYEndPosition() {
        return mMainFormulaStartYPosition + mDefaultHeight;
    }
}
