package model;

import java.util.TreeSet;

/**
 * Created by Danilo on 29/09/2016.
 */
public class Formula implements Comparable<Formula>{
    private static final Integer defaultHeight = 66;
    private static int counter = 0;
    private int mId;
    private int mXStart;
    private int mYStart;
    private int mXEnd;
    private int mYEnd;
    private int mWidth;
    private int mHeight;
    private int mAlignment;
    private boolean mMainFunction;
    private TreeSet<MathElement> mMathElements;

    public Formula(int xStart, int yStart, boolean mainFunction) {
        mId = counter++;
        mXStart = xStart;
        mYStart = yStart;
        mWidth = 0;
        mHeight = defaultHeight;
        mAlignment = (mHeight/2) + mYStart;
        mMainFunction = mainFunction;
        mMathElements = new TreeSet<>();
        mXEnd = mXStart + mWidth;
        mYEnd = mYStart + mHeight;
    }

    public int getId() {
        return mId;
    }

    public int getXStart() {
        return mXStart;
    }

    public int getYStart() {
        return mYStart;
    }

    public int getXEnd() {
        return mXEnd;
    }

    public int getYEnd() {
        return mYEnd;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getAlignment() {
        return mAlignment;
    }

    public boolean isMainFunction() {
        return mMainFunction;
    }

    public TreeSet<MathElement> getMathElements() {
        return mMathElements;
    }

    public MathElement getMathElement(int xPosition) {
        for (MathElement mathElement : mMathElements) {
            if (isBetween(xPosition, mathElement.getXStart(), mathElement.getXEnd()))
                return mathElement;
        }
        return null;
    }

    public void setXStart(int xStart) {
        mXStart = xStart;
        mXEnd = mXStart + mWidth -1;
    }

    public void setYStart(int yStart) {
        mYStart = yStart;
        mYEnd = mYStart + mHeight -1;
    }

    public void setMainFunction(boolean mainFunction) {
        mMainFunction = mainFunction;
    }

    public void addMathElementAtTheEnd(MathElement mathElement) {
        addMathElement(mathElement, mXStart +mWidth);
    }

    public void addMathElement(MathElement mathElement, int xPosition) {
        mathElement.setXStart(xPosition);
        mathElement.setYStart(mAlignment - mathElement.getHeight()/2);
        if (xPosition != mXStart +mWidth)     // if it will be added in the middle then update indexes
            updateIndexes(mathElement, false);
        mWidth = mWidth + mathElement.getWidth();
        mMathElements.add(mathElement);
        assert(mMathElements.contains(mathElement));
    }

    public boolean removeMathElement(MathElement mathElement) {
        if (mMathElements.remove(mathElement)) {
            mWidth = mWidth - mathElement.getWidth();
            updateIndexes(mathElement, true);
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Formula o) {
        return mYStart - o.getYStart();   // sort by y position
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Formula:")
                .append(System.getProperty("line.separator"));
        stringBuilder.append("defaultHeight = " + defaultHeight)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("counter = " + counter)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mId = " + mId)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mXStart = " + mXStart)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("my = " + mYStart)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mWidth = " + mWidth)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mHeight = " + mHeight)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mAlignment = " + mAlignment)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mMainFunction = " + mMainFunction)
                .append(System.getProperty("line.separator"));
        for (MathElement mathElement : mMathElements) {
            stringBuilder.append(mathElement);
        }

        return stringBuilder.toString();
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return (lower <= x) && (x <= upper);
    }

    private void updateIndexes(MathElement mathElementInQuestion, boolean removed){
        System.out.println("Updating indexes");
        // remember that the set will be already modified
        for (MathElement mathElement : mMathElements) {
            if (removed && mathElement.getXStart() > mathElementInQuestion.getXStart()) { // when element is removed
                mathElement.setXStart(mathElement.getXStart() - mathElementInQuestion.getWidth());
            } else if (!removed && mathElement.getXStart() >= mathElementInQuestion.getXStart()) {
                mathElement.setXStart(mathElement.getXStart() + mathElementInQuestion.getWidth());
            }   // for addition remembers that it will be added at the same position of an existing element
        }

    }
}
