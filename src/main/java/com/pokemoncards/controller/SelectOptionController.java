package com.pokemoncards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pokemoncards.model.session.Expander;
import com.pokemoncards.model.session.SessionData;

@Controller
public class SelectOptionController {
	
	@Autowired
	private SessionData sessionData;

	@PostMapping(value = "/expand")
	@ResponseStatus(value = HttpStatus.OK)
	private void expandSelection(@ModelAttribute(value = "expand") String expand) {
		for (Expander expander : Expander.values())
			if (expander.getExpand().equals(expand))
				sessionData.switchExpander(expander);
	}

	@PostMapping(value = "/scroll")
	@ResponseStatus(value = HttpStatus.OK)
	private void scrollPosition(@ModelAttribute(value = "scroll") double scroll) {
		sessionData.setScrollPosition(scroll);
	}
	
}
