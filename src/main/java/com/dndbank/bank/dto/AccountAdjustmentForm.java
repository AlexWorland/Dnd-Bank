package com.dndbank.bank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountAdjustmentForm {
    @NotNull
    private Long accountId;

    @Valid
    private MoneyAmountForm balance = new MoneyAmountForm();

    @DecimalMin("0.00")
    private BigDecimal interestRate;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public MoneyAmountForm getBalance() {
        return balance;
    }

    public void setBalance(MoneyAmountForm balance) {
        this.balance = balance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public boolean shouldUpdateBalance() {
        return balance.isProvided();
    }

    public long balanceInCopper() {
        return balance.toCopper();
    }
}
