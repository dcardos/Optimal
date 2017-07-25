package model;

import controller.FormulasPositionSet;

import java.awt.*;
import java.util.*;

/**
 * Created by Danilo on 29/09/2016.
 */
public class Formula implements Comparable<Formula>{
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
    private boolean mRedFlag;
    private TreeSet<MathElement> mMathElements;
    private MathElement mLastMathElementModified;

    public Formula(int xStart, int yStart, boolean mainFunction) {
        mId = counter++;
        mXStart = xStart == 0 ? 30 : xStart;    // adding margin if necessary
        mYStart = yStart;
        mWidth = 0;
        mHeight = FormulasPositionSet.mDefaultHeight;
        mAlignment = (mHeight/2) + mYStart;
        mMainFunction = mainFunction;
        mMathElements = new TreeSet<>();
        mXEnd = mXStart + mWidth;
        mYEnd = mYStart + mHeight;
        mLastMathElementModified = null;
        mRedFlag = false;
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

    public boolean isRedFlag() {
        return mRedFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.mRedFlag = redFlag;
    }

    public boolean isMainFunction() {
        return mMainFunction;
    }

    public MathElement getLastMathElementModified() {
        return mLastMathElementModified;
    }

    public void setLastMathElementModified(MathElement lastMathElementModified) {
        if ((lastMathElementModified != null)&&(contains(lastMathElementModified)))
            mLastMathElementModified = lastMathElementModified;
        else
            mLastMathElementModified = null;
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
        addMathElement(mathElement, mXStart + mWidth);
    }

    public void addMathElementAtTheBeginning(MathElement mathElement) {
        addMathElement(mathElement, mXStart);
    }

    public void addMathElement(MathElement mathElement, int xPosition) {
        mathElement.setXStart(xPosition);
        mathElement.setYStart(mAlignment - mathElement.getHeight()/2);
        if (xPosition != mXStart + mWidth)     // if it will be added in the middle then update indexes
            updateIndexes(mathElement, false);
        mWidth = mWidth + mathElement.getWidth();
        mMathElements.add(mathElement);
        assert(mMathElements.contains(mathElement));
        mXEnd = mXStart + mWidth;
    }

    public boolean removeMathElement(MathElement mathElement) {
        if (mathElement!=null && mMathElements.remove(mathElement)) {
            mWidth = mWidth - mathElement.getWidth();
            updateIndexes(mathElement, true);
            return true;
        }
        return false;
    }

    public MathElement turnColorBackTo(Color color) {
        if ((mLastMathElementModified != null)&&(contains(mLastMathElementModified))) {
            if (mLastMathElementModified.getColor() != color) {
                mLastMathElementModified.setColor(color);
                mRedFlag = false;
                return mLastMathElementModified;
            }
        }
        mLastMathElementModified = null;
        return null;
    }

    public boolean contains(MathElement mathElement){
        for (MathElement me : mMathElements) {
            if (me.getId() == mathElement.getId())
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
        stringBuilder.append("defaultHeight = " + FormulasPositionSet.mDefaultHeight)
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

    public static boolean isBetween(int pos, int lower, int upper) {
        return (lower <= pos) && (pos <= upper);
    }

    private void updateIndexes(MathElement mathElementInQuestion, boolean removed){
//        System.out.println("Updating indexes");
        // remember that the set will be already modified
        for (MathElement mathElement : mMathElements) {
            if (removed && mathElement.getXStart() > mathElementInQuestion.getXStart()) { // when element is removed
                mathElement.setXStart(mathElement.getXStart() - mathElementInQuestion.getWidth());
            } else if (!removed && mathElement.getXStart() >= mathElementInQuestion.getXStart()) {
                mathElement.setXStart(mathElement.getXStart() + mathElementInQuestion.getWidth());
            }   // for addition remembers that it will be added at the same position of an existing element
        }
    }

    public void correctIndexes() {
        Queue<MathElement> queue = new LinkedList<>();
        for (MathElement mathElement : mMathElements) {
            queue.add(mathElement);
        }
        mMathElements.clear();
        int nextXPosition = mXStart;
        int size = queue.size();
        for (int i=0; i<size; i++) {
            MathElement head = queue.remove();
            head.setXStart(nextXPosition);
            nextXPosition += head.getWidth();
            mMathElements.add(head);
        }
    }
}
