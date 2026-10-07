package payrollcalc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FicaCalculatorTest {

    private final FicaCalculator fica = new FicaCalculator();

    @Test
    void socialSecurityIsFullyTaxedWellBelowTheCap() {
        double withheld = fica.socialSecurityWithholding(0, 5_000);
        assertEquals(5_000 * FicaCalculator.SOCIAL_SECURITY_RATE, withheld, 1e-9);
    }

    @Test
    void socialSecurityStopsEntirelyOnceAlreadyOverTheCap() {
        double withheld = fica.socialSecurityWithholding(200_000, 5_000); // already well past 168,600
        assertEquals(0.0, withheld, 1e-9);
    }

    @Test
    void socialSecurityIsPartiallyTaxedWhenThisPaycheckCrossesTheCap() {
        // YTD $168,000 + this $5,000 check crosses the $168,600 cap after just $600 of it.
        double ytdBefore = 168_000;
        double grossThisPeriod = 5_000;
        double expectedTaxablePortion = FicaCalculator.SOCIAL_SECURITY_WAGE_BASE_2024 - ytdBefore; // 600
        double withheld = fica.socialSecurityWithholding(ytdBefore, grossThisPeriod);
        assertEquals(expectedTaxablePortion * FicaCalculator.SOCIAL_SECURITY_RATE, withheld, 1e-6);
    }

    @Test
    void socialSecurityExactlyAtTheCapBoundaryTaxesTheFullRemainingRoom() {
        double ytdBefore = FicaCalculator.SOCIAL_SECURITY_WAGE_BASE_2024 - 100;
        double withheld = fica.socialSecurityWithholding(ytdBefore, 1_000);
        assertEquals(100 * FicaCalculator.SOCIAL_SECURITY_RATE, withheld, 1e-9);
    }

    @Test
    void medicareHasNoWageBaseCapUnlikeSocialSecurity() {
        double withheld = fica.medicareWithholding(1_000_000);
        assertEquals(1_000_000 * FicaCalculator.MEDICARE_RATE, withheld, 1e-6);
    }

    @Test
    void additionalMedicareIsZeroBelowTheThreshold() {
        assertEquals(0.0, fica.additionalMedicareWithholding(150_000, 5_000), 1e-9);
    }

    @Test
    void additionalMedicareAppliesOnlyToThePortionOverTheThresholdWhenCrossingItMidPaycheck() {
        // YTD $198,000 + this $5,000 check: the first $2,000 of the check closes the gap to the
        // $200,000 threshold, leaving the remaining $3,000 of the check as the taxable portion.
        double ytdBefore = 198_000;
        double grossThisPeriod = 5_000;
        double withheld = fica.additionalMedicareWithholding(ytdBefore, grossThisPeriod);
        assertEquals(3_000 * FicaCalculator.ADDITIONAL_MEDICARE_RATE, withheld, 1e-6);
    }

    @Test
    void additionalMedicareAppliesToTheWholePaycheckWhenAlreadyPastTheThreshold() {
        double withheld = fica.additionalMedicareWithholding(250_000, 5_000);
        assertEquals(5_000 * FicaCalculator.ADDITIONAL_MEDICARE_RATE, withheld, 1e-6);
    }
}
