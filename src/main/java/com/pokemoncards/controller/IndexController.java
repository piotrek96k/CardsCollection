package com.pokemoncards.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.service.AccountService;
import com.pokemoncards.model.service.CardService;

@Controller
public class IndexController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;

	@GetMapping(value = "/")
	public String indexPage(Model model, @RequestParam(value = "ids[]") Optional<String[]> ids,
			@RequestParam(value = "search") Optional<String> search) {
		model.addAttribute("accountId", accountService.getAccountId());
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("link", "/");
		model.addAttribute("cards", cardService.getCards(ids, search));
		model.addAttribute("numberOfCards", cardRepository.count());
		model.addAttribute("numberOfSets", setRepository.count());
		return "index";
	}

}