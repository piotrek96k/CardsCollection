package com.pokemoncards.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.pokemoncards.annotation.OnRegister;
import com.pokemoncards.exception.AccountAlreadyActivatedException;
import com.pokemoncards.model.component.PasswordRepeatData;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.service.RegisterService;

@Controller
public class RegisterController {

	@Autowired
	private RegisterService registerService;

	@GetMapping(value = "/register")
	public String registerForm(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("pswRepeat", new PasswordRepeatData());
		return "register";
	}

	@PostMapping(value = "/register")
	public String registerUser(Model model, @Validated(OnRegister.class) Account account, @Valid PasswordRepeatData pswData) {
		registerService.addAccount(account);
		model.addAttribute("username", account.getUsername());
		return "registersucces";
	}

	@PostMapping(value = "/register/generate")
	public String generateNewTokenFromUsername(Model model,@ModelAttribute(value = "username")String username) {
		registerService.generateNewActivationToken(username);
		model.addAttribute("username", username);
		return "registersucces";
	}

	@GetMapping(value = "/register/{token}")
	public String activateAccount(@PathVariable(value = "token") String token) {
		registerService.activateAccount(token);
		return "accountactivated";
	}

	@ExceptionHandler(BindException.class)
	public String handleValidationExceptions(Model model, BindException exception) {
		List<String> errors = new ArrayList<String>();
		exception.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
		model.addAttribute("errors", errors);
		return registerForm(model);
	}

	@ExceptionHandler(AccountAlreadyActivatedException.class)
	public String handleAccountAlreadyActivated() {
		return "accountActivated";
	}

}