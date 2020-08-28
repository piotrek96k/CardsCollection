package com.pokemoncards.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.service.AccountService;
import com.pokemoncards.model.service.CardService;

@Controller
public class HomeController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CardService cardService;

	@RestController
	public static class HomeRestController {

		@Autowired
		private AccountService accountService;

		@GetMapping(value = "/home/get/cash")
		public String getCash() {
			return accountService.getCashAsJson();
		}
		
		@GetMapping(value = "/home/get/freecard")
		public String getFreeCard() {
			return accountService.getFreeCardAsJson();
		}

		@GetMapping(value = "/home/collect/coins")
		public String collectCoins() {
			return accountService.collectCoins();
		}

	}

	@GetMapping(value = "/")
	public String indexPage() {
		return "redirect:/home";
	}

	@GetMapping(value = "/home")
	public String homePage(Model model, @RequestParam(value = "ids[]") Optional<String[]> ids,
			@RequestParam(value = "search") Optional<String> search) {
		model.addAttribute("accountId", accountService.getAccountId());
		model.addAttribute("cash", accountService.getCash());
		model.addAttribute("cards", cardService.getCards(ids, search));
		model.addAttribute("numberOfCards", cardRepository.count());
		model.addAttribute("numberOfSets", setRepository.count());
		return "home";
	}

	@GetMapping(value = "/home/collect/freecard")
	public String freeCard(Model model) {
		model.addAttribute("card", accountService.collectFreeCard());
		return "freecard";
	}

}