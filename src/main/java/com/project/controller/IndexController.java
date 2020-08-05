package com.project.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.service.AccountService;

@Controller
public class IndexController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/")
	public String indexPage(Model model, @RequestParam("page") Optional<Integer> page) {
		int currentPage = page.orElse(1);
		model.addAttribute("cards", accountService.getUserCards(currentPage));
		model.addAttribute("numberOfPages", accountService.getUserCardsNumberOfPages());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("/link", "");
		return "index";
	}
	
}
