package com.project.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonObject;

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
import org.springframework.web.bind.annotation.RestController;
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

	@RestController
	public static class GalleryRestController {

		@Autowired
		private AccountService accountService;

		@GetMapping("gallery/buy")
		public String test(@RequestParam(required = true) String id) {
			accountService.addCard(id);
			JsonObject json = Json.createObjectBuilder().add("coins", getFormattedCoins(accountService.getCoins()))
					.add("quantity", accountService.countUserCardsByCardId(id)).build();
			return json.toString();
		}

		private String getFormattedCoins(int coins) {
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
			DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
			symbols.setGroupingSeparator(' ');
			formatter.setDecimalFormatSymbols(symbols);
			return formatter.format(coins);
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
		model.addAttribute("link", "/gallery");
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages",
				cardRepository.getNumberOfPages(selectedRarities, selectedSets, selectedTypes, search));
		addSelectedAttributes(model, rarities, sets, types, search, currentPage, selectedRarities, selectedSets,
				selectedTypes);
		return "gallery";
	}

	private void addSelectedAttributes(Model model, Optional<String> rarities, Optional<String> sets,
			Optional<String> types, Optional<String> search, int currentPage, List<Rarity> selectedRarities,
			List<Set> selectedSets, List<Type> selectedTypes) {
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("rarities", getSelectedObjectsMap(selectedRarities, rarityRepository));
		model.addAttribute("selectedRarities", rarities.orElse(""));
		model.addAttribute("sets", getSelectedObjectsMap(selectedSets, setRepository));
		model.addAttribute("selectedSets", sets.orElse(""));
		model.addAttribute("selectedTypes", types.orElse(""));
		model.addAttribute("types", getSelectedObjectsMap(selectedTypes, typeRepository));
		model.addAttribute("enteredSearch", search.orElse(""));
		model.addAttribute("sortOptions", SortType.values());
		model.addAttribute("sessionData", sessionData);
	}

	@PostMapping(value = "/sort")
	public ModelAndView sortSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "sort") String selectedSort) {
		if (selectedSort != null)
			try {
				SortType sortType = SortType.valueOf(selectedSort);
				sessionData.setSortType(sortType);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		return redirectToGallery(new ModelMap(), page, rarities, sets, types, search);
	}

	@PostMapping(value = "/order")
	public ModelAndView orderSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "order") String selectedOrder) {
		if (selectedOrder != null) {
			SortType.OrderType orderType = sessionData.getSortType().getOrderType(selectedOrder);
			if (orderType != null)
				sessionData.setOrderType(orderType);
		}
		return redirectToGallery(new ModelMap(), page, rarities, sets, types, search);
	}

	@PostMapping("/gallery")
	public ModelAndView searchSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@ModelAttribute(value = "search") String search) {
		Optional<String> searchResult = search == null || search.isEmpty() || search.isBlank() ? Optional.empty()
				: Optional.of(search);
		return redirectToGallery(new ModelMap(), page, rarities, sets, types, searchResult);
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
		for (String id : selectedString.get().split(",")) {
			Optional<T> data = repository.findById(id);
			if (data.isPresent())
				selectedObjects.add(data.get());
		}
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