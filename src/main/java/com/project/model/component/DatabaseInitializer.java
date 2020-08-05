package com.project.model.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.model.api.Cards;
import com.project.model.entity.Account;
import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RarityRepository;
import com.project.model.repository.RoleRepository;
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
	private ApiService apiService;

	private void loadData() {
		if (apiService.getNumberOfCards() != cardRepository.count()) {
			LOGGER.log(Level.INFO, "Loading Data");
			ApiService.ApiData data = apiService.getApiData();
			loadRarities(data.getRarities());
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
				card.setCost(100);
				cardRepository.save(card);
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
			admin.setCoins(-1);
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