package com.pokemoncards.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.service.AccountService;
import com.pokemoncards.model.service.SortType;

@Controller
public class IndexController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping(value = "/")
	public String indexPage(Model model, @RequestParam("page") Optional<Integer> page) {
		int currentPage = page.orElse(1);
		model.addAttribute("cards", accountService.getUserCards(currentPage,SortType.COST, SortType.COST.ASC, new ArrayList<Rarity>(), new ArrayList<Set>(), new ArrayList<Type>(), Optional.empty()));
		model.addAttribute("numberOfPages", accountService.getUserCardsNumberOfPages(new ArrayList<Rarity>(), new ArrayList<Set>(), new ArrayList<Type>(), Optional.empty()));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("/link", "");
		return "index";
	}

	@GetMapping(value = "/sold")
	public String sellPage(Model model, @RequestParam("id") String id, @RequestParam("page") int page) {
		model.addAttribute("card",accountService.getCardToSell(id));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("page", page);
		return "sold";
	}

	@GetMapping(value = "/sold/sold")
	public String boughtPage(@RequestParam("id") String id, @RequestParam("page") int page) {
		accountService.removeCard(id);
		return "redirect:/?page="+page;
	}
	
}