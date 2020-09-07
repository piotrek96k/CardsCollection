package com.pokemoncards.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pokemoncards.model.service.AccountService;

@Controller
public class ApplicationErrorController implements ErrorController {

	@Autowired
	private AccountService accountService;

	@RequestMapping("/error")
	public String errorPage(Model model, HttpServletRequest request) {
		if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken)
			model.addAttribute("cash", accountService.getCash());
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			int code = Integer.valueOf(status.toString());
			model.addAttribute("errorCode", code);
			model.addAttribute("errorMessage", HttpStatus.valueOf(code).getReasonPhrase());
		}
		return "errorpage";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}