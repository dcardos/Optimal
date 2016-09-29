package model.math;

/**
 * Created by Danilo on 09/09/2016.
 */
public class Equal extends BasicExpression implements Expression {

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        latex.append(mLeftExpression.getLatexExpression()).append(" = ");
        latex.append(mRightExpression.getLatexExpression());

        return latex.toString();
    }
}
