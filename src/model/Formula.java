package model;

import java.util.TreeSet;

/**
 * Created by Danilo on 29/09/2016.
 */
public class Formula {
    private static final Integer defaultHeight = 66;
    private static int counter = 0;
    private int mId;
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private int mAlignment;
    private boolean mMainFunction;
    private TreeSet<MathElement> mMathElements;

    public Formula(int x, int y, boolean mainFunction) {
        mId = counter++;
        mX = x;
        mY = y;
        mWidth = 0;
        mHeight = defaultHeight;
        mAlignment = (mHeight/2) + mY;
        mMainFunction = mainFunction;
        mMathElements = new TreeSet<>();
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

    public TreeSet<MathElement> getMathElements() {
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

    public void addMathElementAtTheEnd(MathElement mathElement) {
        addMathElement(mathElement, mX+mWidth);
    }

    public void addMathElement(MathElement mathElement, int xPosition) {
        mathElement.setX(xPosition);
        mathElement.setY(mAlignment - mathElement.getHeight()/2);
        if (xPosition != mX+mWidth)     // if it will be added in the middle then update indexes
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
        stringBuilder.append("mX = " + mX)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("my = " + mY)
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
    
    private void updateIndexes(MathElement mathElementInQuestion, boolean removed){
        System.out.println("Updating indexes");
        // remember that the set will be already modified
        for (MathElement mathElement : mMathElements) {
            if (removed && mathElement.getX() > mathElementInQuestion.getX()) { // when element is removed
                mathElement.setX(mathElement.getX() - mathElementInQuestion.getWidth());
            } else if (!removed && mathElement.getX() >= mathElementInQuestion.getX()) {
                mathElement.setX(mathElement.getX() + mathElementInQuestion.getWidth());
            }   // for addition remembers that it will be added at the same position of an existing element
        }

    }
}
