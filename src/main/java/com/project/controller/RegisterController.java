package com.project.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.model.component.PasswordRepeatData;
import com.project.model.entity.Account;
import com.project.model.service.AccountService;

@Controller
public class RegisterController {

	@Autowired
	private AccountService accountService;

	@GetMapping(value = "/register")
	public String registerForm(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("pswRepeat", new PasswordRepeatData());
		return "register";
	}

	@PostMapping(value = "/register")
	public String registerUser(@Valid Account account, @Valid PasswordRepeatData pswData) {
		accountService.addAccount(account);
		return "registersucces";
	}

	@ExceptionHandler(BindException.class)
	public String handleValidationExceptions(Model model, BindException exception) {
		List<String> errors = new ArrayList<String>();
		exception.getAllErrors().forEach(error->errors.add(error.getDefaultMessage()));
		model.addAttribute("errors",errors);
		return registerForm(model);
	}

}