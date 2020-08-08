package com.project.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.entity.QuantityCard;
import com.project.model.entity.Rarity;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RarityRepository;
import com.project.model.service.AccountService;

@Controller
public class GalleryController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private RarityRepository rarityRepository;

	@Autowired
	private AccountService accountService;

	@GetMapping("/gallery")
	public String galleryPage(Model model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities) {
		int currentPage = page.orElse(1);
		List<String> selectedRarities = getSelectedRaritiesAsList(rarities);
		model.addAttribute("cards", getCards(currentPage, selectedRarities));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages", getNumberOfPages(selectedRarities));
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("link", "/gallery");
		model.addAttribute("rarities", getSelectedRarities(rarities));
		model.addAttribute("selectedRarities", rarities.orElse(""));
		return "gallery";
	}

	private List<String> getSelectedRaritiesAsList(Optional<String> rarities) {
		if (rarities.isEmpty() || rarities.get().isBlank())
			return new ArrayList<String>();
		return Arrays.asList(rarities.get().split(","));
	}

	private List<QuantityCard> getCards(int page, List<String> rarities) {
		if (rarities.isEmpty())
			return accountService.getGalleryCards(page);
		return accountService.getGalleryCardsWithSelectedRarities(page, rarities);
	}

	private int getNumberOfPages(List<String> rarities) {
		if (rarities.isEmpty())
			return cardRepository.getNumberOfPages();
		return cardRepository.getNumberOfPagesWithSelectedRarities(rarities);
	}

	private Map<String, Boolean> getSelectedRarities(Optional<String> rarityString) {
		Map<String, Boolean> result = new TreeMap<String, Boolean>();
		if (rarityString.isEmpty()) {
			for (Rarity rarity : rarityRepository.findAll())
				result.put(rarity.getId(), false);
			return result;
		}
		List<String> selectedRarities = Arrays.asList(rarityString.get().split(","));
		for (Rarity rarity : rarityRepository.findAll())
			result.put(rarity.getId(), selectedRarities.contains(rarity.getId()));
		return result;
	}

	@GetMapping(value = "/gallery/buy")
	public String buyPage(Model model, @RequestParam("id") String id, @RequestParam("page") int page) {
		model.addAttribute("card", accountService.getQuantityCard(id));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("page", page);
		return "buy";
	}

	@GetMapping(value = "/gallery/bought")
	public String boughtPage(@RequestParam("id") String id, @RequestParam("page") int page) {
		accountService.addCard(id);
		return "redirect:/gallery?page=" + page;
	}

}