package com.dndbank.bank.support;

import com.dndbank.bank.dto.InstitutionForm;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import com.dndbank.bank.repository.BankInstitutionRepository;
import com.dndbank.bank.repository.UserRepository;
import com.dndbank.bank.service.InstitutionService;
import com.dndbank.bank.service.UserService;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final InstitutionService institutionService;
    private final BankInstitutionRepository bankInstitutionRepository;

    @Value("${app.default-dm.username:dm}")
    private String defaultDmUsername;

    @Value("${app.default-dm.password:ChangeMe123!}")
    private String defaultDmPassword;

    @Value("${app.default-dm.display-name:Dungeon Master}")
    private String defaultDmDisplayName;

    @Value("${app.default-institution.name:Gilded Griffon Bank}")
    private String defaultInstitutionName;

    public DataInitializer(UserService userService,
                           UserRepository userRepository,
                           InstitutionService institutionService,
                           BankInstitutionRepository bankInstitutionRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.institutionService = institutionService;
        this.bankInstitutionRepository = bankInstitutionRepository;
    }

    @PostConstruct
    public void seedData() {
        userService.ensureDungeonMaster(defaultDmUsername, defaultDmPassword, defaultDmDisplayName);
        if (!bankInstitutionRepository.existsByName(defaultInstitutionName)) {
            log.info("Creating default institution {}", defaultInstitutionName);
            User dm = userRepository.findByRole(Role.DM).orElseThrow();
            InstitutionForm form = new InstitutionForm();
            form.setName(defaultInstitutionName);
            form.setDescription("Default campaign treasury");
            form.setBaseInterestRate(new BigDecimal("0.02"));
            institutionService.createInstitution(form, dm);
        }
    }
}
