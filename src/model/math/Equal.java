package model.math;

/**
 * Created by Danilo on 09/09/2016.
 */
public class Equal extends BasicExpression implements Expression {

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();

        if (null != mLeftExpression)
            latex.append(mLeftExpression.getLatexExpression());
        latex.append(" = ");
        if (null != mRightExpression)
            latex.append(mRightExpression.getLatexExpression());

        return latex.toString();
    }
}
