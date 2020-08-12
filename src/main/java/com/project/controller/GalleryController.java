package com.project.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.component.SessionData;
import com.project.model.entity.Rarity;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RarityRepository;
import com.project.model.service.AccountService;
import com.project.model.service.SortType;

@Controller
@Scope(value = "session")
public class GalleryController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private RarityRepository rarityRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SessionData sessionData;

	public static class StringWrapper {

		public String string;

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

	}

	public static class SearchWrapper {

		public String search;

		public String getSearch() {
			return search;
		}

		public void setSearch(String search) {
			this.search = search;
		}

	}

	@GetMapping("/gallery")
	public String galleryPage(Model model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "search") Optional<String> search) {
		int currentPage = page.orElse(1);
		List<Rarity> selectedRarities = getSelectedRaritiesAsList(rarities);
		model.addAttribute("cards", accountService.getGalleryCards(currentPage, sessionData.getSortType(),
				sessionData.getOrderType(), selectedRarities, search));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages", getNumberOfPages(selectedRarities, search));
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("link", "/gallery");
		model.addAttribute("rarities", getSelectedRarities(rarities));
		model.addAttribute("selectedRarities", rarities.orElse(""));
		model.addAttribute("sortOptions", SortType.values());
		model.addAttribute("sessionData", sessionData);
		model.addAttribute("selectedSortOption", new StringWrapper());
		model.addAttribute("selectedOrderOption", new StringWrapper());
		model.addAttribute("searchInput", new SearchWrapper());
		model.addAttribute("enteredSearch", search.orElse(""));
		return "gallery";
	}

	@PostMapping("/gallery")
	public String sortSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities, @ModelAttribute StringWrapper selectedOption,
			@ModelAttribute SearchWrapper search) {
		if (selectedOption.getString() != null) {
			SortType.OrderType orderType = sessionData.getSortType().getOrderType(selectedOption.getString());
			if (orderType == null)
				sessionData.setSortType(SortType.valueOf(selectedOption.getString()));
			else
				sessionData.setOrderType(orderType);
		}
		Optional<String> searchResult = search.getSearch() == null || search.getSearch().isEmpty()
				|| search.getSearch().isBlank() ? Optional.empty() : Optional.of(search.getSearch());
		return getGalleryRedirectString(page, rarities, searchResult);
	}

	@GetMapping(value = "/gallery/buy")
	public String buyCard(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "search") Optional<String> search, @RequestParam(value = "id") String id) {
		accountService.addCard(id);
		return getGalleryRedirectString(page, rarities, search);
	}
	
	private String getGalleryRedirectString(Optional<Integer> page, Optional<String> rarities,
			Optional<String> search) {
		StringBuilder builder = new StringBuilder();
		builder.append("redirect:/gallery");
		boolean added = false;
		if (page.isPresent()) {
			builder.append("?page=");
			builder.append(page.get());
			added = true;
		}
		if (rarities.isPresent()) {
			appendSign(builder, added);
			builder.append("rarity=");
			builder.append(rarities.get());
			added = true;
		}
		if (search.isPresent()) {
			appendSign(builder, added);
			builder.append("search=");
			builder.append(search.get());
		}
		return builder.toString();
	}

	private void appendSign(StringBuilder builder, boolean added) {
		if (added)
			builder.append('&');
		else
			builder.append('?');
	}

	private List<Rarity> getSelectedRaritiesAsList(Optional<String> rarities) {
		if (rarities.isEmpty() || rarities.get().isBlank())
			return new ArrayList<Rarity>();
		List<Rarity> raritiesList = new ArrayList<Rarity>();
		for (String rarity : rarities.get().split(","))
			raritiesList.add(new Rarity(rarity));
		return raritiesList;
	}

	private int getNumberOfPages(List<Rarity> rarities, Optional<String> search) {
		if (rarities.isEmpty()) {
			if (search.isEmpty())
				return cardRepository.getNumberOfPages();
			return cardRepository.getNumberOfPagesWithSearch(search.get());
		}
		if (search.isEmpty())
			return cardRepository.getNumberOfPagesWithSelectedRarities(rarities);
		return cardRepository.getNumberOfPagesWithSelectedRaritiesWithSearch(rarities, search.get());
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

}