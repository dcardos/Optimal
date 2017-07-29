package controller;

public class FormulasPositionSet {
    static final int mMainFormulaStartXPosition = 120;
    static final int mMainFormulaStartYPosition = 30;
    static final int mConstraintStartXPosition = 120;
    static final int mFirstConstraintStartYPosition = 170;
    private static final int mVerticalSpaceBetweenConstraints = 30;
    public static final int mDefaultHeight = 100;

    static int getMainFormulaVerticalAlignment() {
        return mDefaultHeight/2 + mMainFormulaStartYPosition;
    }

    static int getFirstConstraintVerticalAlignment() {
        return mDefaultHeight/2 + mFirstConstraintStartYPosition;
    }

    static int getMainFormulaYEndPosition() {
        return mMainFormulaStartYPosition + mDefaultHeight;
    }

    public static int getTotalConstraintVerticalSpace() {
        return mDefaultHeight + mVerticalSpaceBetweenConstraints;
    }

}
