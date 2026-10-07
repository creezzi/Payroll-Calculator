package payrollcalc;

import java.util.List;

/**
 * A simplified, illustrative snapshot of 2024 single-filer federal tax brackets and the standard
 * deduction. <b>Not tax advice</b> - real payroll withholding also depends on the employee's W-4
 * elections (filing status, multiple jobs, dependents, extra withholding), and this only models
 * the base progressive-bracket calculation. Verify against current IRS Publication 15-T before
 * using this for anything beyond demonstrating the algorithm.
 */
public final class FederalTaxTables {

    public static final double STANDARD_DEDUCTION_SINGLE = 14600.0;

    public static final List<TaxBracket> SINGLE_BRACKETS_2024 = List.of(
            new TaxBracket(0, 11_600, 0.10),
            new TaxBracket(11_600, 47_150, 0.12),
            new TaxBracket(47_150, 100_525, 0.22),
            new TaxBracket(100_525, 191_950, 0.24),
            new TaxBracket(191_950, 243_725, 0.32),
            new TaxBracket(243_725, 609_350, 0.35),
            new TaxBracket(609_350, Double.POSITIVE_INFINITY, 0.37)
    );

    private FederalTaxTables() {
    }
}
