package model;

public class Variable extends Coefficient{
    private char domain;
    private boolean nonNegative;
    private Double upperBound;
    private Double lowerBound;

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

    public Double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }
}
