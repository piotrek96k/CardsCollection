package com.project.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.project.model.entity.QuantityCard;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RoleRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private PasswordChecker passwordChecker;

	public Optional<RegistrationError> addAccount(Account account, String pswRepeat) {
		if (account.getUsername().length() < 4)
			return Optional.of(RegistrationError.USERNAME_TOO_SHORT);
		if (account.getPassword().length() < 8)
			return Optional.of(RegistrationError.PASSWORD_TOO_SHORT);
		if (!account.getPassword().equals(pswRepeat))
			return Optional.of(RegistrationError.DIFFERENT_PASSWORDS);
		if (!passwordChecker.validatePassword(pswRepeat))
			return Optional.of(RegistrationError.WRONG_PASSWORD);
		if (accountRepository.findByUsername(account.getUsername()) != null)
			return Optional.of(RegistrationError.USERNAME_EXISTS);
		if (accountRepository.findByEmail(account.getEmail()) != null)
			return Optional.of(RegistrationError.EMAIL_EXISTS);
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(true);
		account.setCoins(1000);
		accountRepository.save(account);
		return Optional.empty();
	}

	public List<QuantityCard> getUserCards(int page) {
		Function<AccountId, List<QuantityCard>> function = accountId -> accountRepository
				.getAccountCardsListByPage(accountId.getUsername(), page);
		return operateOnAccount(function, () -> new ArrayList<QuantityCard>());
	}

	public int getUserCardsNumberOfPages() {
		return operateOnAccount(accountId -> accountRepository.getNumberOfPages(accountId.getUsername()), () -> 1);
	}

	public List<QuantityCard> getGalleryCards(int page) {
		Function<AccountId, List<QuantityCard>> function = accountId -> {
			List<QuantityCard> quantityCards = new ArrayList<QuantityCard>();
			List<Card> cards = cardRepository.getCardsByPageOrderByName(page);
			for (Card card : cards)
				quantityCards.add(new QuantityCard(card,
						accountRepository.countUserCardsByCardId(accountId.getUsername(), card.getId())));
			return quantityCards;
		};
		return operateOnAccount(function, () -> new ArrayList<QuantityCard>());
	}

	public int getCoins() {
		return operateOnAccount(accountId -> accountRepository.getCoins(accountId.getUsername()), () -> 0);
	}

	public QuantityCard getQuantityCard(String id) {
		Function<AccountId, QuantityCard> function = accountId -> {
			QuantityCard card = new QuantityCard(cardRepository.getOne(id));
			card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), id));
			return card;
		};
		return operateOnAccount(function, () -> new QuantityCard(cardRepository.getOne(id)));
	}

	public boolean addCard(String id) {
		Function<AccountId, Boolean> function = accountId -> {
			int cost = cardRepository.getCost(id);
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

	private <T> T operateOnAccount(Function<AccountId, T> function, Supplier<T> supplier) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return function.apply(accountId);
		}
		return supplier.get();
	}

}