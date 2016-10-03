package model;

import model.math.Expression;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * Created by Danilo on 21/09/2016.
 */
public class MathElement implements Comparable<MathElement>{
    private static int counter = 0;
    private int mId;
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private Expression mExpression;
    private TeXFormula mTeXFormula;
    private TeXIcon mIcon;  // to draw element

    public MathElement(Expression expression) {
        mId = counter++;
        mX = 0;
        mY = 0;
        mExpression = expression;
        mTeXFormula = new TeXFormula(mExpression.getLatexExpression());
        mIcon = mTeXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
        mWidth = mIcon.getIconWidth();
        mHeight = mIcon.getIconHeight();
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

    public TeXIcon getIcon() {
        return mIcon;
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("MathElement:")
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
        stringBuilder.append("mExpression = " + mExpression.getLatexExpression())
                .append(System.getProperty("line.separator"));

        return stringBuilder.toString();
    }

    @Override
    public int compareTo(MathElement o) {
        return mX - o.getX();   // sort by x position
    }
}
