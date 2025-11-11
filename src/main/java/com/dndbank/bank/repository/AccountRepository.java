package com.dndbank.bank.repository;

import com.dndbank.bank.entity.Account;
import com.dndbank.bank.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwner(User owner);
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT COALESCE(SUM(a.balanceCopper), 0) FROM Account a")
    Long totalBankHoldingsCopper();

    @Query("SELECT COALESCE(SUM(a.balanceCopper), 0) FROM Account a WHERE a.owner.id = :ownerId")
    Long totalByOwnerCopper(@Param("ownerId") Long ownerId);
}
