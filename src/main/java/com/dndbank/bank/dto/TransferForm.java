package com.dndbank.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

public class TransferForm {
    @NotNull
    private Long fromAccountId;

    @Valid
    private MoneyAmountForm amount = new MoneyAmountForm();

    @NotBlank
    private String targetAccountNumber;

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public MoneyAmountForm getAmount() {
        return amount;
    }

    public void setAmount(MoneyAmountForm amount) {
        this.amount = amount;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public long amountInCopper() {
        return amount.toCopper();
    }

    public boolean hasPositiveAmount() {
        return amount.hasPositiveValue();
    }
}
