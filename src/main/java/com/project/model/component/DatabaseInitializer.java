package com.project.model.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.model.api.Cards;
import com.project.model.api.Sets;
import com.project.model.entity.Account;
import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RarityRepository;
import com.project.model.repository.RoleRepository;
import com.project.model.repository.SetRepository;
import com.project.model.service.ApiService;

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
	private ApiService apiService;

	private void loadData() {
		if (apiService.getNumberOfCards() != cardRepository.count()) {
			LOGGER.log(Level.INFO, "Loading Data");
			ApiService.ApiData data = apiService.getApiData();
			loadRarities(data.getRarities());
			loadSets(data.getSets());
			loadCards(data.getCards());
			setRaritiesCost();
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
				cardRepository.save(card);
			}
	}
	
	private void loadSets(List<Sets.Set> sets) {
		for(Sets.Set apiSet: sets) {
			if(setRepository.findById(apiSet.getName()).isEmpty()) {
				com.project.model.entity.Set set = new com.project.model.entity.Set();
				set.setId(apiSet.getName());
				setRepository.save(set);
			}
		}
	}

	private void loadRarities(Set<String> rarities) {
		for (String apiRarity : rarities)
			if (rarityRepository.findById(apiRarity).isEmpty()) {
				Rarity rarity = new Rarity();
				rarity.setId(apiRarity);
				rarityRepository.save(rarity);
			}
	}

	private void setRaritiesCost() {
		Map<String, Integer> costs = getDefaultCosts();
		List<Rarity> rarities = rarityRepository.findAllRaritiesOrderById();
		for (Rarity rarity : rarities)
			if (rarity.getCost() == 0) {
				int cost = costs.get(rarity.getId()) == null ? 25_000 : costs.get(rarity.getId());
				rarityRepository.setRarityCost(rarity.getId(), cost);
			}
	}

	private Map<String, Integer> getDefaultCosts() {
		Map<String, Integer> costs = new HashMap<String, Integer>();
		costs.put("Common", 100);
		costs.put("Uncommon", 150);
		costs.put("Rare", 250);
		costs.put("Rare Holo", 300);
		costs.put("Rare Ultra", 500);
		costs.put("Rare Holo EX", 750);
		costs.put("Rare Secret", 1_000);
		costs.put("Rare Holo GX", 1_500);
		costs.put("Rare Holo Lv.X", 2_000);
		costs.put("Rare BREAK", 2_500);
		costs.put("Rare Prime", 3_000);
		costs.put("LEGEND", 5_000);
		costs.put("V", 7_500);
		costs.put("Rare Promo", 10_000);
		costs.put("Rare ACE", 12_500);
		costs.put("Shining", 15_000);
		costs.put("VM", 20_000);
		costs.put("Rare Rainbow", 100_000);
		return costs;
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
			admin.setEmail("cardsCollectorsAdmin@gmail.com");
			admin.setFirstName(adminString);
			admin.setLastName(adminString);
			admin.setPassword(new BCryptPasswordEncoder().encode(adminString));
			admin.setEnabled(true);
			admin.setCoins(1_000_000);
			List<Role> roles = new ArrayList<Role>();
			for (RoleEnum roleEnum : RoleEnum.values())
				roles.add(roleRepository.getOne(roleEnum.toString()));
			admin.setRoles(roles);
			accountRepository.save(admin);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadData();
		loadRoles();
		createAdmin();
//		for(int i =1; i<3; i++)
//			cardRepository.getCardsByPageOrderByName(i).forEach(card->accountRepository.addCard("admin", "cardsCollectorsAdmin@gmail.com", card.getId()));
	}

}