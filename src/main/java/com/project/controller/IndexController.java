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
	
	@GetMapping(value = "/")
	public String indexPage(Model model, @RequestParam("page") Optional<Integer> page) {
		int currentPage = page.orElse(1);
		model.addAttribute("cards", accountService.getUserCards(currentPage));
		model.addAttribute("numberOfPages", accountService.getUserCardsNumberOfPages());
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("/link", "");
		return "index";
	}

	@GetMapping(value = "/sell")
	public String sellPage(Model model, @RequestParam("id") String id, @RequestParam("page") int page) {
		model.addAttribute("card",accountService.getQuantityCardToSell(id));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("page", page);
		return "sell";
	}

	@GetMapping(value = "/sell/sold")
	public String boughtPage(@RequestParam("id") String id, @RequestParam("page") int page) {
		accountService.removeCard(id);
		return "redirect:/?page="+page;
	}
	
}