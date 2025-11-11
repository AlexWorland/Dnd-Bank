package com.dndbank.bank.enums;

import java.math.BigDecimal;

public enum AccountType {
    CHECKING(new BigDecimal("0.01")),
    SAVINGS(new BigDecimal("0.03"));

    private final BigDecimal annualInterestRate;

    AccountType(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }
}
