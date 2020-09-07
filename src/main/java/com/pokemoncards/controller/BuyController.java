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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.AccountRepository;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.service.AccountService;

@Controller
@RequestMapping(value = "/buy")
public class BuyController extends CardsController {

	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	AccountRepository repository;
	
	@RestController
	public static class BuyRestController {

		@Autowired
		private AccountService accountService;

		@PostMapping(value = "/buy/{id}")
		public String bought(@PathVariable(value = "id", required = true) String id) {
			return accountService.addCard(id);
		}

	}

	@GetMapping
	public String buyPage(Model model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search) {
		addSelectedAttributes(model, page, rarities, sets, types, search);
		return "buy";
	}

	@Override
	@PostMapping
	public ModelAndView searchSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@ModelAttribute(value = "search") String search) {
		return super.searchSelection(page, rarities, sets, types, search);
	}

	@Override
	@PostMapping(value = "/sort")
	public ModelAndView sortSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "sort") String selectedSort) {
		return super.sortSelection(page, rarities, sets, types, search, selectedSort);
	}

	@Override
	@PostMapping(value = "/order")
	public ModelAndView orderSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "order") String selectedOrder) {
		return super.orderSelection(page, rarities, sets, types, search, selectedOrder);
	}

	@Override
	protected int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		return cardRepository.getNumberOfPages(rarities, sets, types, search);
	}

	@Override
	protected String getLink() {
		return "/buy";
	}

	@Override
	protected List<Card> getCards(int page, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		return cardService.getCards(page, sessionData.getSortType(), sessionData.getOrderType(), rarities, sets,
				types, search);
	}

	@Override
	protected ModelAndView redirectToCardsPage(ModelMap model, Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types, Optional<String> search) {
		ModelAndView modelAndView = super.redirectToCardsPage(model, page, rarities, sets, types, search);
		modelAndView.setViewName("redirect:/buy");
		return modelAndView;
	}

}