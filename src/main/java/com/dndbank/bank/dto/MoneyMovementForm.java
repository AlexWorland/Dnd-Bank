package com.dndbank.bank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class MoneyMovementForm {
    @NotNull
    private Long accountId;

    @Valid
    private MoneyAmountForm amount = new MoneyAmountForm();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public MoneyAmountForm getAmount() {
        return amount;
    }

    public void setAmount(MoneyAmountForm amount) {
        this.amount = amount;
    }

    public long amountInCopper() {
        return amount.toCopper();
    }

    public boolean hasPositiveAmount() {
        return amount.hasPositiveValue();
    }
}
