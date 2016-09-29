package model;

import model.math.Expression;

/**
 * Created by Danilo on 21/09/2016.
 */
public class MathElement {
    private static int counter = -1;
    private int mId;
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private Expression mExpression;

    public MathElement(int x, int y, int width, int height, Expression expression) {
        mId = counter++;
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mExpression = expression;
    }

    public int getId() {
        return mId;
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public Expression getExpression() {
        return mExpression;
    }

    public void setExpression(Expression expression) {
        mExpression = expression;
    }
}
