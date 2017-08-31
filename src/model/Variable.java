package model;

public class Variable extends Coefficient{
    private char domain;
    private boolean nonNegative;
    private double upperBound;
    private double lowerBound;

    public Variable(char letter, int dimension, char domain, boolean nonNegative) {
        super(letter, dimension);
        this.domain = domain;
        this.nonNegative = nonNegative;
    }

    public char getDomain() {
        return domain;
    }

    public void setDomain(char domain) {
        this.domain = domain;
    }

    public boolean isNonNegative() {
        return nonNegative;
    }

    public void setNonNegative(boolean nonNegative) {
        this.nonNegative = nonNegative;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }
}
