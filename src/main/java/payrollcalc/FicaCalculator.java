package payrollcalc;

/**
 * FICA (Social Security + Medicare) withholding for one paycheck.
 *
 * <p>The one genuinely easy-to-get-wrong piece: Social Security tax only applies up to an annual
 * wage base cap (2024: $168,600), and Additional Medicare only kicks in above a threshold
 * ($200,000 single). A paycheck that straddles either boundary - year-to-date wages are just under
 * the line, and this check's gross pay pushes past it - must be split so only the portion of
 * *this* paycheck under the cap gets taxed, not the whole thing and not none of it. A naive
 * "is grossPay under the cap" check gets this wrong the moment YTD wages matter, which is every
 * paycheck after the first for anyone who will cross the cap that year.
 */
public final class FicaCalculator {

    public static final double SOCIAL_SECURITY_RATE = 0.062;
    public static final double SOCIAL_SECURITY_WAGE_BASE_2024 = 168_600.0;
    public static final double MEDICARE_RATE = 0.0145;
    public static final double ADDITIONAL_MEDICARE_RATE = 0.009;
    public static final double ADDITIONAL_MEDICARE_THRESHOLD_SINGLE = 200_000.0;

    public double socialSecurityWithholding(double ytdWagesBeforeThisPeriod, double grossPayThisPeriod) {
        double remainingCapRoom = SOCIAL_SECURITY_WAGE_BASE_2024 - ytdWagesBeforeThisPeriod;
        double taxableThisPeriod = clamp(Math.min(grossPayThisPeriod, Math.max(0, remainingCapRoom)), grossPayThisPeriod);
        return taxableThisPeriod * SOCIAL_SECURITY_RATE;
    }

    public double medicareWithholding(double grossPayThisPeriod) {
        return grossPayThisPeriod * MEDICARE_RATE; // uncapped, no wage base
    }

    public double additionalMedicareWithholding(double ytdWagesBeforeThisPeriod, double grossPayThisPeriod) {
        double totalYtdAfterThisPeriod = ytdWagesBeforeThisPeriod + grossPayThisPeriod;
        if (totalYtdAfterThisPeriod <= ADDITIONAL_MEDICARE_THRESHOLD_SINGLE) {
            return 0.0;
        }
        double alreadyOverThreshold = Math.max(0, ytdWagesBeforeThisPeriod - ADDITIONAL_MEDICARE_THRESHOLD_SINGLE);
        double amountOverThresholdIncludingThisPeriod = totalYtdAfterThisPeriod - ADDITIONAL_MEDICARE_THRESHOLD_SINGLE;
        double taxableThisPeriod = amountOverThresholdIncludingThisPeriod - alreadyOverThreshold;
        return taxableThisPeriod * ADDITIONAL_MEDICARE_RATE;
    }

    private double clamp(double value, double max) {
        return Math.min(Math.max(value, 0), max);
    }
}
