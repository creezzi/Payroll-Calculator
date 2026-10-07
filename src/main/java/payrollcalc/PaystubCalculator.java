package payrollcalc;

/** Combines federal income tax and FICA into one paycheck breakdown. */
public final class PaystubCalculator {

    private final IncomeTaxCalculator incomeTax = new IncomeTaxCalculator();
    private final FicaCalculator fica = new FicaCalculator();

    public PaystubResult calculate(double annualSalary, int payPeriodsPerYear, double ytdWagesBeforeThisPeriod) {
        double grossPay = annualSalary / payPeriodsPerYear;

        double federalIncomeTax = incomeTax.perPaycheckFederalWithholding(
                annualSalary, payPeriodsPerYear, FederalTaxTables.STANDARD_DEDUCTION_SINGLE,
                FederalTaxTables.SINGLE_BRACKETS_2024);
        double socialSecurity = fica.socialSecurityWithholding(ytdWagesBeforeThisPeriod, grossPay);
        double medicare = fica.medicareWithholding(grossPay);
        double additionalMedicare = fica.additionalMedicareWithholding(ytdWagesBeforeThisPeriod, grossPay);

        double totalTax = federalIncomeTax + socialSecurity + medicare + additionalMedicare;
        double netPay = grossPay - totalTax;

        return new PaystubResult(grossPay, federalIncomeTax, socialSecurity, medicare, additionalMedicare, netPay);
    }

    public record PaystubResult(double grossPay, double federalIncomeTax, double socialSecurity,
                                 double medicare, double additionalMedicare, double netPay) {
    }
}
