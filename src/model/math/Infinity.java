package model.math;

/**
 * Created by Danilo on 10/09/2016.
 */
public class Infinity extends Element implements Expression{
    @Override
    public String getLatexExpression() {
        return "\\infty";
    }
}
