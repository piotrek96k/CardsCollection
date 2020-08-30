package com.pokemoncards.model.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.pokemoncards.model.api.Cards;
import com.pokemoncards.model.api.Sets;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Identifiable;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Role;
import com.pokemoncards.model.entity.RoleEnum;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.AccountRepository;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.CashRepository;
import com.pokemoncards.model.repository.RarityRepository;
import com.pokemoncards.model.repository.RoleRepository;
import com.pokemoncards.model.repository.SetRepository;
import com.pokemoncards.model.repository.TypeRepository;
import com.pokemoncards.model.service.AccountService;
import com.pokemoncards.model.service.ApiService;

@Component
public class DatabaseInitializer implements InitializingBean {

	private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RarityRepository rarityRepository;

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private CashRepository cashRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ApiService apiService;

	private void loadData() {
		if (apiService.getNumberOfCards() > cardRepository.count()) {
			LOGGER.log(Level.INFO, "Loading Data");
			ApiService.ApiData data = apiService.getApiData();
			Map<String, Integer> values = getDefaultValues();
			loadObjects(data.getRarities(), rarityRepository, rarity -> rarity, Rarity::new,
					Optional.of(rarity -> setRarityValue(rarity, values)));
			loadObjects(data.getSets(), setRepository, Sets.Set::getName, Set::new, Optional.empty());
			loadObjects(data.getTypes(), typeRepository, type -> type, Type::new, Optional.empty());
			loadCards(data.getCards());
		}
	}

	private void loadCards(List<Cards.Card> cards) {
		for (Cards.Card apiCard : cards)
			if (cardRepository.findById(apiCard.getId()).isEmpty()) {
				Card card = new Card();
				card.setId(apiCard.getId());
				card.setName(apiCard.getName());
				card.setImageUrl(apiCard.getImageUrl());
				card.setRarity(rarityRepository.findById(apiCard.getRarity()).get());
				card.setSet(setRepository.findById(apiCard.getSet()).get());
				card.setPokedexNumber(apiCard.getPokedexNumber());
				card.setEvolvesFrom(apiCard.getEvolvesFrom());
				card.setHp(apiCard.getHp() == null ? null : Integer.valueOf(apiCard.getHp()));
				List<Type> types = new ArrayList<Type>();
				apiCard.getTypes().forEach(type -> types.add(typeRepository.findById(type).get()));
				Collections.sort(types);
				card.setFirstType(types.isEmpty() ? null : types.get(0));
				card.setTypes(types);
				cardRepository.save(card);
			}
	}

	private <T, U, V extends Identifiable<U>> void loadObjects(Iterable<T> objects, JpaRepository<V, U> repository,
			Function<T, U> idExtractor, Supplier<V> constructor, Optional<Consumer<V>> setter) {
		for (T object : objects)
			if (repository.findById(idExtractor.apply(object)).isEmpty()) {
				V entity = constructor.get();
				entity.setId(idExtractor.apply(object));
				if (setter.isPresent())
					setter.get().accept(entity);
				repository.save(entity);
			}
	}

	private void setRarityValue(Rarity rarity, Map<String, Integer> costs) {
		int cost = costs.get(rarity.getId()) == null ? 25_000 : costs.get(rarity.getId());
		rarity.setValue(cost);
		rarity.setSellPrice(cost / 2);
	}

	private Map<String, Integer> getDefaultValues() {
		Map<String, Integer> values = new HashMap<String, Integer>();
		values.put("Common", 100);
		values.put("Uncommon", 150);
		values.put("Rare", 250);
		values.put("Rare Holo", 300);
		values.put("Rare Ultra", 500);
		values.put("Rare Holo EX", 750);
		values.put("Rare Secret", 1_000);
		values.put("Rare Holo GX", 1_500);
		values.put("Rare Holo Lv.X", 2_000);
		values.put("Rare BREAK", 2_500);
		values.put("Rare Prime", 3_000);
		values.put("LEGEND", 5_000);
		values.put("V", 7_500);
		values.put("Rare Promo", 10_000);
		values.put("Rare ACE", 12_500);
		values.put("Shining", 15_000);
		values.put("VM", 20_000);
		values.put("Rare Rainbow", 100_000);
		return values;
	}

	private void loadRoles() {
		if (RoleEnum.values().length != roleRepository.count()) {
			LOGGER.log(Level.INFO, "Loading Roles");
			for (RoleEnum roleEnum : RoleEnum.values()) {
				Role role = new Role();
				role.setId(roleEnum.toString());
				roleRepository.save(role);
			}
		}
	}

	private void createAdmin() {
		String adminString = "admin";
		Account admin = accountRepository.findByUsername(adminString);
		if (admin == null) {
			LOGGER.log(Level.INFO, "Creating Admin");
			admin = new Account();
			admin.setUsername(adminString);
			admin.setEmail("pokemonCardsAdmin@gmail.com");
			admin.setFirstName(adminString);
			admin.setLastName(adminString);
			admin.setPassword(adminString);
			admin.setEnabled(true);
			accountService.addAccount(admin);
			accountRepository.addRole(admin.getUsername(), admin.getEmail(), RoleEnum.ROLE_ADMIN.name());
			cashRepository.updateCoins(admin.getUsername(), 100_000_000);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadData();
		loadRoles();
		createAdmin();
//		for(int i =1; i<503; i++)
//			cardRepository.getCards(i, SortType.NAME, SortType.NAME.ASC, new ArrayList<Rarity>(), new ArrayList<Set>(), new ArrayList<Type>(), Optional.empty(), Optional.empty()).forEach(card->accountRepository.addCard("admin", "pokemonCardsAdmin@gmail.com", card.getId()));
	}

}