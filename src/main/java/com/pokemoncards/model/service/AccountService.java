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

import javax.json.Json;
import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.pokemoncards.model.repository.RoleRepository;
import com.pokemoncards.model.service.SortType.OrderType;

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
			int cost = card.get().getRarity().getCost();
			int quantity = accountRepository.countUserCardsByCardId(accountId.getUsername(), id);
			if (coins >= cost) {
				accountRepository.addCard(accountId.getUsername(), accountId.getEmail(), id);
				cashRepository.updateCoins(accountId.getUsername(), coins - cost);
				return getCoinsJson(coins - cost, ++quantity);
			}
			return getCoinsJson(coins, quantity);
		};
		return operateOnAccount(function, () -> getCoinsJson(0, 0));
	}

	public String removeCard(String id) {
		Function<AccountId, String> function = accountId -> {
			int coins = cashRepository.getCash(accountId.getUsername()).getCoins();
			int quantity = accountRepository.countUserCardsByCardId(accountId.getUsername(), id);
			if (quantity > 0) {
				int cost = cardRepository.getCardSellCost(id);
				accountRepository.removeCard(accountId.getUsername(), id);
				cashRepository.updateCoins(accountId.getUsername(), coins + cost);
				return getCoinsJson(coins + cost, --quantity);
			}
			return getCoinsJson(coins, quantity);
		};
		return operateOnAccount(function, () -> getCoinsJson(0, 0));
	}

	private String getCoinsJson(int coins, int quantity) {
		JsonObject json = Json.createObjectBuilder().add("coins", formatInteger(coins))
				.add("quantity", formatInteger(quantity)).build();
		return json.toString();
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