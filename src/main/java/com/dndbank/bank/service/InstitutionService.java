package com.dndbank.bank.service;

import com.dndbank.bank.dto.InstitutionForm;
import com.dndbank.bank.entity.BankInstitution;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.repository.BankInstitutionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService {
    private final BankInstitutionRepository repository;

    public InstitutionService(BankInstitutionRepository repository) {
        this.repository = repository;
    }

    public List<BankInstitution> findAll() {
        return repository.findAll();
    }

    public BankInstitution getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional
    public BankInstitution createInstitution(InstitutionForm form, User createdBy) {
        BankInstitution institution = new BankInstitution();
        institution.setName(form.getName());
        institution.setDescription(form.getDescription());
        institution.setBaseInterestRate(form.getBaseInterestRate());
        institution.setCreatedBy(createdBy);
        return repository.save(institution);
    }
}
