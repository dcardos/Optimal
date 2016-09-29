package model.math;

/**
 * Created by Danilo on 09/09/2016.
 */
public class BasicExpression implements Expression{
    protected Expression mLeftExpression;
    protected Expression mRightExpression;

    public Expression getLeftExpression() {
        return mLeftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {
        mLeftExpression = leftExpression;
    }

    public Expression getRightExpression() {
        return mRightExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        mRightExpression = rightExpression;
    }

    @Override
    public String getLatexExpression() {
        return null;
    }
}
