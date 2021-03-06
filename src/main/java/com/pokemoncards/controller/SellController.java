package com.pokemoncards.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.card.RarityRepository;
import com.pokemoncards.model.repository.card.SetRepository;
import com.pokemoncards.model.repository.card.TypeRepository;
import com.pokemoncards.model.service.AccountService;

@Controller
public class SellController extends CardsController {
	
	@RestController
	public static class SellRestController {

		@Autowired
		private RarityRepository rarityRepository;
		
		@Autowired
		private SetRepository setRepository;
		
		@Autowired
		private TypeRepository typeRepository;
		
		@Autowired
		private AccountService accountService;

		@PostMapping(value = "/sell/one/{id}")
		public String sold(@PathVariable(value = "id", required = true) String id, @RequestParam(value = "page") Optional<Integer> page,
				@RequestParam(value = "rarity") Optional<String> rarities,
				@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
				@RequestParam(value = "search") Optional<String> search) {
			List<Rarity> selectedRarities = getSelectedObjectsAsList(rarities, rarityRepository);
			List<Set> selectedSets = getSelectedObjectsAsList(sets, setRepository);
			List<Type> selectedTypes = getSelectedObjectsAsList(types, typeRepository);
			return accountService.removeCard(id, selectedRarities, selectedSets, selectedTypes, search);
		}

	}

	@GetMapping(value = "/sell")
	public String sellPage(Model model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search) {
		addSelectedAttributes(model, page, rarities, sets, types, search);
		return "sell";
	}

	@Override
	@PostMapping(value = "/sell")
	public ModelAndView searchSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@ModelAttribute(value = "search") String search) {
		return super.searchSelection(page, rarities, sets, types, search);
	}
	
	@PostMapping(value = "/sell/all")
	public ModelAndView  sellAll(ModelMap model,@RequestParam(value = "page") Optional<Integer> page,
	@RequestParam(value = "rarity") Optional<String> rarities,
	@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
	@RequestParam(value = "search") Optional<String> search) {
		List<Rarity> selectedRarities = getSelectedObjectsAsList(rarities, rarityRepository);
		List<Set> selectedSets = getSelectedObjectsAsList(sets, setRepository);
		List<Type> selectedTypes = getSelectedObjectsAsList(types, typeRepository);
		accountService.removeCards(selectedRarities, selectedSets, selectedTypes, search);
		return redirectToCardsPage(model, page, rarities, sets, types, search);
	}

	@Override
	@PostMapping(value = "/sell/sort")
	public ModelAndView sortSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "sort") String selectedSort) {
		return super.sortSelection(page, rarities, sets, types, search, selectedSort);
	}

	@Override
	@PostMapping(value = "/sell/order")
	public ModelAndView orderSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "order") String selectedOrder) {
		return super.orderSelection(page, rarities, sets, types, search, selectedOrder);
	}

	@Override
	@PostMapping(value = "/sell/selectedpage")
	public ModelAndView pageSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search, @ModelAttribute(value = "selectedPage")String selectedPage) {
		return super.pageSelection(page, rarities, sets, types, search, selectedPage);
	}
	
	@Override
	protected int getNumberOfCards(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		return accountService.getUserNumberOfCards(rarities, sets, types, search);
	}
	
	@Override
	protected Optional<Integer> getCardsValue(List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search){
		return Optional.of(accountService.getUserCardsSellValue(rarities, sets, types, search));
	}

	@Override
	protected String getLink() {
		return "/sell";
	}

	@Override
	protected List<Card> getCards(int page, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		return accountService.getUserCards(page, sessionData.getSortType(), sessionData.getOrderType(), rarities, sets,
				types, search);
	}

	@Override
	protected ModelAndView redirectToCardsPage(ModelMap model, Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types, Optional<String> search) {
		ModelAndView modelAndView = super.redirectToCardsPage(model, page, rarities, sets, types, search);
		modelAndView.setViewName("redirect:/sell");
		return modelAndView;
	}

}