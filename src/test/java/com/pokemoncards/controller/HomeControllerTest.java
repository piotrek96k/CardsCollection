package com.pokemoncards.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.repository.account.AccountRepository;
import com.pokemoncards.model.repository.card.CardRepository;
import com.pokemoncards.model.repository.card.SetRepository;
import com.pokemoncards.model.service.AccountService;
import com.pokemoncards.model.service.CardService;

@WebMvcTest(controllers = HomeController.class)
@Import(ControllerTestConfiguration.class)
public class HomeControllerTest {

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private CardRepository cardRepository;

	@MockBean
	private SetRepository setRepository;

	@MockBean
	private AccountService accountService;

	@MockBean
	private CardService cardService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturnHomePage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/home")).andExpect(MockMvcResultMatchers.view().name("home"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldRedirectToHomePageTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.redirectedUrl("/home"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}

	@Test
	public void shouldCashAttributeNotExists() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/home"))
				.andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("cash"));
	}

	@Test
	public void shouldReturnFreeCard() throws Exception {
		Card card = new Card();
		card.setId("test-id");
		Mockito.when(accountService.collectFreeCard()).thenReturn(card);
		mockMvc.perform(MockMvcRequestBuilders.post("/home/collect/freecard"))
				.andExpect(MockMvcResultMatchers.view().name("freecard"))
				.andExpect(MockMvcResultMatchers.model().attribute("card", card))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturnFreeCardJson() throws Exception {
		String json = "{\"nextFreeCard\":1599841425609};";
		Mockito.when(accountService.getFreeCardAsJson()).thenReturn(json);
		mockMvc.perform(MockMvcRequestBuilders.get("/home/get/freecard"))
				.andExpect(MockMvcResultMatchers.content().json(json)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturnFreeCoins() throws Exception {
		String json = "{\"coins\":\"2 999 450\",\"nextCoinsCollecting\":1599841429733,\"nextCoins\":\"200\"}";
		Mockito.when(accountService.getCashAsJson()).thenReturn(json);
		mockMvc.perform(MockMvcRequestBuilders.get("/home/get/cash"))
				.andExpect(MockMvcResultMatchers.content().json(json)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturnCashJson() throws Exception {
		String json = "{\"coins\":\"2 999 450\",\"nextCoinsCollecting\":1599841429733,\"nextCoins\":\"200\"}";
		Mockito.when(accountService.getCashAsJson()).thenReturn(json);
		mockMvc.perform(MockMvcRequestBuilders.get("/home/get/cash"))
				.andExpect(MockMvcResultMatchers.content().json(json)).andExpect(MockMvcResultMatchers.status().isOk());
	}

}