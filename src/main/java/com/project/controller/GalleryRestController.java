package com.project.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.service.AccountService;

@RestController
public class GalleryRestController {

	@Autowired
	private AccountService accountService;

	@GetMapping("gallery/buy")
	public String test(@RequestParam(required = true) String id) {
		accountService.addCard(id);
		JsonObject json = Json.createObjectBuilder().add("coins", getFormattedCoins(accountService.getCoins()))
				.add("quantity", accountService.countUserCardsByCardId(id)).build();
		return json.toString();
	}

	private String getFormattedCoins(int coins) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(coins);
	}
	
}
