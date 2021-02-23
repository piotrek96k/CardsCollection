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
import com.pokemoncards.model.repository.account.AccountRepository;
import com.pokemoncards.model.repository.account.CashRepository;
import com.pokemoncards.model.repository.account.RoleRepository;
import com.pokemoncards.model.repository.card.CardRepository;
import com.pokemoncards.model.repository.card.RarityRepository;
import com.pokemoncards.model.repository.card.SetRepository;
import com.pokemoncards.model.repository.card.TypeRepository;
import com.pokemoncards.model.service.ApiService;
import com.pokemoncards.model.service.EmailService;
import com.pokemoncards.model.service.RegisterService;

@Component
public class DatabaseInitializer implements InitializingBean {

	private static final String ADMIN;

	private static final Logger LOGGER;

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
	private RegisterService registerService;

	@Autowired
	private ApiService apiService;

	static {
		ADMIN = "admin";
		LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
	}

	private void loadData() {
		if (apiService.getNumberOfCards() > cardRepository.count()) {
			LOGGER.log(Level.INFO, "Loading Data");
			loadObjects(apiService.getAllSets(), setRepository, Sets.Set::getName, Set::new, Optional.empty());
			loadObjects(apiService.getAllTypes(), typeRepository, type -> type, Type::new, Optional.empty());
			Map<String, Integer> values = getDefaultValues();
			List<String> rarities = new ArrayList<String>();
			for (int i = 1; i <= apiService.getNumberOfPages(); i++) {
				List<Cards.Card> cards = apiService.getCardsByPage(i);
				for (Cards.Card card : cards) {
					if (!rarities.contains(card.getRarity())) {
						rarities.add(card.getRarity());
						loadObjects(Collections.singletonList(card.getRarity()), rarityRepository, rarity -> rarity,
								Rarity::new, Optional.of(rarity -> setRarityValue(rarity, values)));
					}
				}
				loadCards(cards);
			}
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
		Account admin = accountRepository.findByUsername(ADMIN);
		if (admin == null) {
			LOGGER.log(Level.INFO, "Creating Admin");
			admin = new Account();
			admin.setUsername(ADMIN);
			admin.setEmail(EmailService.ADMIN_EMAIL);
			admin.setPassword(ADMIN);
			registerService.addAccount(admin);
			accountRepository.addRole(admin.getUsername(), admin.getEmail(), RoleEnum.ROLE_ADMIN.name());
			accountRepository.activateAccount(ADMIN);
			cashRepository.updateCoins(admin.getUsername(), 100_000_000);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadData();
		loadRoles();
		createAdmin();
	}

}