package payrollcalc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncomeTaxCalculatorTest {

    private final IncomeTaxCalculator calculator = new IncomeTaxCalculator();
    private final List<TaxBracket> brackets = List.of(
            new TaxBracket(0, 10_000, 0.10),
            new TaxBracket(10_000, 40_000, 0.20),
            new TaxBracket(40_000, Double.POSITIVE_INFINITY, 0.30)
    );

    @Test
    void incomeEntirelyInTheFirstBracketIsTaxedAtItsRateOnly() {
        assertEquals(500, calculator.annualTax(5_000, brackets), 1e-9);
    }

    @Test
    void incomeSpanningTwoBracketsIsTaxedProgressivelyNotAtOneFlatRate() {
        // $15,000: first $10,000 at 10% ($1,000) + next $5,000 at 20% ($1,000) = $2,000.
        // A flat-rate (non-progressive) calculation would wrongly give $15,000 * 20% = $3,000.
        assertEquals(2_000, calculator.annualTax(15_000, brackets), 1e-9);
    }

    @Test
    void incomeSpanningAllThreeBracketsSumsEachPortionCorrectly() {
        // $50,000: $1,000 (10% of 10k) + $6,000 (20% of 30k) + $3,000 (30% of 10k) = $10,000.
        assertEquals(10_000, calculator.annualTax(50_000, brackets), 1e-9);
    }

    @Test
    void zeroOrNegativeTaxableIncomeOwesNoTax() {
        assertEquals(0.0, calculator.annualTax(0, brackets), 1e-9);
        assertEquals(0.0, calculator.annualTax(-500, brackets), 1e-9);
    }

    @Test
    void perPaycheckWithholdingAppliesTheStandardDeductionFirst() {
        // $50,000 salary, $10,000 deduction -> $40,000 taxable -> $1,000 + $6,000 = $7,000/year,
        // divided across 26 biweekly paychecks.
        double perCheck = calculator.perPaycheckFederalWithholding(50_000, 26, 10_000, brackets);
        assertEquals(7_000.0 / 26, perCheck, 1e-6);
    }

    @Test
    void incomeBelowTheStandardDeductionOwesNothing() {
        double perCheck = calculator.perPaycheckFederalWithholding(8_000, 26, 10_000, brackets);
        assertEquals(0.0, perCheck, 1e-9);
    }
}
