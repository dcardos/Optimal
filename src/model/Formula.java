package model;

import java.util.Vector;

/**
 * Created by Danilo on 29/09/2016.
 */
public class Formula {
    private static final Integer defaultHeight = 66;
    private static int counter = -1;
    private int mId;
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private int mAlignment;
    private boolean mMainFunction;
    private Vector<MathElement> mMathElements;

    public Formula(int x, int y, boolean mainFunction) {
        mId = counter++;
        mX = x;
        mY = y;
        mWidth = 0;
        mHeight = defaultHeight;
        mAlignment = (mHeight/2) + mY;
        mMainFunction = mainFunction;
        mMathElements = new Vector<>();
    }

    public int getId() {
        return mId;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
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

    public Vector<MathElement> getMathElements() {
        return mMathElements;
    }

    public void setX(int x) {
        mX = x;
    }

    public void setY(int y) {
        mY = y;
    }

    public void setMainFunction(boolean mainFunction) {
        mMainFunction = mainFunction;
    }

    public void addMathElement(MathElement mathElement) {
        mMathElements.add(mathElement);
        updateSize(mathElement.getWidth());
    }

    public MathElement removeMathElement(int index) {
        MathElement mathElementRemoved = mMathElements.remove(index);
        updateSize(-mathElementRemoved.getWidth());
        return mathElementRemoved;
    }

    private void updateSize(int width) {
        mWidth = mWidth + width;
    }
}
