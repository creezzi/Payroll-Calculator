# PayrollCalc

Computes a per-paycheck federal tax withholding breakdown (income tax + FICA) the way real payroll
software has to: correctly handling the Social Security wage base cap and the Additional Medicare
threshold when a single paycheck straddles either boundary mid-year.

## The problem

Small businesses running payroll by hand (or with a spreadsheet) routinely get withholding wrong,
which means either shorting employees' paychecks or under-withholding and creating an IRS penalty
surprise. The progressive-bracket math itself is well understood, but the FICA wage-base-cap
mechanic is a genuinely common real-world payroll bug: it's easy to write "if grossPay < cap, tax
it" and get it right for every paycheck of the year except the one where year-to-date wages cross
the cap mid-paycheck.

## What it does

- **Progressive federal income tax**: standard-deduction-then-bracket calculation, annualized and
  divided back down to a per-paycheck amount (the same "annualize, tax, divide" approach the IRS's
  own percentage method uses).
- **Social Security (6.2%)** — correctly capped at the annual wage base, including the case where
  *this specific paycheck* is the one that crosses the cap: only the portion of the check actually
  under the cap gets taxed, not the whole check and not none of it.
- **Medicare (1.45%)** — uncapped, unlike Social Security.
- **Additional Medicare (0.9%)** — applies only above a wage threshold, with the same mid-paycheck
  boundary-crossing handling as the Social Security cap.

> Uses a simplified, single-filing-status model — not tax advice. Real withholding also depends on
> the employee's W-4 elections. The CLI prints this disclaimer on every run.

## Design

```
payrollcalc/
  TaxBracket             one marginal bracket (lower/upper bound + rate)
  FederalTaxTables       2024 single-filer brackets + standard deduction (documented snapshot)
  IncomeTaxCalculator    progressive bracket tax -> per-paycheck withholding
  FicaCalculator         Social Security + Medicare + Additional Medicare, cap-aware
  PaystubCalculator      combines both into one paycheck breakdown
  PayrollCalcApp         CLI
```

**The wage-base-cap logic in `FicaCalculator` is the real content here**, and it's directly tested:
`socialSecurityStopsEntirelyOnceAlreadyOverTheCap`, `socialSecurityIsPartiallyTaxedWhenThisPaycheckCrossesTheCap`,
and the equivalent pair for Additional Medicare all exist specifically because a naive
per-paycheck-only check (ignoring year-to-date wages) gets exactly these cases wrong.

## Running it

Requires a JDK (17+); `lib/` has the JUnit console jar used for tests.

```powershell
.\build.ps1     # Windows
```
```bash
./build.sh      # macOS/Linux
```

```
java -cp out/main payrollcalc.PayrollCalcApp <annualSalary> <payPeriodsPerYear> <ytdWagesBeforeThisPeriod>
```

```
=== A paycheck that crosses the Social Security wage base mid-year ===
java -cp out/main payrollcalc.PayrollCalcApp 220000 26 168000

Gross pay:            $  8,461.54
Federal income tax:  -$  1,669.79
Social Security:     -$     37.20    <- only $600 of this $8,461.54 check is under the $168,600 cap
Medicare:            -$    122.69
Additional Medicare: -$      0.00
Net pay:              $  6,631.86
```

## Tests

14 JUnit 5 tests: progressive bracket math (a case that would give a wrong answer under a flat-rate
miscalculation, verified explicitly), the standard deduction being applied before bracketing,
zero/negative income owing no tax, and the FICA cap/threshold boundary logic — fully taxed well
under the cap, zero tax once already over it, and the mid-paycheck crossing case for both the
Social Security cap and the Additional Medicare threshold, including the exact-boundary case.

## What I'd change for production

- Multiple filing statuses (married filing jointly, head of household) and W-4 adjustments
  (multiple jobs, dependents, extra withholding) instead of one hardcoded single-filer table.
- State income tax withholding, which varies enormously by state.
- Year-to-date tracking built in, rather than requiring the caller to supply it each time.
