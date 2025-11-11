package com.dndbank.bank.service;

import com.dndbank.bank.dto.AccountAdjustmentForm;
import com.dndbank.bank.dto.AccountCreationForm;
import com.dndbank.bank.entity.Account;
import com.dndbank.bank.entity.AccountTransaction;
import com.dndbank.bank.entity.BankInstitution;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.AccountType;
import com.dndbank.bank.enums.TransactionType;
import com.dndbank.bank.repository.AccountRepository;
import com.dndbank.bank.repository.AccountTransactionRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final InstitutionService institutionService;

    public AccountService(AccountRepository accountRepository,
                          AccountTransactionRepository transactionRepository,
                          InstitutionService institutionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.institutionService = institutionService;
    }

    public List<Account> findByOwner(User owner) {
        return accountRepository.findByOwner(owner);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public long totalHoldingsCopper() {
        Long total = accountRepository.totalBankHoldingsCopper();
        return total == null ? 0L : total;
    }

    @Transactional
    public Account createAccount(User owner, AccountCreationForm form) {
        BankInstitution institution = institutionService.getById(form.getInstitutionId());
        Account account = new Account();
        account.setAccountNumber(com.dndbank.bank.support.AccountNumberGenerator.nextAccountNumber());
        account.setOwner(owner);
        account.setInstitution(institution);
        account.setType(form.getAccountType());
        account.setInterestRate(resolveInterestRate(form.getAccountType(), institution));
        account.setBalanceCopper(0L);
        return accountRepository.save(account);
    }

    @Transactional
    public void deposit(Long accountId, long amountCopper, String note) {
        if (amountCopper <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Account account = getAccount(accountId);
        account.setBalanceCopper(account.getBalanceCopper() + amountCopper);
        accountRepository.save(account);
        recordTransaction(account, amountCopper, TransactionType.DEPOSIT, note);
    }

    @Transactional
    public void withdraw(Long accountId, long amountCopper, String note) {
        if (amountCopper <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Account account = getAccount(accountId);
        if (account.getBalanceCopper() < amountCopper) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalanceCopper(account.getBalanceCopper() - amountCopper);
        accountRepository.save(account);
        recordTransaction(account, -amountCopper, TransactionType.WITHDRAWAL, note);
    }

    @Transactional
    public void transferByAccountNumber(Long fromAccountId, String targetAccountNumber, long amountCopper) {
        if (amountCopper <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Account from = getAccount(fromAccountId);
        Account to = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (from.getBalanceCopper() < amountCopper) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        from.setBalanceCopper(from.getBalanceCopper() - amountCopper);
        to.setBalanceCopper(to.getBalanceCopper() + amountCopper);
        accountRepository.save(from);
        accountRepository.save(to);
        recordTransaction(from, -amountCopper, TransactionType.TRANSFER_OUT,
                "Transfer to account " + to.getAccountNumber());
        recordTransaction(to, amountCopper, TransactionType.TRANSFER_IN,
                "Transfer from account " + from.getAccountNumber());
    }

    @Transactional
    public void applyInterest(Long accountId) {
        Account account = getAccount(accountId);
        if (account.getBalanceCopper() <= 0) {
            return;
        }
        BigDecimal monthlyRate = account.getInterestRate().divide(new BigDecimal("12"), 8, RoundingMode.HALF_UP);
        long interestCopper = BigDecimal.valueOf(account.getBalanceCopper())
                .multiply(monthlyRate)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
        if (interestCopper <= 0) {
            return;
        }
        account.setBalanceCopper(account.getBalanceCopper() + interestCopper);
        accountRepository.save(account);
        recordTransaction(account, interestCopper, TransactionType.INTEREST, "Monthly interest");
    }

    @Transactional
    public void adjustAccount(AccountAdjustmentForm form) {
        Account account = getAccount(form.getAccountId());
        if (form.shouldUpdateBalance()) {
            account.setBalanceCopper(form.balanceInCopper());
        }
        if (form.getInterestRate() != null) {
            account.setInterestRate(form.getInterestRate());
        }
        accountRepository.save(account);
    }

    public List<AccountTransaction> recentTransactions(Long accountId) {
        return transactionRepository.findTop10ByAccountIdOrderByOccurredAtDesc(accountId);
    }

    private void recordTransaction(Account account, long amountCopper, TransactionType type, String note) {
        AccountTransaction tx = new AccountTransaction();
        tx.setAccount(account);
        tx.setAmountCopper(amountCopper);
        tx.setType(type);
        tx.setDescription(note);
        tx.setOccurredAt(Instant.now());
        transactionRepository.save(tx);
    }

    private BigDecimal resolveInterestRate(AccountType type, BankInstitution institution) {
        return institution.getBaseInterestRate().add(type.getAnnualInterestRate());
    }
}
