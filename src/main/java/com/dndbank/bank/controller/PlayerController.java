package com.dndbank.bank.controller;

import com.dndbank.bank.dto.AccountCreationForm;
import com.dndbank.bank.dto.MoneyMovementForm;
import com.dndbank.bank.dto.TransferForm;
import com.dndbank.bank.entity.Account;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import com.dndbank.bank.service.AccountService;
import com.dndbank.bank.service.InstitutionService;
import com.dndbank.bank.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PlayerController {
    private final UserService userService;
    private final AccountService accountService;
    private final InstitutionService institutionService;

    public PlayerController(UserService userService, AccountService accountService, InstitutionService institutionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.institutionService = institutionService;
    }

    @GetMapping("/player/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        if (user.getRole() == Role.DM) {
            return "redirect:/dm/dashboard";
        }
        List<Account> accounts = accountService.findByOwner(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("accountCreationForm", new AccountCreationForm());
        model.addAttribute("depositForm", new MoneyMovementForm());
        model.addAttribute("withdrawForm", new MoneyMovementForm());
        model.addAttribute("transferForm", new TransferForm());
        model.addAttribute("institutions", institutionService.findAll());
        model.addAttribute("transactions", accounts.stream()
                .findFirst()
                .map(account -> accountService.recentTransactions(account.getId()))
                .orElse(List.of()));
        return "player/dashboard";
    }

    @PostMapping("/player/accounts")
    public String createAccount(@Valid @ModelAttribute("accountCreationForm") AccountCreationForm form,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please select an account type and institution");
            return "redirect:/player/dashboard";
        }
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        accountService.createAccount(user, form);
        redirectAttributes.addFlashAttribute("message", "Account created");
        return "redirect:/player/dashboard";
    }

    @PostMapping("/player/deposit")
    public String deposit(@Valid @ModelAttribute("depositForm") MoneyMovementForm form,
                          BindingResult result,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        if (!validateOwnership(form.getAccountId(), principal, redirectAttributes) || result.hasErrors()) {
            return "redirect:/player/dashboard";
        }
        if (!form.hasPositiveAmount()) {
            redirectAttributes.addFlashAttribute("error", "Enter an amount greater than zero");
            return "redirect:/player/dashboard";
        }
        accountService.deposit(form.getAccountId(), form.amountInCopper(), "Player deposit");
        redirectAttributes.addFlashAttribute("message", "Deposit successful");
        return "redirect:/player/dashboard";
    }

    @PostMapping("/player/withdraw")
    public String withdraw(@Valid @ModelAttribute("withdrawForm") MoneyMovementForm form,
                           BindingResult result,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (!validateOwnership(form.getAccountId(), principal, redirectAttributes) || result.hasErrors()) {
            return "redirect:/player/dashboard";
        }
        if (!form.hasPositiveAmount()) {
            redirectAttributes.addFlashAttribute("error", "Enter an amount greater than zero");
            return "redirect:/player/dashboard";
        }
        try {
            accountService.withdraw(form.getAccountId(), form.amountInCopper(), "Player withdrawal");
            redirectAttributes.addFlashAttribute("message", "Withdrawal successful");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/player/dashboard";
    }

    @PostMapping("/player/transfer")
    public String transfer(@Valid @ModelAttribute("transferForm") TransferForm form,
                           BindingResult result,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (!validateOwnership(form.getFromAccountId(), principal, redirectAttributes) || result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid transfer request");
            return "redirect:/player/dashboard";
        }
        if (!form.hasPositiveAmount()) {
            redirectAttributes.addFlashAttribute("error", "Enter an amount greater than zero");
            return "redirect:/player/dashboard";
        }
        try {
            accountService.transferByAccountNumber(form.getFromAccountId(), form.getTargetAccountNumber(),
                    form.amountInCopper());
            redirectAttributes.addFlashAttribute("message", "Transfer sent");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/player/dashboard";
    }

    private boolean validateOwnership(Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        if (accountId == null) {
            redirectAttributes.addFlashAttribute("error", "Select an account");
            return false;
        }
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        return accountService.findByOwner(user).stream()
                .anyMatch(account -> account.getId().equals(accountId));
    }
}
