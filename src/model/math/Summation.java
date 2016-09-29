package model.math;

/**
 * Created by Danilo on 09/09/2016.
 */
public class Summation implements Expression {
    private BasicExpression mStartingPoint;
    private Element mStoppingPoint;
    private Expression mExpression;

    public BasicExpression getStartingPoint() {
        return mStartingPoint;
    }

    public void setStartingPoint(BasicExpression startingPoint) {
        mStartingPoint = startingPoint;
    }

    public Element getStoppingPoint() {
        return mStoppingPoint;
    }

    public void setStoppingPoint(Element stoppingPoint) {
        mStoppingPoint = stoppingPoint;
    }

    public Expression getExpression() {
        return mExpression;
    }

    public void setExpression(Expression expression) {
        mExpression = expression;
    }

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        latex.append("\\sum");
        if (null != mStartingPoint) {
            latex.append("_{").append(mStartingPoint.getLatexExpression()).append("}");
        }
        if (null != mStoppingPoint) {
            latex.append("^{").append(mStoppingPoint.getLatexExpression()).append("}");
        }
        latex.append(mExpression.getLatexExpression());
        return latex.toString();
    }
}
