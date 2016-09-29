package model.math;

/**
 * Created by Danilo on 10/09/2016.
 */
public class Power extends BasicExpression implements Expression{

    @Override
    public String getLatexExpression() {
        StringBuilder latex = new StringBuilder();
        latex.append(mLeftExpression.getLatexExpression()).append("^{");
        latex.append(mRightExpression.getLatexExpression()).append("}");

        return latex.toString();
    }
}
