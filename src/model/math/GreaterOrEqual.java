package model.math;

/**
 * Created by Danilo on 09/09/2016.
 */
public class GreaterOrEqual extends BasicExpression implements Expression {

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        if (mLeftExpression != null)
            latex.append(mLeftExpression.getLatexExpression());
        latex.append(" \\geqslant ");
        if (mRightExpression != null)
            latex.append(mRightExpression.getLatexExpression());

        return latex.toString();
    }
}
