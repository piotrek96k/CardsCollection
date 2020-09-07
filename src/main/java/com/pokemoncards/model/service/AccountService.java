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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pokemoncards.config.ExtendedUser;
import com.pokemoncards.exception.NotAuthenticatedException;
import com.pokemoncards.exception.NotFoundException;
import com.pokemoncards.model.component.SortType;
import com.pokemoncards.model.component.SortType.OrderType;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Cash;
import com.pokemoncards.model.entity.FreeCard;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Role;
import com.pokemoncards.model.entity.RoleEnum;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.AccountRepository;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.CashRepository;
import com.pokemoncards.model.repository.FreeCardRepository;
import com.pokemoncards.model.repository.RarityRepository;
import com.pokemoncards.model.repository.RoleRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.repository.TypeRepository;

@Service
public class AccountService {

	private static DecimalFormat formatter;

	private static BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	protected AccountRepository accountRepository;

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

	public int getUserCardsNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		return accountRepository.getNumberOfPages(getUser().getUsername(), rarities, sets, types, search);
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

	public int countUserCardsByCardId(String id) {
		return accountRepository.countUserCardsByCardId(getUser().getUsername(), id);
	}

	public String getFreeCardAsJson() {
		return freeCardRepository.findByUsername(getUser().getUsername()).convertToJson();
	}

	public Card collectFreeCard() {
		ExtendedUser user = getUser();
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

	public String addCard(String id) {
		ExtendedUser user = getUser();
			Optional<Card> card = cardRepository.findById(id);
			int coins = cashRepository.getCash(user.getUsername()).getCoins();
			if (card.isEmpty())
				throw new NotFoundException();
			int cost = card.get().getRarity().getValue();
			int quantity = accountRepository.countUserCardsByCardId(user.getUsername(), id);
			if (coins >= cost) {
				accountRepository.addCard(user.getUsername(), user.getEmail(), id);
				cashRepository.updateCoins(user.getUsername(), coins - cost);
				return getCoinsJson(coins - cost, ++quantity, card.get(), user.getUsername());
			}
			return getCoinsJson(coins, quantity, card.get(), user.getUsername());
	}

	public String removeCard(String id) {
		ExtendedUser user = getUser();
			int coins = cashRepository.getCash(user.getUsername()).getCoins();
			Optional<Card> card = cardRepository.findById(id);
			if (card.isEmpty())
				throw new NotFoundException();
			int quantity = accountRepository.countUserCardsByCardId(user.getUsername(), id);
			if (quantity > 0) {
				int cost = cardRepository.getCardSellCost(id);
				accountRepository.removeCard(user.getUsername(), id);
				cashRepository.updateCoins(user.getUsername(), coins + cost);
				return getCoinsJson(coins + cost, --quantity, card.get(), user.getUsername());
			}
			return getCoinsJson(coins, quantity, card.get(), user.getUsername());
	}

	private String getCoinsJson(int coins, int quantity, Card card, String username) {
		ObjectNode node = getCoinsObjectNode(coins, quantity);
		node.putPOJO("rarity", rarityRepository.findById(card.getRarity().getId(), Optional.of(username)).get());
		node.putPOJO("set", setRepository.findById(card.getSet().getId(), Optional.of(username)).get());
		ArrayNode array = mapper.createArrayNode();
		for (Type type : card.getTypes())
			array.addPOJO(typeRepository.findById(type.getId(), Optional.of(username)).get());
		node.putArray("types").addAll(array);
		return node.toString();
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