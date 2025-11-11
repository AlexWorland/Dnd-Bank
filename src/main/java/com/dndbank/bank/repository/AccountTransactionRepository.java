package com.dndbank.bank.repository;

import com.dndbank.bank.entity.AccountTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    List<AccountTransaction> findTop10ByAccountIdOrderByOccurredAtDesc(Long accountId);
}
