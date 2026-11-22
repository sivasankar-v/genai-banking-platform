package com.fincore.service.impl;

import com.fincore.enums.LoanType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanServiceImplTest {

    @Test
    void emiMatchesStandardFormula() {
        // 1,00,000 at 12% a year over 12 months is a textbook EMI of 8,884.88.
        assertThat(LoanServiceImpl.calculateEmi(new BigDecimal("100000"), 12.0, 12))
                .isEqualByComparingTo("8884.88");
    }

    @Test
    void emiForLongHomeLoan() {
        // 10,00,000 at 8.5% over 20 years: 8,678.23 a month.
        assertThat(LoanServiceImpl.calculateEmi(new BigDecimal("1000000"), 8.5, 240))
                .isEqualByComparingTo("8678.23");
    }

    @Test
    void zeroRateSplitsPrincipalEvenly() {
        assertThat(LoanServiceImpl.calculateEmi(new BigDecimal("1200"), 0, 12))
                .isEqualByComparingTo("100.00");
    }

    @Test
    void ratesComeFromLoanType() {
        assertThat(LoanServiceImpl.getInterestRate(LoanType.HOME)).isEqualTo(8.5);
        assertThat(LoanServiceImpl.getInterestRate(LoanType.PERSONAL)).isEqualTo(12.0);
    }
}
