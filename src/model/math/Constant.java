package model.math;

/**
 * Created by Danilo on 09/09/2016.
 * Leaf node
 */
public class Constant extends Element implements Expression {
    private float mFloat;

    public float getFloat() {
        return mFloat;
    }

    public void setFloat(float aFloat) {
        mFloat = aFloat;
    }

    @Override
    public String getLatexExpression() {    /* printing nicely Floats to Strings */
        if(mFloat == (long) mFloat)
            return String.format("%d",(long)mFloat);
        else
            return String.format("%s", mFloat);
    }
}
