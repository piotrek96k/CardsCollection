package com.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

	private List<String> errorFields;

	{
		errorFields = new ArrayList<String>();
	}

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
		if (!errorFields.isEmpty()) {
			List<String> list = new ArrayList<String>(errorFields);
			model.addAttribute("errorFields", list);
			errorFields.clear();
		}
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Validated Account account, @RequestParam(name = "pswRepeat") String pswRepeat,
			Model model) {
		Optional<RegistrationError> error = accountService.addAccount(account, pswRepeat);
		if (error.isPresent()) {
			model.addAttribute(error.get().getError(), true);
			model.addAttribute("account", new Account());
			return "register";
		}
		return "registersucces";
	}

	@ExceptionHandler(BindException.class)
	public String handleValidationExceptions(BindException exception, Model model) {
		exception.getFieldErrors().forEach(error -> errorFields.add(changeString(error.getField())));
		return "redirect:/register";
	}

	private String changeString(String string) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			if (Character.isUpperCase(string.charAt(i)))
				builder.append(" ");
			builder.append(string.charAt(i));
		}
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		return builder.toString();
	}

}
