package payrollcalc;

/**
 * CLI: {@code java -cp out/main payrollcalc.PayrollCalcApp <annualSalary> <payPeriodsPerYear>
 * <ytdWagesBeforeThisPeriod>}
 */
public final class PayrollCalcApp {

    private static final String DISCLAIMER =
            "PayrollCalc uses a simplified, single-filing-status model - not tax advice.\n"
            + "Verify against current IRS Publication 15-T before relying on this for real payroll.\n";

    public static void main(String[] args) {
        System.out.print(DISCLAIMER);
        if (args.length < 3) {
            System.out.println("Usage: PayrollCalcApp <annualSalary> <payPeriodsPerYear> <ytdWagesBeforeThisPeriod>");
            return;
        }
        double annualSalary = Double.parseDouble(args[0]);
        int payPeriodsPerYear = Integer.parseInt(args[1]);
        double ytdWages = Double.parseDouble(args[2]);

        var result = new PaystubCalculator().calculate(annualSalary, payPeriodsPerYear, ytdWages);

        System.out.printf("Gross pay:            $%,10.2f%n", result.grossPay());
        System.out.printf("Federal income tax:  -$%,10.2f%n", result.federalIncomeTax());
        System.out.printf("Social Security:     -$%,10.2f%n", result.socialSecurity());
        System.out.printf("Medicare:            -$%,10.2f%n", result.medicare());
        System.out.printf("Additional Medicare: -$%,10.2f%n", result.additionalMedicare());
        System.out.printf("Net pay:              $%,10.2f%n", result.netPay());
    }
}
