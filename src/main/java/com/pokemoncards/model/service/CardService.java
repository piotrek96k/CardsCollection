package com.pokemoncards.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.RarityRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.repository.TypeRepository;
import com.pokemoncards.model.service.SortType.OrderType;

@Service
public class CardService extends AbstractService{

	private static final String LEGEND_RARITY_ID;

	static {
		LEGEND_RARITY_ID = "LEGEND";
	}

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private RarityRepository rarityRepository;
	
	@Autowired
	private SetRepository setRepository;
	
	@Autowired
	private TypeRepository typeRepository;
	
	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search) {
		Function<AccountId, List<Card>> function = accountId -> cardRepository.getCards(page, sortType, orderType,
				rarities, sets, types, search, Optional.of(accountId.getUsername()));
		return operateOnAccount(function, () -> cardRepository.getCards(page, sortType, orderType, rarities, sets,
				types, search, Optional.empty()));
	}

	public List<Card> getCards(Optional<String[]> ids, Optional<String> search) {
		Optional<String> username = getUsername();
		if (ids.isEmpty())
			return getCardsWithEmptyIds(search, username);
		Function<String, Card> cardGetter;
		if (search.isEmpty() || cardRepository.countCardsBySearch(search.get()) == 0)
			cardGetter = id -> cardRepository.getNextCard(rarityRepository.findById(LEGEND_RARITY_ID).get(), id, username);
		else
			cardGetter = id -> cardRepository.getNextCard(search.get(), id, username);
		return getCards(cardGetter, ids.get(), username);
	}

	private List<Card> getCardsWithEmptyIds(Optional<String> search, Optional<String> username) {
		int cardsQuantity;
		Function<Integer, Card> cardGetter;
		if (search.isEmpty()) {
			cardsQuantity = cardRepository.countCardsByRarity(LEGEND_RARITY_ID);
			cardGetter = number -> cardRepository.getCardByRowNumber(number,
					rarityRepository.findById(LEGEND_RARITY_ID).get(), username);
		} else {
			cardsQuantity = cardRepository.countCardsBySearch(search.get());
			if (cardsQuantity == 0)
				return getCardsWithEmptyIds(Optional.empty(), username);
			cardGetter = number -> cardRepository.getCardByRowNumber(number, search.get(), username);
		}
		return getCards(cardGetter, cardsQuantity);
	}

	private List<Card> getCards(Function<Integer, Card> cardGetter, int cardsQuantity) {
		List<Card> cards = new ArrayList<Card>(5);
		int number = new Random().nextInt(cardsQuantity);
		for (int i = 0; i < 5; i++) {
			if (number == cardsQuantity)
				number = 0;
			cards.add(cardGetter.apply(number++));
		}
		return cards;
	}

	private List<Card> getCards(Function<String, Card> cardGetter, String[] ids, Optional<String> username) {
		List<Card> cards = new ArrayList<Card>();
		for (int i = 1; i < ids.length; i++) {
			Optional<Card> card = cardRepository.findById(ids[i], username);
			if(card.isPresent())
				cards.add(card.get());
			else
				cards.add(cardRepository.getRandomCard());
		}
		cards.add(cardGetter.apply(ids[ids.length - 1]));
		return cards;
	}
	
	public List<Rarity> getAllRarities(){
		return rarityRepository.findAll(getUsername());
	}
	
	public List<Set> getAllSets(){
		return setRepository.findAll(getUsername());
	}
	
	public List<Type> getAllTypes(){
		return typeRepository.findAll(getUsername());
	}
	
	private Optional<String> getUsername(){
		return operateOnAccount(accountId->Optional.of(accountId.getUsername()), ()->Optional.empty());
	}

}