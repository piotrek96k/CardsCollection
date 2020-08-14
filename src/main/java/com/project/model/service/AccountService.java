package com.project.model.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;
import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RoleRepository;
import com.project.model.service.SortType.OrderType;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CardRepository cardRepository;

	public void addAccount(Account account) {
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(true);
		account.setCoins(1000);
		accountRepository.save(account);
	}

	public List<Card> getUserCards(int page) {
		Function<AccountId, List<Card>> function = accountId -> {
			List<Card> cards = accountRepository.getAccountCardsListByPage(accountId.getUsername(), page);
			Set<Rarity> rarities = new HashSet<Rarity>();
			cards.forEach(card -> rarities.add(card.getRarity()));
			rarities.forEach(rarity->rarity.setCost(rarity.getCost()/2));
			return cards;
		};
		return operateOnAccount(function, () -> new ArrayList<Card>());
	}

	public int getUserCardsNumberOfPages() {
		return operateOnAccount(accountId -> accountRepository.getNumberOfPages(accountId.getUsername()), () -> 1);
	}

	public List<Card> getGalleryCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<com.project.model.entity.Set> sets,
			Optional<String> search) {
		Function<AccountId, List<Card>> function = accountId -> {
			List<Card> cards = cardRepository.getCards(page, sortType, orderType, rarities,sets, search);
			for (Card card : cards)
				card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), card.getId()));
			return cards;
		};
		return operateOnAccount(function, () -> cardRepository.getCards(page, sortType, orderType, rarities,sets, search));
	}

	public int getCoins() {
		return operateOnAccount(accountId -> accountRepository.getCoins(accountId.getUsername()), () -> 0);
	}
	
	public int countUserCardsByCardId(String id) {
		return operateOnAccount(accountId->accountRepository.countUserCardsByCardId(accountId.getUsername(), id), ()->0);
	}

	public Card getCard(String id) {
		Function<AccountId, Card> function = accountId -> {
			Card card = cardRepository.getOne(id);
			card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), id));
			return card;
		};
		return operateOnAccount(function, () -> cardRepository.getOne(id));
	}

	public Card getCardToSell(String id) {
		Card card = getCard(id);
		card.getRarity().setCost(card.getRarity().getCost() / 2);
		return card;
	}

	public boolean addCard(String id) {
		Function<AccountId, Boolean> function = accountId -> {
			int cost = cardRepository.getCardCost(id);
			int coins = accountRepository.getCoins(accountId.getUsername());
			if (coins >= cost) {
				accountRepository.addCard(accountId.getUsername(), accountId.getEmail(), id);
				accountRepository.updateUserCoins(accountId.getUsername(), coins - cost);
				return true;
			}
			return false;
		};
		return operateOnAccount(function, () -> false);
	}

	public boolean removeCard(String id) {
		Function<AccountId, Boolean> function = accountId -> {
			int cost = cardRepository.getCardCost(id) / 2;
			int coins = accountRepository.getCoins(accountId.getUsername());
			accountRepository.removeCard(accountId.getUsername(), id);
			accountRepository.updateUserCoins(accountId.getUsername(), coins + cost);
			return true;
		};
		return operateOnAccount(function, () -> false);
	}

	public <T> T operateOnAccount(Function<AccountId, T> function, Supplier<T> supplier) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return function.apply(accountId);
		}
		return supplier.get();
	}

}