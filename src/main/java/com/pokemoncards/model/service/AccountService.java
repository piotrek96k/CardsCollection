package com.pokemoncards.model.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pokemoncards.config.security.ExtendedUser;
import com.pokemoncards.config.security.SecurityUserDetails;
import com.pokemoncards.exception.IllegalPurchaseException;
import com.pokemoncards.exception.NotAuthenticatedException;
import com.pokemoncards.exception.NotFoundException;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Cash;
import com.pokemoncards.model.entity.FreeCard;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.account.AccountRepository;
import com.pokemoncards.model.repository.account.CashRepository;
import com.pokemoncards.model.repository.account.FreeCardRepository;
import com.pokemoncards.model.repository.card.CardRepository;
import com.pokemoncards.model.repository.card.RarityRepository;
import com.pokemoncards.model.repository.card.SetRepository;
import com.pokemoncards.model.repository.card.TypeRepository;
import com.pokemoncards.model.session.SortType;
import com.pokemoncards.model.session.SortType.OrderType;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private CashRepository cashRepository;

	@Autowired
	private FreeCardRepository freeCardRepository;

	@Autowired
	private RarityRepository rarityRepository;

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private TypeRepository typeRepository;
	
	@Autowired
	private SecurityUserDetails userDetails;

	@Autowired
	private ObjectMapper mapper;

	private ExtendedUser getUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal instanceof ExtendedUser))
			throw new NotAuthenticatedException();
		return (ExtendedUser) principal;
	}

	public List<Card> getUserCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search) {
		return accountRepository.getCards(getUser().getUsername(), page, sortType, orderType, rarities, sets, types,
				search);
	}

	public int getUserNumberOfCards(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		return accountRepository.getNumberOfCards(getUser().getUsername(), rarities, sets, types, search);
	}

	public int getUserCardsSellValue(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		return accountRepository.getCardsValues(getUser().getUsername(), rarities, sets, types, search) / 2;
	}

	public Cash getCash() {
		ExtendedUser user = getUser();
		Cash cash = cashRepository.getCash(user.getUsername());
		LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
		Duration duration = Duration.between(cash.getNextCoinsCollecting().plusDays(1), dateTime);
		if (!duration.isNegative()) {
			cash.setDaysInRow(1);
			cashRepository.updateDaysInRow(user.getUsername(), 1);
		}
		return cash;
	}

	public String getCashAsJson() {
		return getCash().convertToJson();
	}

	public String collectCoins() {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			Cash cash = cashRepository.getCash(user.getUsername());
			LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
			Duration duration = Duration.between(cash.getNextCoinsCollecting(), dateTime);
			if (!duration.isNegative()) {
				if (!duration.minusDays(1).isNegative())
					cash.setDaysInRow(1);
				cash.setCoins(Cash.COINS_PER_DAY * cash.getDaysInRow() + cash.getCoins());
				if (cash.getDaysInRow() != 10)
					cash.setDaysInRow(cash.getDaysInRow() + 1);
				cash.setNextCoinsCollecting(dateTime.plusDays(1));
				cashRepository.updateCoinsCollect(user.getUsername(), cash.getCoins(), cash.getNextCoinsCollecting(),
						cash.getDaysInRow());
			}
			return cash.convertToJson();
		}
	}

	public int countUserCardsByCardId(String id) {
		return accountRepository.countUserCardsByCardId(getUser().getUsername(), id);
	}

	public String getFreeCardAsJson() {
		return freeCardRepository.findByUsername(getUser().getUsername()).convertToJson();
	}

	public Card collectFreeCard() {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			FreeCard freeCard = freeCardRepository.findByUsername(user.getUsername());
			LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
			Duration duration = Duration.between(freeCard.getNextFreeCard(), dateTime);
			if (duration.isNegative())
				return new Card();
			Card card = cardRepository.getRandomCard();
			accountRepository.addCard(user.getUsername(), user.getEmail(), card.getId());
			freeCardRepository.updateNextFreeCard(user.getUsername(), dateTime.plusDays(1));
			card.setQuantity(accountRepository.countUserCardsByCardId(user.getUsername(), card.getId()));
			return card;
		}
	}

	public String addCard(String id) {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			Optional<Card> card = cardRepository.findById(id);
			int coins = cashRepository.getCoins(user.getUsername());
			if (card.isEmpty())
				throw new NotFoundException();
			int cost = card.get().getRarity().getValue();
			int quantity = accountRepository.countUserCardsByCardId(user.getUsername(), id);
			if (coins < cost)
				throw new IllegalPurchaseException();
			accountRepository.addCard(user.getUsername(), user.getEmail(), id);
			cashRepository.updateCoins(user.getUsername(), coins - cost);
			return getCoinsJson(coins - cost, ++quantity, card.get(), user.getUsername(), Optional.empty());
		}
	}

	public void addCards(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			int cost = cardRepository.getCardsValue(rarities, sets, types, search);
			int coins = cashRepository.getCoins(user.getUsername());
			if (cost > coins)
				throw new IllegalPurchaseException();
			accountRepository.addCards(user.getUsername(), user.getEmail(), rarities, sets, types, search);
			cashRepository.updateCoins(user.getUsername(), coins - cost);
		}
	}

	public String removeCard(String id, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			int coins = cashRepository.getCoins(user.getUsername());
			Optional<Card> card = cardRepository.findById(id);
			if (card.isEmpty())
				throw new NotFoundException();
			int quantity = accountRepository.countUserCardsByCardId(user.getUsername(), id);
			if (quantity <= 0)
				throw new IllegalPurchaseException();
			int cost = cardRepository.getCardSellCost(id);
			accountRepository.removeCard(user.getUsername(), id);
			cashRepository.updateCoins(user.getUsername(), coins + cost);
			return getCoinsJson(coins + cost, --quantity, card.get(), user.getUsername(),
					Optional.of(getUserCardsSellValue(rarities, sets, types, search)));
		}
	}

	public void removeCards(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		ExtendedUser user = getUser();
		synchronized (userDetails.getUsersLockers().get(user)) {
			int coins = cashRepository.getCoins(user.getUsername());
			int value = getUserCardsSellValue(rarities, sets, types, search);
			accountRepository.removeCards(user.getUsername(), rarities, sets, types, search);
			cashRepository.updateCoins(user.getUsername(), coins + value);
		}
	}

	private String getCoinsJson(int coins, int quantity, Card card, String username, Optional<Integer> totalValue) {
		ObjectNode node = mapper.createObjectNode();
		node.put("coins", NumbersService.formatInteger(coins));
		node.put("quantity", NumbersService.formatInteger(quantity));
		node.putPOJO("rarity", rarityRepository.findById(card.getRarity().getId(), Optional.of(username)).get());
		node.putPOJO("set", setRepository.findById(card.getSet().getId(), Optional.of(username)).get());
		ArrayNode array = mapper.createArrayNode();
		for (Type type : card.getTypes())
			array.addPOJO(typeRepository.findById(type.getId(), Optional.of(username)).get());
		node.putArray("types").addAll(array);
		if (totalValue.isPresent())
			node.put("totalValue", NumbersService.formatInteger(totalValue.get()));
		return node.toString();
	}

}