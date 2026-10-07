package payrollcalc;

/** One marginal tax bracket: income in [lowerBound, upperBound) is taxed at `rate`. */
public final class TaxBracket {
    private final double lowerBound;
    private final double upperBound; // Double.POSITIVE_INFINITY for the top bracket
    private final double rate;

    public TaxBracket(double lowerBound, double upperBound, double rate) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.rate = rate;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getRate() {
        return rate;
    }
}
