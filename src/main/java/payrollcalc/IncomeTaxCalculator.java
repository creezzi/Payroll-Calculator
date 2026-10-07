package payrollcalc;

import java.util.List;

/**
 * Computes progressive (marginal-bracket) federal income tax, then converts an annual figure into
 * a per-paycheck withholding amount - the same "annualize, tax, divide back down" approach the
 * IRS's own percentage method uses, simplified to a single filing status and no W-4 adjustments.
 */
public final class IncomeTaxCalculator {

    /** Progressive tax on `taxableIncome`: each bracket's rate applies only to the income within it. */
    public double annualTax(double taxableIncome, List<TaxBracket> brackets) {
        if (taxableIncome <= 0) {
            return 0.0;
        }
        double tax = 0.0;
        for (TaxBracket bracket : brackets) {
            if (taxableIncome <= bracket.getLowerBound()) {
                break;
            }
            double taxableInThisBracket = Math.min(taxableIncome, bracket.getUpperBound()) - bracket.getLowerBound();
            tax += taxableInThisBracket * bracket.getRate();
        }
        return tax;
    }

    public double perPaycheckFederalWithholding(double annualGrossPay, int payPeriodsPerYear,
                                                 double standardDeduction, List<TaxBracket> brackets) {
        double taxableIncome = Math.max(0, annualGrossPay - standardDeduction);
        double annual = annualTax(taxableIncome, brackets);
        return annual / payPeriodsPerYear;
    }
}
