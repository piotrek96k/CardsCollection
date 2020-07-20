package com.project.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.entity.Account;
import com.project.model.service.AccountService;
import com.project.model.service.RegistrationError;

@Controller
public class MenuController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/")
	public String indexPage() {
		return "index";
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		return "login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("account", new Account());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(Account account, @RequestParam(name = "pswRepeat") String pswRepeat, Model model) {
		Optional<RegistrationError> error = accountService.addAccount(account, pswRepeat);
		if (error.isPresent()) {
			model.addAttribute(error.get().getError(), true);
			model.addAttribute("account", new Account());
			return "register";
		}
		model.addAttribute("succes", true);
		return "registersucces";
	}

}
