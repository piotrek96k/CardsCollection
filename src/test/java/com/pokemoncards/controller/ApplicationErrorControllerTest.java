package com.pokemoncards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.pokemoncards.model.repository.account.AccountRepository;
import com.pokemoncards.model.service.AccountService;

@WebMvcTest(controllers = ApplicationErrorController.class)
@Import(ControllerTestConfiguration.class)
public class ApplicationErrorControllerTest {

	@MockBean
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturnErrorPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/error"))
				.andExpect(MockMvcResultMatchers.view().name("errorpage"));
	}

}