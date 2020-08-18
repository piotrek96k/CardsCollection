package com.pokemoncards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pokemoncards.model.component.Expander;
import com.pokemoncards.model.component.SessionData;

@Controller
@Scope(value = "session")
public class SelectOptionController {
	
	@Autowired
	private SessionData sessionData;

	@PostMapping(value = "/expand")
	@ResponseStatus(value = HttpStatus.OK)
	public void expandSelection(@ModelAttribute(value = "expand") String expand) {
		for (Expander expander : Expander.values())
			if (expander.getExpand().equals(expand))
				sessionData.switchExpander(expander);
	}

	@PostMapping(value = "/scroll")
	@ResponseStatus(value = HttpStatus.OK)
	public void scrollPosition(@ModelAttribute(value = "scroll") int scroll) {
		sessionData.setScrollPosition(scroll);
	}
	
}
