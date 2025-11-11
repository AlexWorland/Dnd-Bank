package com.dndbank.bank.dto;

import com.dndbank.bank.enums.AccountType;
import jakarta.validation.constraints.NotNull;

public class AccountCreationForm {
    @NotNull
    private AccountType accountType;

    @NotNull
    private Long institutionId;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }
}
