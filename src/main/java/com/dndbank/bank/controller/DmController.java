package com.dndbank.bank.controller;

import com.dndbank.bank.dto.AccountAdjustmentForm;
import com.dndbank.bank.dto.InstitutionForm;
import com.dndbank.bank.dto.RegistrationForm;
import com.dndbank.bank.dto.TransferForm;
import com.dndbank.bank.entity.Account;
import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import com.dndbank.bank.service.AccountService;
import com.dndbank.bank.service.InstitutionService;
import com.dndbank.bank.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DmController {
    private final AccountService accountService;
    private final InstitutionService institutionService;
    private final UserService userService;

    public DmController(AccountService accountService,
                        InstitutionService institutionService,
                        UserService userService) {
        this.accountService = accountService;
        this.institutionService = institutionService;
        this.userService = userService;
    }

    @GetMapping("/dm/dashboard")
    public String dashboard(Model model) {
        List<Account> accounts = accountService.findAll();
        List<User> allUsers = userService.findAll();
        model.addAttribute("accounts", accounts.stream()
                .sorted(Comparator.comparing(account -> account.getInstitution().getName()))
                .collect(Collectors.toList()));
        model.addAttribute("institutions", institutionService.findAll());
        model.addAttribute("players", allUsers.stream()
                .filter(user -> user.getRole() == Role.PLAYER)
                .toList());
        model.addAttribute("npcs", allUsers.stream()
                .filter(user -> user.getRole() == Role.NPC)
                .toList());
        model.addAttribute("totalHoldingsCopper", accountService.totalHoldingsCopper());
        model.addAttribute("institutionForm", new InstitutionForm());
        model.addAttribute("npcForm", new RegistrationForm());
        model.addAttribute("transferForm", new TransferForm());
        model.addAttribute("accountAdjustmentForm", new AccountAdjustmentForm());
        return "dm/dashboard";
    }

    @PostMapping("/dm/institutions")
    public String createInstitution(@Valid @ModelAttribute("institutionForm") InstitutionForm form,
                                    BindingResult result,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Provide institution details");
            return "redirect:/dm/dashboard";
        }
        User creator = userService.findByUsername(principal.getName()).orElseThrow();
        institutionService.createInstitution(form, creator);
        redirectAttributes.addFlashAttribute("message", "Institution onboarded");
        return "redirect:/dm/dashboard";
    }

    @PostMapping("/dm/npcs")
    public String createNpc(@Valid @ModelAttribute("npcForm") RegistrationForm form,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Check NPC form values");
            return "redirect:/dm/dashboard";
        }
        userService.registerNpc(form);
        redirectAttributes.addFlashAttribute("message", "NPC added");
        return "redirect:/dm/dashboard";
    }

    @PostMapping("/dm/transfer")
    public String dmTransfer(@Valid @ModelAttribute("transferForm") TransferForm form,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid transfer request");
            return "redirect:/dm/dashboard";
        }
        if (!form.hasPositiveAmount()) {
            redirectAttributes.addFlashAttribute("error", "Enter an amount greater than zero");
            return "redirect:/dm/dashboard";
        }
        try {
            accountService.transferByAccountNumber(form.getFromAccountId(), form.getTargetAccountNumber(),
                    form.amountInCopper());
            redirectAttributes.addFlashAttribute("message", "Transfer completed");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dm/dashboard";
    }

    @PostMapping("/dm/accounts/adjust")
    public String adjustAccount(@Valid @ModelAttribute("accountAdjustmentForm") AccountAdjustmentForm form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Provide valid adjustment details");
            return "redirect:/dm/dashboard";
        }
        accountService.adjustAccount(form);
        redirectAttributes.addFlashAttribute("message", "Account updated");
        return "redirect:/dm/dashboard";
    }

    @PostMapping("/dm/accounts/{accountId}/interest")
    public String applyInterest(@PathVariable Long accountId, RedirectAttributes redirectAttributes) {
        accountService.applyInterest(accountId);
        redirectAttributes.addFlashAttribute("message", "Interest applied");
        return "redirect:/dm/dashboard";
    }
}
