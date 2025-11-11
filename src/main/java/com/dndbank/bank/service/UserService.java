package com.dndbank.bank.service;

import com.dndbank.bank.dto.RegistrationForm;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import com.dndbank.bank.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public User registerPlayer(RegistrationForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName());
        user.setRole(Role.PLAYER);
        return userRepository.save(user);
    }

    @Transactional
    public User registerNpc(RegistrationForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName());
        user.setRole(Role.NPC);
        return userRepository.save(user);
    }

    @Transactional
    public User registerInstitutionAdmin(RegistrationForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName());
        user.setRole(Role.INSTITUTION_ADMIN);
        return userRepository.save(user);
    }

    @Transactional
    public void ensureDungeonMaster(String username, String password, String displayName) {
        if (userRepository.existsByRole(Role.DM)) {
            return;
        }
        User dm = new User();
        dm.setUsername(username);
        dm.setPassword(passwordEncoder.encode(password));
        dm.setDisplayName(displayName);
        dm.setRole(Role.DM);
        userRepository.save(dm);
    }
}
