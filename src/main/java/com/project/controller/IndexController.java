package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.model.service.AccountService;

@Controller
public class IndexController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/")
	public String indexPage(Model model) {
		model.addAttribute("cards", accountService.getCards());
		return "index";
	}
	
}
