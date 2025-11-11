package com.dndbank.bank.repository;

import com.dndbank.bank.entity.BankInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankInstitutionRepository extends JpaRepository<BankInstitution, Long> {
    boolean existsByName(String name);
}
