package com.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.project.model.component.SessionData;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RarityRepository;
import com.project.model.repository.SetRepository;
import com.project.model.repository.TypeRepository;
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
	private SetRepository setRepository;

	@Autowired
	private TypeRepository typeRepository;

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
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search) {
		int currentPage = page.orElse(1);
		List<Rarity> selectedRarities = getSelectedObjectsAsList(rarities, rarityRepository);
		List<Set> selectedSets = getSelectedObjectsAsList(sets, setRepository);
		List<Type> selectedTypes = getSelectedObjectsAsList(types, typeRepository);
		model.addAttribute("cards", accountService.getGalleryCards(currentPage, sessionData.getSortType(),
				sessionData.getOrderType(), selectedRarities, selectedSets, selectedTypes, search));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages",
				cardRepository.getNumberOfPages(selectedRarities, selectedSets, selectedTypes, search));
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("link", "/gallery");
		model.addAttribute("rarities", getSelectedObjectsMap(selectedRarities, rarityRepository));
		model.addAttribute("selectedRarities", rarities.orElse(""));
		model.addAttribute("sets", getSelectedObjectsMap(selectedSets, setRepository));
		model.addAttribute("selectedSets", sets.orElse(""));
		model.addAttribute("selectedTypes", types.orElse(""));
		model.addAttribute("types", getSelectedObjectsMap(selectedTypes, typeRepository));
		model.addAttribute("sortOptions", SortType.values());
		model.addAttribute("sessionData", sessionData);
		model.addAttribute("selectedSortOption", new StringWrapper());
		model.addAttribute("selectedOrderOption", new StringWrapper());
		model.addAttribute("searchInput", new SearchWrapper());
		model.addAttribute("enteredSearch", search.orElse(""));
		return "gallery";
	}

	@PostMapping("/gallery")
	public ModelAndView sortSelection(ModelMap model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@ModelAttribute(name = "selectedOption") StringWrapper selectedOption,
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
		return redirectToGallery(model, page, rarities, sets, types, searchResult);
	}

	private ModelAndView redirectToGallery(ModelMap model, Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types, Optional<String> search) {
		if (page.isPresent())
			model.addAttribute("page", page.get());
		if (rarities.isPresent())
			model.addAttribute("rarity", rarities.get());
		if (sets.isPresent())
			model.addAttribute("set", sets.get());
		if (search.isPresent())
			model.addAttribute("search", search.get());
		if (types.isPresent())
			model.addAttribute("type", types.get());
		return new ModelAndView("redirect:/gallery", model);
	}

	private <T, U extends JpaRepository<T, ? super String>> List<T> getSelectedObjectsAsList(
			Optional<String> selectedString, U repository) {
		if (selectedString.isEmpty() || selectedString.get().isBlank())
			return new ArrayList<T>();
		List<T> selectedObjects = new ArrayList<T>();
		for (String id : selectedString.get().split(","))
			selectedObjects.add(repository.findById(id).get());
		return selectedObjects;
	}

	private <T extends Comparable<? super T>, U extends JpaRepository<T, ?>> Map<T, Boolean> getSelectedObjectsMap(
			List<T> selectedObjects, U repository) {
		Map<T, Boolean> result = new TreeMap<T, Boolean>();
		if (selectedObjects.isEmpty()) {
			for (T object : repository.findAll())
				result.put(object, false);
			return result;
		}
		for (T object : repository.findAll())
			result.put(object, selectedObjects.contains(object));
		return result;
	}

}