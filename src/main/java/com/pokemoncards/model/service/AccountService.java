package com.pokemoncards.model.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pokemoncards.model.component.SortType;
import com.pokemoncards.model.component.SortType.OrderType;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Cash;
import com.pokemoncards.model.entity.FreeCard;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Role;
import com.pokemoncards.model.entity.RoleEnum;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.CashRepository;
import com.pokemoncards.model.repository.FreeCardRepository;
import com.pokemoncards.model.repository.RarityRepository;
import com.pokemoncards.model.repository.RoleRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.repository.TypeRepository;

@Service
public class AccountService extends AbstractService{

	private static DecimalFormat formatter;

	private static BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

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
	private ObjectMapper mapper;

	public void addAccount(Account account) {
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(getPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(true);
		accountRepository.save(account);
		setCash(account);
		setFreeCard(account);
	}

	private void setCash(Account account) {
		Cash cash = new Cash();
		cash.setCoins(3_000);
		cash.setAccount(account);
		cash.setUsername(account.getUsername());
		cash.setEmail(account.getEmail());
		cash.setNextCoinsCollecting(LocalDateTime.now(ZoneOffset.UTC));
		cash.setDaysInRow(1);
		cashRepository.save(cash);
	}

	private void setFreeCard(Account account) {
		FreeCard freeCard = new FreeCard();
		freeCard.setAccount(account);
		freeCard.setUsername(account.getUsername());
		freeCard.setEmail(account.getEmail());
		freeCard.setNextFreeCard(LocalDateTime.now(ZoneOffset.UTC));
		freeCardRepository.save(freeCard);
	}

	public AccountId getAccountId() {
		return operateOnAccount(accountId -> accountId, () -> new AccountId());
	}

	public List<Card> getUserCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search) {
		Function<AccountId, List<Card>> function = accountId -> accountRepository.getCards(accountId.getUsername(),
				page, sortType, orderType, rarities, sets, types, search);
		return operateOnAccount(function, () -> new ArrayList<Card>());
	}

	public int getUserCardsNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		return operateOnAccount(
				accountId -> accountRepository.getNumberOfPages(accountId.getUsername(), rarities, sets, types, search),
				() -> 1);
	}

	public Cash getCash() {
		Function<AccountId, Cash> function = accountId -> {
			Cash cash = cashRepository.getCash(accountId.getUsername());
			LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
			Duration duration = Duration.between(cash.getNextCoinsCollecting().plusDays(1), dateTime);
			if (!duration.isNegative()) {
				cash.setDaysInRow(1);
				cashRepository.updateDaysInRow(accountId.getUsername(), 1);
			}
			return cash;
		};
		return operateOnAccount(function, () -> new Cash());
	}

	public String getCashAsJson() {
		return getCash().convertToJson();
	}

	public String collectCoins() {
		Function<AccountId, String> function = accountId -> {
			Cash cash = cashRepository.getCash(accountId.getUsername());
			LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
			Duration duration = Duration.between(cash.getNextCoinsCollecting(), dateTime);
			if (!duration.isNegative()) {
				if (!duration.minusDays(1).isNegative())
					cash.setDaysInRow(1);
				cash.setCoins(Cash.COINS_PER_DAY * cash.getDaysInRow() + cash.getCoins());
				if (cash.getDaysInRow() != 10)
					cash.setDaysInRow(cash.getDaysInRow() + 1);
				cash.setNextCoinsCollecting(dateTime.plusDays(1));
				cashRepository.updateCoinsCollect(accountId.getUsername(), cash.getCoins(),
						cash.getNextCoinsCollecting(), cash.getDaysInRow());
			}
			return cash.convertToJson();
		};
		return operateOnAccount(function, () -> new Cash().convertToJson());
	}

	public int countUserCardsByCardId(String id) {
		return operateOnAccount(accountId -> accountRepository.countUserCardsByCardId(accountId.getUsername(), id),
				() -> 0);
	}

	public String getFreeCardAsJson() {
		return operateOnAccount(accountId -> freeCardRepository.findByUsername(accountId.getUsername()).convertToJson(),
				() -> new FreeCard().convertToJson());
	}

	public Card collectFreeCard() {
		Function<AccountId, Card> function = accountId -> {
			FreeCard freeCard = freeCardRepository.findByUsername(accountId.getUsername());
			LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
			Duration duration = Duration.between(freeCard.getNextFreeCard(), dateTime);
			if (duration.isNegative())
				return new Card();
			Card card = cardRepository.getRandomCard();
			accountRepository.addCard(accountId.getUsername(), accountId.getEmail(), card.getId());
			freeCardRepository.updateNextFreeCard(accountId.getUsername(), dateTime.plusDays(1));
			card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), card.getId()));
			return card;
		};
		return operateOnAccount(function, () -> new Card());
	}

	public String addCard(String id) {
		Function<AccountId, String> function = accountId -> {
			Optional<Card> card = cardRepository.findById(id);
			int coins = cashRepository.getCash(accountId.getUsername()).getCoins();
			if (card.isEmpty())
				return getCoinsJson(coins, 0);
			int cost = card.get().getRarity().getValue();
			int quantity = accountRepository.countUserCardsByCardId(accountId.getUsername(), id);
			if (coins >= cost) {
				accountRepository.addCard(accountId.getUsername(), accountId.getEmail(), id);
				cashRepository.updateCoins(accountId.getUsername(), coins - cost);
				return getCoinsJson(coins - cost, ++quantity, card.get(), accountId.getUsername());
			}
			return getCoinsJson(coins, quantity, card.get(), accountId.getUsername());
		};
		return operateOnAccount(function, () -> getCoinsJson(0, 0));
	}

	public String removeCard(String id) {
		Function<AccountId, String> function = accountId -> {
			int coins = cashRepository.getCash(accountId.getUsername()).getCoins();
			Optional<Card> card = cardRepository.findById(id);
			if(card.isEmpty())
				return getCoinsJson(coins, 0);
			int quantity = accountRepository.countUserCardsByCardId(accountId.getUsername(), id);
			if (quantity > 0) {
				int cost = cardRepository.getCardSellCost(id);
				accountRepository.removeCard(accountId.getUsername(), id);
				cashRepository.updateCoins(accountId.getUsername(), coins + cost);
				return getCoinsJson(coins + cost, --quantity, card.get(), accountId.getUsername());
			}
			return getCoinsJson(coins, quantity, card.get(),accountId.getUsername());
		};
		return operateOnAccount(function, () -> getCoinsJson(0, 0));
	}
	
	private String getCoinsJson(int coins, int quantity, Card card, String username) {
		ObjectNode node = getCoinsObjectNode(coins, quantity);
		node.putPOJO("rarity", rarityRepository.findById(card.getRarity().getId(), Optional.of(username)).get());
		node.putPOJO("set",setRepository.findById(card.getSet().getId(), Optional.of(username)).get());
		ArrayNode array = mapper.createArrayNode();
		for(Type type: card.getTypes())
			array.addPOJO(typeRepository.findById(type.getId(), Optional.of(username)).get());
		node.putArray("types").addAll(array);
		return node.toString();
	}

	private String getCoinsJson(int coins, int quantity) {
		return getCoinsObjectNode(coins, quantity).toString();
	}
	
	private ObjectNode getCoinsObjectNode(int coins, int quantity) {
		ObjectNode node = mapper.createObjectNode();
		node.put("coins", formatInteger(coins));
		node.put("quantity", formatInteger(quantity));
		return node;
	}

	public static String formatInteger(int value) {
		if (formatter == null)
			formatter = getDecimalFormatter();
		return formatter.format(value);
	}

	private static DecimalFormat getDecimalFormatter() {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter;
	}

	private static BCryptPasswordEncoder getPasswordEncoder() {
		if (passwordEncoder == null)
			passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder;
	}

}