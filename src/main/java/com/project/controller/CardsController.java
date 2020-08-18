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
import org.springframework.web.servlet.ModelAndView;

import com.project.model.component.SessionData;
import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.repository.RarityRepository;
import com.project.model.repository.SetRepository;
import com.project.model.repository.TypeRepository;
import com.project.model.service.AccountService;
import com.project.model.service.SortType;

@Controller
@Scope(value = "session")
public abstract class CardsController {

	@Autowired
	protected RarityRepository rarityRepository;

	@Autowired
	protected SetRepository setRepository;

	@Autowired
	protected TypeRepository typeRepository;

	@Autowired
	protected AccountService accountService;

	@Autowired
	protected SessionData sessionData;

	abstract protected String getLink();

	abstract protected List<Card> getCards(int page, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);
	
	abstract protected int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

	protected void addSelectedAttributes(Model model, Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types, Optional<String> search) {
		int currentPage = page.orElse(1);
		List<Rarity> selectedRarities = getSelectedObjectsAsList(rarities, rarityRepository);
		List<Set> selectedSets = getSelectedObjectsAsList(sets, setRepository);
		List<Type> selectedTypes = getSelectedObjectsAsList(types, typeRepository);
		model.addAttribute("cards", getCards(currentPage, selectedRarities, selectedSets, selectedTypes, search));
		model.addAttribute("link", getLink());
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages", getNumberOfPages(selectedRarities, selectedSets, selectedTypes, search));
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
		handleVerticalMenu();
	}
	
	protected ModelAndView searchSelection(Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types,
			String search) {
		Optional<String> searchResult = search == null || search.isEmpty() || search.isBlank() ? Optional.empty()
				: Optional.of(search);
		return redirectToCardsPage(new ModelMap(), page, rarities, sets, types, searchResult);
	}

	private void handleVerticalMenu() {
		String page = getLink();
		if (sessionData.getLastVisited() != page)
			sessionData.resetExpanders();
		sessionData.setLastVisited(page);
	}

	protected ModelAndView sortSelection(Optional<Integer> page, Optional<String> rarities, Optional<String> sets,
			Optional<String> types, Optional<String> search, String selectedSort) {
		if (selectedSort != null)
			try {
				SortType sortType = SortType.valueOf(selectedSort);
				sessionData.setSortType(sortType);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		return redirectToCardsPage(new ModelMap(), page, rarities, sets, types, search);
	}

	protected ModelAndView orderSelection(Optional<Integer> page, Optional<String> rarities, Optional<String> sets,
			Optional<String> types, Optional<String> search, String selectedOrder) {
		if (selectedOrder != null) {
			SortType.OrderType orderType = sessionData.getSortType().getOrderType(selectedOrder);
			if (orderType != null)
				sessionData.setOrderType(orderType);
		}
		return redirectToCardsPage(new ModelMap(), page, rarities, sets, types, search);
	}

	protected ModelAndView redirectToCardsPage(ModelMap model, Optional<Integer> page, Optional<String> rarities,
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
		return new ModelAndView("redirect:/", model);
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